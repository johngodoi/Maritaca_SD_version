package br.unifesp.maritaca.persistence;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;
import static me.prettyprint.hector.api.factory.HFactory.createColumnFamilyDefinition;
import static me.prettyprint.hector.api.factory.HFactory.createIndexedSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;
import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.unifesp.maritaca.persistence.annotation.Column;
import br.unifesp.maritaca.persistence.annotation.Index;
import br.unifesp.maritaca.persistence.annotation.JSONValue;
import br.unifesp.maritaca.persistence.annotation.Minimal;
import br.unifesp.maritaca.persistence.util.UtilsPersistence;

public class EntityManagerHectorImpl implements EntityManager, Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(EntityManagerHectorImpl.class);
	private static EntityManagerHectorImpl instance;

	private Cluster cluster;
	private Keyspace keyspace;
	private final StringSerializer stringSerializer = StringSerializer.get();
	private final UUIDSerializer uuidSerializer = UUIDSerializer.get();

	private EntityManagerHectorImpl(Cluster c, Keyspace k) {
		cluster = c;
		keyspace = k;
	}

	public static EntityManagerHectorImpl getInstance(Cluster c, Keyspace k) {
		if (instance == null) {
			instance = new EntityManagerHectorImpl(c, k);
		}
		return instance;
	}

	@Override
	public <T> boolean persist(T obj) {
		return persist(obj, true);
	}

	@Override
	public <T> boolean persist(T obj, boolean createTable) {

		if (obj == null || !isEntity(obj.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}

		if (!columnFamilyExists(obj.getClass()) && createTable) {
			try {
				createColumnFamily(obj.getClass());
			} catch (Exception e) {
				log.error(e);
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
			Method method = getMethod(obj, "get" + UtilsPersistence.toUpperFirst(f.getName()));
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
				if(f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).multi()){
					if(!(result instanceof List)){
						throw new RuntimeException("Result is not a list: "+result.getClass().getName());
					}
					List resultList = (List) result;
					HColumn column; 
					int i = 0;
					for(Object listObj : resultList){
						column = getHColumn(f.getName() + i, listObj);
						i++;
						saveColumn(obj, mutator, key, f, column);
					}
					
				} else {
					HColumn column = getHColumn(f.getName(), result);
					
					if(f.isAnnotationPresent(JSONValue.class)){
						Gson gson = new Gson();
						result = gson.toJson(result);					
					}
					
					saveColumn(obj, mutator, key, f, column);
				}
			}
		}

		try {
			mutator.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	private void saveColumn(Object obj, Mutator<UUID> mutator, UUID key, Field f,
			HColumn column) {
		int timeToLive = (f.getAnnotation(Column.class)).ttl();				
		if(timeToLive>0) {					
			column.setTtl(timeToLive);
		}		
		mutator.addInsertion(key, obj.getClass().getSimpleName(), column);
	}

	private HColumn getHColumn(String columnname, Object obj) {
		// TODO select serializer by object type
		// Serializer serializer = getObjectSerializer(obj);
		// if(serializer == null || serializer instanceof ObjectSerializer){
		// //serializer not found, setting default serializer to
		// stringserializer
		// obj = obj.toString();
		// serializer = stringSerializer;
		// }

		Serializer serializer = stringSerializer;
		obj = obj.toString();

		HColumn column = createColumn(columnname, obj, stringSerializer,
				serializer);
		return column;
	}

	// @SuppressWarnings({ "rawtypes" })
	// private <T> Serializer getObjectSerializer(T obj){
	// Serializer serializer = null;
	// serializer = SerializerTypeInferer.getSerializer(obj);
	// return serializer;
	// }

	@Override
	public <T> T find(Class<T> cl, UUID uuid, boolean justMinimal) {
		if (cl == null || !isEntity(cl)) {
			throw new IllegalArgumentException("object null or not an entity");
		}

		Collection<String> fields = getNameFields(cl, justMinimal);
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

	public <T> List<T> cRangeQuery(Class<T> cl, String field, String value) {
		RangeSlicesQuery<String, String, String> rangeSlicesQuery;
		rangeSlicesQuery = createRangeSlicesQuery(keyspace, stringSerializer,
				stringSerializer, stringSerializer);
		rangeSlicesQuery.setColumnFamily(cl.getSimpleName());
		rangeSlicesQuery.setRange(value, "", false, 3);

		QueryResult<OrderedRows<String, String, String>> resultq;
		resultq = rangeSlicesQuery.execute();

		List<T> result = new ArrayList<T>();

		for (Row<String, String, String> line : resultq.get().getList()) {
			T obj = instantiateObject(cl);
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

			log.debug(line);
		}
		return result;
	}

	@Override
	public <T> List<T> cQuery(Class<T> cl, String field, String value,
			boolean justMinimal) {
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
			addObjectFromRowLine(cl, false, line, result);
		}
		return result;
	}

	@Override
	public <T> List<T> listAll(Class<T> cl, boolean justMinimal) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}

		boolean keysOnly = false;

		List<T> result = new ArrayList<T>();

		Collection<String> fields = getNameFields(cl, justMinimal);
		RangeSlicesQuery<UUID, String, String> q = createRangeSlicesQuery(
				keyspace, uuidSerializer, stringSerializer, stringSerializer);
		q.setColumnFamily(cl.getSimpleName());
		if (fields.size() > 0) {
			q.setColumnNames(fields.toArray(new String[fields.size()]));
		} else {
			q.setReturnKeysOnly();
			keysOnly = true;
		}

		QueryResult<OrderedRows<UUID, String, String>> resultq = q.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			addObjectFromRowLine(cl, keysOnly, line, result);
		}
		return result;
	}

	private <T> void addObjectFromRowLine(Class<T> cl, boolean keysOnly,
			Row<UUID, String, String> line, List<T> result) {
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

	public void addHost(String host, int port) {
		CassandraHost cassandraHost = new CassandraHost(host, port);
		cluster.addHost(cassandraHost, true);
	}

	@Override
	public <T> boolean createColumnFamily(Class<T> cl) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}
		if (!columnFamilyExists(cl)) {

			List<ColumnDef> columns = new ArrayList<ColumnDef>();
			for (Field f : getColumnFields(cl, false)) {
				String cName = f.getName();
				// log.info("Creating column " + cName);
				if (f.getAnnotation(Column.class) != null && f.getAnnotation(Column.class).indexed())
					columns.add(newIndexedColumnDef(cName, cl.getSimpleName()
							+ cName, ComparatorType.UTF8TYPE.getTypeName()));
				// TODO temporal solution while all the entities will be migrate to the new annotation Column
				if (f.getAnnotation(Index.class) != null &&	f.getAnnotation(Index.class).indexed()) {
					columns.add(newIndexedColumnDef(cName, cl.getSimpleName()
							+ cName, ComparatorType.UTF8TYPE.getTypeName()));
				}
			}

			List<ColumnDefinition> columnMetadata = ThriftColumnDef
					.fromThriftList(columns);

			ColumnFamilyDefinition cfdef = createColumnFamilyDefinition(
					keyspace.getKeyspaceName(), cl.getSimpleName(),
					ComparatorType.UTF8TYPE, columnMetadata);
			cfdef.setKeyValidationClass(UUIDType.class.getSimpleName());

			cluster.addColumnFamily(cfdef);
			return true;
		}
		return false;
	}

	ColumnDef newIndexedColumnDef(String column_name, String indexName,
			String comparer) {
		ColumnDef cd = new ColumnDef(
				stringSerializer.toByteBuffer(column_name), comparer);
		cd.setIndex_name(indexName);
		cd.setIndex_type(IndexType.KEYS);
		return cd;
	}

	@Override
	public <T> boolean columnFamilyExists(Class<T> cl) {
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
	public <T> boolean dropColumnFamily(Class<T> cl) {
		if (cl == null || isEntity(cl.getClass())) {
			throw new IllegalArgumentException("object null or not an entity");
		}

		if (columnFamilyExists(cl)) {
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
		Method method = getMethod(result, "set" + UtilsPersistence.toUpperFirst(f.getName()),
				f.getType());
		try {
			if(f.isAnnotationPresent(JSONValue.class)){
				Gson gson = new Gson();
				Type type = new TypeToken<List<UUID>>(){}.getType();
				List<UUID> listJson = gson.fromJson(value, type);
				method.invoke(result, listJson);
			}else if (f.getType() == String.class)

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
			else if (f.getType() == Date.class) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm a");
				Date date;
				try {
					date = sdf.parse(value);
					method.invoke(result, date);
				} catch (ParseException e) {
					log.error("date not set", e);
				}
			} else {
				try {
					// alternative method with String
					method = getMethod(result, "set"
							+ UtilsPersistence.toUpperFirst(f.getName()), String.class);
					method.invoke(result, value);
				} catch (Exception e) {
					log.error("Losing data, method set" + f.getName()
							+ "not found in class "
							+ result.getClass().getSimpleName(), e);
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
			if (!f.isAnnotationPresent(Id.class) 
					&& f.isAnnotationPresent(javax.persistence.Column.class)) {
				if (!justMinimal || f.isAnnotationPresent(Minimal.class)) {
					colFields.add(f);
				}
			}
		}
		return colFields;
	}

	private <T> boolean isEntity(Class<T> cl) {
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
			log.error("not possible to close the connection with cassandra", e);
		}

	}

	// To support IDs with different names than "key"
	// private <T> String getKeyName(Class<T> cl){
	// for(Field f : cl.getDeclaredFields()){
	// if(f.isAnnotationPresent(Id.class)){
	// return f.getName();
	// }
	// }
	// throw new IllegalArgumentException("Class " + cl.getName() +
	// " does not have ID");
	// }

}
