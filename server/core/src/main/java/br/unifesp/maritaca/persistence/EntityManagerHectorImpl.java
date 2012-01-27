package br.unifesp.maritaca.persistence;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;
import static me.prettyprint.hector.api.factory.HFactory.createColumnFamilyDefinition;
import static me.prettyprint.hector.api.factory.HFactory.createIndexedSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;
import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.ThriftColumnDef;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

import org.apache.cassandra.db.marshal.UUIDType;
import org.apache.cassandra.thrift.ColumnDef;
import org.apache.cassandra.thrift.IndexType;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

public class EntityManagerHectorImpl implements EntityManager {
	private static EntityManagerHectorImpl instance;

	private Cluster cluster;
	private Keyspace keyspace;
	private final StringSerializer stringSerializer = StringSerializer.get();
	private final UUIDSerializer uuidSerializer = UUIDSerializer.get();

	private EntityManagerHectorImpl(Cluster c, Keyspace k) {
		cluster = c;
		keyspace = k;
	}
	
	public static EntityManagerHectorImpl getInstance(Cluster c, Keyspace k){
		if(instance == null){
			instance = new EntityManagerHectorImpl(c, k);
		}
		return instance;
	}

	@Override
	public <T> boolean persist(T obj) {
		return persist(obj, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> boolean persist(T obj, boolean createTable) {
		
		if (obj == null || !isEntity(obj.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}
		
		if (!tableExists(obj.getClass()) && createTable) {
			try {
				createTable(obj.getClass());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		Mutator<UUID> mutator = createMutator(keyspace, uuidSerializer);

		UUID key = getObjectKey(obj);

		if (key == null) {
			key = getUUID(obj);
			setObjectKey(obj, key);
		}

		for (Field f : getColumnFields(obj.getClass(), false)) {
			Method method = getMethod(obj, "get" + toUpperFirst(f.getName()));
			Object result;
			try {
				result = method.invoke(obj);
			} catch (IllegalAccessException e1) {
				throw new RuntimeException("Exception while executing method "
						+ method.getName(), e1);
			} catch (InvocationTargetException e1) {
				throw new RuntimeException("Exception while executing method "
						+ method.getName(), e1);
			}

			if (result != null) {
				
				HColumn column = getHColumn(f.getName(), result);
				mutator.addInsertion(key, obj.getClass().getSimpleName(),
						column);
			}
		}

		try {
			mutator.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private HColumn getHColumn(String columnname, Object obj){
		//TODO select serializer by object type
//		Serializer serializer = getObjectSerializer(obj);
//		if(serializer == null || serializer instanceof ObjectSerializer){
//			//serializer not found, setting default serializer to stringserializer
//			obj = obj.toString();
//			serializer = stringSerializer;
//		}
		
		Serializer serializer = stringSerializer;
		obj = obj.toString();
		
		HColumn column = createColumn(columnname,
				obj, stringSerializer, serializer);
		return column;
	}

//	@SuppressWarnings({ "rawtypes" })
//	private <T> Serializer getObjectSerializer(T obj){
//		Serializer serializer = null;
//		serializer = SerializerTypeInferer.getSerializer(obj);
//		return serializer;
//	}
	
	
	@Override
	public <T> T find(Class<T> cl, UUID uuid, boolean justMinimal) {
		
		if (cl == null || !isEntity(cl)) {
			throw new IllegalArgumentException("object null or not an entity");
		}

		Collection<String> fields = getNameFields(cl,justMinimal);
		SliceQuery<UUID, String, String> query = createSliceQuery(keyspace,
				uuidSerializer, stringSerializer, stringSerializer);
		query.setColumnFamily(cl.getSimpleName());
		query.setKey(uuid);
		query.setColumnNames(fields.toArray(new String[fields.size()]));

		QueryResult<ColumnSlice<String, String>> qresult = query.execute();

		boolean ghost = true;

		T obj = instantiateObject(cl);

		for (HColumn<String, String> column : qresult.get().getColumns())

		{
			try {
				setValue(obj, cl.getDeclaredField(column.getName()),
						column.getValue());
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}

			ghost = false;
		}

		if (ghost)
			return null;
		else {
			setObjectKey(obj, uuid);
			return obj;
		}

	}

	@Override
	public <T> boolean delete(T obj) throws IllegalArgumentException {
		if (obj == null || !isEntity(obj.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}
		
		Mutator<UUID> mutator = createMutator(keyspace, uuidSerializer);

		UUID uuid = getObjectKey(obj);
		mutator.addDeletion(uuid, obj.getClass().getSimpleName());
		mutator.execute();

		return true;
	}

	@Override
	public <T> List<T> cQuery(Class<T> cl, String field, String value, boolean justMinimal) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}

		List<T> result = new ArrayList<T>();

		IndexedSlicesQuery<UUID, String, String> indexedSlicesQuery = createIndexedSlicesQuery(
				keyspace, uuidSerializer, stringSerializer, stringSerializer);
		indexedSlicesQuery.addEqualsExpression(field, value);
		indexedSlicesQuery.setColumnNames(getNameFields(cl, justMinimal));
		indexedSlicesQuery.setColumnFamily(cl.getSimpleName());

		QueryResult<OrderedRows<UUID, String, String>> resultq = indexedSlicesQuery
				.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj = instantiateObject(cl);
			setObjectKey(obj, line.getKey());

			for (HColumn<String, String> column : line.getColumnSlice()
					.getColumns()) {
				try {
					setValue(obj, cl.getDeclaredField(column.getName()),
							column.getValue());
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				} catch (NoSuchFieldException e) {
					throw new IllegalArgumentException(e);
				}
			}

			result.add(obj);
		}
		return result;
	}

	@Override
	public <T> List<T> listAll(Class<T> cl, boolean justMinimal) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}
		
		boolean keysOnly=false;

		List<T> result = new ArrayList<T>();

		Collection<String> fields = getNameFields(cl, justMinimal);
		RangeSlicesQuery<UUID, String, String> q = createRangeSlicesQuery(
				keyspace, uuidSerializer, stringSerializer, stringSerializer);
		q.setColumnFamily(cl.getSimpleName());
		if(fields.size()>0){
			q.setColumnNames(fields.toArray(new String[fields.size()]));
		}else{
			q.setReturnKeysOnly();
			keysOnly = true;
		}

		QueryResult<OrderedRows<UUID, String, String>> resultq = q.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj = instantiateObject(cl);
			setObjectKey(obj, line.getKey());
			boolean ghost = true;
			for (HColumn<String, String> column : line.getColumnSlice()
					.getColumns()) {
				try {
					setValue(obj, cl.getDeclaredField(column.getName()),
							column.getValue());
					ghost = false;
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				} catch (NoSuchFieldException e) {
					throw new IllegalArgumentException(e);
				}
			}
			if (!ghost || keysOnly) {
				result.add(obj);
			}
		}
		return result;
	}

	public void addHost(String host, int port) {
		CassandraHost cassandraHost = new CassandraHost(host, port);
		cluster.addHost(cassandraHost, true);
	}

	@Override
	public <T> boolean createTable(Class<T> cl) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}
		if (!tableExists(cl)) {
			
			List<ColumnDef> columns = new ArrayList<ColumnDef>();
		    for (Field f : getColumnFields(cl, false)) {
		      String cName = f.getName();
		      //log.info("Creating column " + cName);
		      if(f.getAnnotation(Column.class).indexed())
		    	  columns.add(newIndexedColumnDef(cName, cl.getSimpleName() + cName, ComparatorType.UTF8TYPE.getTypeName()));
		    }

		    List<ColumnDefinition> columnMetadata = ThriftColumnDef
		        .fromThriftList(columns);
		    
			ColumnFamilyDefinition cfdef = createColumnFamilyDefinition(
					keyspace.getKeyspaceName(), 
					cl.getSimpleName(), 
					ComparatorType.UTF8TYPE,
					columnMetadata);
			cfdef.setKeyValidationClass(UUIDType.class.getSimpleName());

			
			cluster.addColumnFamily(cfdef);
			return true;
		}
		return false;
	}
	
