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
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

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
			throws IllegalArgumentException {
		if (obj == null) {
			throw new IllegalArgumentException("parameter cannot be null");
		}
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

		UUID key;
		Method method = getMethod(obj, "getKey");
		try {
			key = (UUID) method.invoke(obj);
		} catch (IllegalAccessException e1) {
			throw new RuntimeException("Exception while getting uuid", e1);
		} catch (InvocationTargetException e1) {
			throw new RuntimeException("Exception while getting uuid", e1);
		}

		if (key == null) {
			key = getUUID(obj);
			method = getMethod(obj, "setKey", UUID.class);
			try {
				method.invoke(obj, key);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}

		}

		for (Field f : obj.getClass().getDeclaredFields()) {
			// TODO check annotation Column
			if (!f.getName().equals("key")) {
				method = getMethod(obj, "get" + toUpperFirst(f.getName()));
				Object result;
				try {
					result = method.invoke(obj);
				} catch (IllegalAccessException e1) {
					throw new RuntimeException(
							"Exception while executing method "
									+ method.getName(), e1);
				} catch (InvocationTargetException e1) {
					throw new RuntimeException(
							"Exception while executing method "
									+ method.getName(), e1);
				}

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
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public <T> T find(Class<T> cl, UUID uuid) {

		Collection<String> fields = getNameFields(cl);
		SliceQuery<UUID, String, String> query = createSliceQuery(keyspace,
				uuidSerializer, stringSerializer, stringSerializer);
		query.setColumnFamily(cl.getSimpleName());
		query.setKey(uuid);
		query.setColumnNames(fields.toArray(new String[fields.size()]));

		QueryResult<ColumnSlice<String, String>> qresult = query.execute();

		boolean ghost = true;

		T obj;
		try {
			obj = (T) cl.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while instantiating "
					+ cl.getSimpleName(), e);
		}

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
			try {
				getMethod(obj, "setKey", UUID.class).invoke(obj, uuid);
				return obj;
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public <T> boolean delete(T obj) throws IllegalArgumentException {
		Mutator<UUID> mutator = createMutator(keyspace, uuidSerializer);

		UUID uid;
		try {
			uid = (UUID) getMethod(obj, "getKey").invoke(obj);
			mutator.addDeletion(uid, obj.getClass().getSimpleName());
			mutator.execute();
		} catch (IllegalAccessException e1) {
			throw new RuntimeException("Exception while setting uuid", e1);
		} catch (InvocationTargetException e1) {
			throw new RuntimeException("Exception while setting uuid", e1);
		}

		return true;
	}

	@Override
	public <T> List<T> cQuery(Class<T> cl, String field, String value)
			throws IllegalArgumentException {

		List<T> result = new ArrayList<T>();

		IndexedSlicesQuery<UUID, String, String> indexedSlicesQuery = createIndexedSlicesQuery(
				keyspace, uuidSerializer, stringSerializer, stringSerializer);
		indexedSlicesQuery.addEqualsExpression(field, value);
		indexedSlicesQuery.setColumnNames(getNameFields(cl));
		indexedSlicesQuery.setColumnFamily(cl.getSimpleName());

		QueryResult<OrderedRows<UUID, String, String>> resultq = indexedSlicesQuery
				.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj;
			try {
				obj = (T) cl.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Exception while instantiating "
						+ cl.getSimpleName(), e);
			}
			Method method = getMethod(obj, "setKey", UUID.class);
			try {
				method.invoke(obj, line.getKey());
			} catch (IllegalAccessException e1) {
				throw new RuntimeException("Exception while setting uuid", e1);
			} catch (InvocationTargetException e1) {
				throw new RuntimeException("Exception while setting uuid", e1);
			}

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
	public <T> List<T> listAll(Class<T> cl) throws IllegalArgumentException {

		List<T> result = new ArrayList<T>();

		Collection<String> fields = getNameFields(cl);
		RangeSlicesQuery<UUID, String, String> q = createRangeSlicesQuery(
				keyspace, uuidSerializer, stringSerializer, stringSerializer);
		q.setColumnFamily(cl.getSimpleName());
		q.setColumnNames(fields.toArray(new String[fields.size()]));

		QueryResult<OrderedRows<UUID, String, String>> resultq = q.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj;
			try {
				obj = (T) cl.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Exception while instantiating "
						+ cl.getSimpleName(), e);
			}
			Method method = getMethod(obj, "setKey", UUID.class);
			try {
				method.invoke(obj, line.getKey());
			} catch (IllegalAccessException e1) {
				throw new RuntimeException("Exception while setting uuid", e1);
			} catch (InvocationTargetException e1) {
				throw new RuntimeException("Exception while setting uuid", e1);
			}
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
	public <T> List<T> listAllMinimal(Class<T> cl)
			throws IllegalArgumentException {

		List<T> result = new ArrayList<T>();

		Collection<String> fields = getNameFields(cl);
		RangeSlicesQuery<UUID, String, String> q = createRangeSlicesQuery(
				keyspace, uuidSerializer, stringSerializer, stringSerializer);
		q.setColumnFamily(cl.getSimpleName()).setReturnKeysOnly();

		QueryResult<OrderedRows<UUID, String, String>> resultq = q.execute();

		for (Row<UUID, String, String> line : resultq.get().getList()) {
			T obj;
			try {
				obj = (T) cl.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Exception while instantiating "
						+ cl.getSimpleName(), e);
			}
			Method method = getMethod(obj, "setKey", UUID.class);
			try {
				method.invoke(obj, line.getKey());
			} catch (IllegalAccessException e1) {
				throw new RuntimeException("Exception while setting uuid", e1);
			} catch (InvocationTargetException e1) {
				throw new RuntimeException("Exception while setting uuid", e1);
			}
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

	private <T> Method getMethod(T obj, String methodName, Class... parameter) {
		try {
			return obj.getClass().getDeclaredMethod(methodName, parameter);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("no method : " + methodName, e);
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
			else {
				try {
					// alternative method with String
					method = getMethod(result, "set"
							+ toUpperFirst(f.getName()), String.class);
					method.invoke(result, value);
				} catch (Exception e) {
					throw new RuntimeException(
							"data cannot be mapped from database to class", e);
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

	public void addHost(String host, int port) {
		CassandraHost cassandraHost = new CassandraHost(host, port);
		cluster.addHost(cassandraHost, true);
	}

	@Override
	public <T> boolean createTable(Class<T> cl) {
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
		if (tableExists(cl)) {
			cluster.dropColumnFamily(keyspace.getKeyspaceName(),
					cl.getSimpleName());
			return true;
		}
		return false;
	}

	@Override
	public <T> boolean persist(T obj) throws IllegalArgumentException {
		return persist(obj, true);
	}

}
