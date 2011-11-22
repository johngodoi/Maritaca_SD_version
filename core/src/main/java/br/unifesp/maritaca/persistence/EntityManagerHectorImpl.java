package br.unifesp.maritaca.persistence;

import static me.prettyprint.hector.api.factory.HFactory.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.apache.cassandra.db.marshal.TimeUUIDType;

public class EntityManagerHectorImpl implements EntityManager {

	Cluster cluster;
	Keyspace keyspace;
	private final StringSerializer stringSerializer = StringSerializer.get();
	private final UUIDSerializer uuidSerializer = UUIDSerializer.get();

	public EntityManagerHectorImpl(Cluster c, Keyspace k) {
		cluster = c;
		keyspace = k;
	}

	@Override
	public <T> boolean persist(T obj, boolean createTable)
			throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException {
		if (!tableExists(obj.getClass()) && createTable) {
			try {
				createTable(obj.getClass());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		// TODO check annotation Entity
		Mutator<UUID> mutator = createMutator(keyspace, uuidSerializer);

		UUID key = (UUID) getMethod(obj, "getKey").invoke(obj);

		if (key == null) {
			key = getUUID(obj);
			getMethod(obj, "setKey", UUID.class).invoke(obj, key);
		}

		for (Field f : obj.getClass().getDeclaredFields()) {
			// TODO check annotation Column
			if (!f.getName().equals("key")) {
				Object result = getMethod(obj,
						"get" + toUpperFirst(f.getName())).invoke(obj);

				if (result != null) {
					HColumn<String, String> column = createColumn(f.getName(),
							result.toString(), stringSerializer,
							stringSerializer);
					mutator.addInsertion(key, obj.getClass().getSimpleName(),
							column);
				}
			}
		}
		
		try {
			mutator.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public <T> T find(Class<T> cl, UUID uuid) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			SecurityException, InvocationTargetException, NoSuchMethodException {

		T result = cl.newInstance();

		for (Annotation a : cl.getDeclaredAnnotations()) {
			System.out.println(a.toString());

		}

		for (Field f : cl.getDeclaredFields()) {
			ColumnQuery<UUID, String, String> columnQuery = createColumnQuery(
					keyspace, uuidSerializer, stringSerializer,
					stringSerializer);

			columnQuery.setColumnFamily(cl.getSimpleName());
			columnQuery.setKey(uuid);
			columnQuery.setName(f.getName());
			QueryResult<HColumn<String, String>> queryResult = columnQuery
					.execute();

			String value = null;

			if (!f.getName().equals("key")) {
				if (queryResult != null && queryResult.get() != null) {
					value = queryResult.get().getValue();
				}
			} else {
				value = uuid.toString();
			}

			if (value != null) {
				setValue(result, f, value);
			}

		}

		return result;
	}

	@Override
	public <T> boolean delete(T obj) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		Mutator<UUID> mutator = createMutator(keyspace, uuidSerializer);

		UUID uid = (UUID) getMethod(obj, "getKey").invoke(obj);
		mutator.addDeletion(uid, obj.getClass().getSimpleName());
		mutator.execute();
	
		return true;
	}

	@Override
	public <T> List<T> cQuery(Class<T> cl, String field, String value)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			NoSuchFieldException {

		List<T> result = new ArrayList<T>();

		IndexedSlicesQuery<UUID, String, String> indexedSlicesQuery = createIndexedSlicesQuery(
				keyspace, uuidSerializer, stringSerializer, stringSerializer);
		indexedSlicesQuery.addEqualsExpression(field, value);
		indexedSlicesQuery.setColumnNames(getNameFields(cl));
		indexedSlicesQuery.setColumnFamily(cl.getSimpleName());

		QueryResult<OrderedRows<UUID, String, String>> resultq = indexedSlicesQuery
				.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj = cl.newInstance();
			getMethod(obj, "setKey", UUID.class).invoke(obj, line.getKey());
			for (HColumn<String, String> column : line.getColumnSlice()
					.getColumns()) {
				setValue(obj, cl.getDeclaredField(column.getName()),
						column.getValue());
			}

			result.add(obj);
		}

		return result;
	}

	@Override
	public <T> List<T> listAll(Class<T> cl) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			NoSuchFieldException {

		List<T> result = new ArrayList<T>();
		
		Collection<String> fields = getNameFields(cl);
		RangeSlicesQuery<UUID, String,String> q = createRangeSlicesQuery(keyspace, uuidSerializer, stringSerializer, stringSerializer);
	    q.setColumnFamily(cl.getSimpleName());
	    q.setColumnNames(fields.toArray(new String[fields.size()]));

		
		QueryResult<OrderedRows<UUID, String, String>> resultq = q
				.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj = (T) cl.newInstance();
			getMethod(obj, "setKey", UUID.class).invoke(obj, line.getKey());
			for (HColumn<String, String> column : line.getColumnSlice()
					.getColumns()) {
				setValue(obj, cl.getDeclaredField(column.getName()),
						column.getValue());
			}

			result.add(obj);
		}

		return result;
	}
	
	@Override
	public <T> List<T> listAllMinimal(Class<T> cl) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			NoSuchFieldException {

		List<T> result = new ArrayList<T>();
		
		Collection<String> fields = getNameFields(cl);
		RangeSlicesQuery<UUID, String,String> q = createRangeSlicesQuery(keyspace, uuidSerializer, stringSerializer, stringSerializer);
	    q.setColumnFamily(cl.getSimpleName()).setReturnKeysOnly();

		
		QueryResult<OrderedRows<UUID, String, String>> resultq = q
				.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj = (T) cl.newInstance();
			getMethod(obj, "setKey", UUID.class).invoke(obj, line.getKey());
			result.add(obj);
		}

		return result;
	}

	private Collection<String> getNameFields(Class classe) {
		List<String> result = new ArrayList<String>();

		for (Field field : classe.getDeclaredFields()) {
			if (!field.getName().equals("key")) {
				result.add(field.getName());
			}
		}

		return result;
	}

	private <T> Method getMethod(T obj, String methodName, Class... parameter)
			throws SecurityException, NoSuchMethodException {
		return obj.getClass().getDeclaredMethod(methodName, parameter);
	}

	private <T> void setValue(T result, Field f, String value)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Method method = getMethod(result, "set" + toUpperFirst(f.getName()),
				f.getType());

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

	public void addHost(String host, int port) {
		CassandraHost cassandraHost = new CassandraHost(host, port);
		cluster.addHost(cassandraHost, true);
	}

	@Override
	public <T> boolean createTable(Class<T> cl) throws Exception {
		if (!tableExists(cl)) {
			ColumnFamilyDefinition cfdef = createColumnFamilyDefinition(
					keyspace.getKeyspaceName(), cl.getSimpleName());
			cfdef.setKeyValidationClass(TimeUUIDType.class.getSimpleName());
			cluster.addColumnFamily(cfdef);
			return true;
		}
		return false;
	}

	@Override
	public <T> boolean tableExists(Class<T> cl) {
		KeyspaceDefinition ksdef = cluster.describeKeyspace(keyspace
				.getKeyspaceName());
		if(ksdef==null){
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
	public <T> boolean dropTable(Class<T> cl) throws Exception {
		if (tableExists(cl)) {
			cluster.dropColumnFamily(keyspace.getKeyspaceName(),
					cl.getSimpleName());
			return true;
		}
		return false;
	}

	@Override
	public <T> boolean persist(T obj) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			InvocationTargetException {
		return persist(obj, true);
	}

}