	ColumnDef newIndexedColumnDef(String column_name, String indexName, String comparer) {
	    ColumnDef cd = new ColumnDef(stringSerializer.toByteBuffer(column_name), comparer);
	    cd.setIndex_name(indexName);
	    cd.setIndex_type(IndexType.KEYS);
	    return cd;
	  }

	@Override
	public <T> boolean tableExists(Class<T> cl) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}
		KeyspaceDefinition ksdef = cluster.describeKeyspace(keyspace
				.getKeyspaceName());
		if (ksdef == null) {
			return false;
		}
		for (ColumnFamilyDefinition cfd : ksdef.getCfDefs()) {
			if (cfd.getName().equals(cl.getSimpleName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> boolean dropTable(Class<T> cl) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}
		
		if (tableExists(cl)) {
			cluster.dropColumnFamily(keyspace.getKeyspaceName(),
					cl.getSimpleName());
			return true;
		}
		return false;
	}

	@Override
	public <T> T find(Class<T> cl, UUID uuid) {
		return find(cl, uuid, false);
	}

	@Override
	public <T> List<T> cQuery(Class<T> cl, String field, String value) {
		return cQuery(cl, field, value, false);
	}

	@Override
	public <T> List<T> listAll(Class<T> cl) {
		return listAll(cl, false);
	}
	
	private <T> T instantiateObject(Class<T> cl) {
		T obj = null;
		try {
			obj = (T) cl.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while instantiating "
					+ cl.getSimpleName(), e);
		}
		return obj;
	}

	private <T> void setObjectKey(T obj, UUID uuid) {
		Method method = getMethod(obj, "setKey", UUID.class);
		try {
			method.invoke(obj, uuid);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Exception while setting key", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while setting key", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Exception while setting key", e);
		}

	}

	@SuppressWarnings("rawtypes")
	private Collection<String> getNameFields(Class cl, boolean justMinimal) {
		ArrayList<String> colFields = new ArrayList<String>();
		for (Field f : cl.getDeclaredFields()) {
			if (!f.isAnnotationPresent(Id.class)
					&& f.isAnnotationPresent(Column.class)) {
				if (!justMinimal || f.isAnnotationPresent(Minimal.class)) {
					colFields.add(f.getName());
				}
			}
		}
		return colFields;
	}

	@SuppressWarnings("rawtypes")
	private <T> Method getMethod(T obj, String methodName, Class... parameter) {
		try {
			return obj.getClass().getDeclaredMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("no method : " + methodName
					+ " in Class: " + obj.getClass().getSimpleName(), e);
		}
	}

	private <T> void setValue(T result, Field f, String value) {
		Method method = getMethod(result, "set" + toUpperFirst(f.getName()),
				f.getType());
		try {
			if (f.getType() == String.class)

				method.invoke(result, value);

			else if (f.getType() == Long.class)
				method.invoke(result, Long.parseLong(value));

			else if (f.getType() == Integer.class)
				method.invoke(result, Integer.parseInt(value));

			else if (f.getType() == Double.class)
				method.invoke(result, Double.parseDouble(value));

			else if (f.getType() == Float.class)
				method.invoke(result, Float.parseFloat(value));

			else if (f.getType() == BigDecimal.class)
				method.invoke(result, new BigDecimal(value));

			else if (f.getType() == Boolean.class)
				method.invoke(result, new Boolean(value));
			else if (f.getType() == UUID.class)
				method.invoke(result, UUID.fromString(value));
			if (f.getType() == Date.class){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yy hh:mm a");
				Date date;
				try {
					date = sdf.parse(value);
					method.invoke(result, date);
				} catch (ParseException e) {
					System.out.println("Date not set");
				}
			}
			else {
				try {
					// alternative method with String
					method = getMethod(result, "set"
							+ toUpperFirst(f.getName()), String.class);
					method.invoke(result, value);
				} catch (Exception e) {
					//TODO log de dados perdidos, campo nao encontrado
					e.printStackTrace();
				}
			}

		} catch (IllegalArgumentException e1) {
			throw new RuntimeException(e1);
		} catch (IllegalAccessException e1) {
			throw new RuntimeException(e1);
		} catch (InvocationTargetException e1) {
			throw new RuntimeException(e1);
		}

	}

	private String toUpperFirst(String valor) {
		StringBuilder result = new StringBuilder(valor);
		result.setCharAt(0, new String(Character.toString(result.charAt(0)))
				.toUpperCase().charAt(0));

		return result.toString();
	}

	private UUID getUUID(Object obj) {
		return TimeUUIDUtils.getUniqueTimeUUIDinMillis();
	}

	private <T> UUID getObjectKey(T obj) {
		UUID key = null;
		Method method = getMethod(obj, "getKey");
		try {
			key = (UUID) method.invoke(obj);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Exception while getting key", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while getting key", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Exception while getting key", e);
		}
		return key;
	}
	
	private <T> ArrayList<Field> getColumnFields(Class<T> cl,
			boolean justMinimal) {
		ArrayList<Field> colFields = new ArrayList<Field>();
		for (Field f : cl.getDeclaredFields()) {
			if (!f.isAnnotationPresent(Id.class)
					&& f.isAnnotationPresent(Column.class)) {
				if (!justMinimal || f.isAnnotationPresent(Minimal.class)) {
					colFields.add(f);
				}
			}
		}
		return colFields;
	}
	
	private <T> boolean isEntity(Class<T> cl){
		return cl.isAnnotationPresent(Entity.class);
	}

	@Override
	public <T> boolean rowDataExists(Class<T> cl, UUID uuid) {
		T t = find(cl, uuid);
		return t != null;
	}

	@Override
	public void close() {
		try {
			cluster.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
//	To support IDs with different names than "key"
//	private <T> String getKeyName(Class<T> cl){
//		for(Field f : cl.getDeclaredFields()){
//			if(f.isAnnotationPresent(Id.class)){
//				return f.getName();
//			}
//		}
//		throw new IllegalArgumentException("Class " + cl.getName() + " does not have ID");
//	}
	
}
