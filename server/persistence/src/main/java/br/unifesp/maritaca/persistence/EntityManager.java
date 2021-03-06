package br.unifesp.maritaca.persistence;

import java.util.List;
import java.util.UUID;

public interface EntityManager {
	<T> boolean persist(T obj);

	<T> boolean persist(T obj, boolean createTable);

	<T> T find(Class<T> cl, UUID uuid);

	<T> boolean delete(T obj);

	<T> List<T> cQuery(Class<T> cl, String field, String value);
	
	<T> List<T> cRangeQuery(Class<T> cl, String field, String value);

	<T> List<T> listAll(Class<T> cl);

	<T> boolean createColumnFamily(Class<T> cl);

	<T> boolean columnFamilyExists(Class<T> cl);

	<T> boolean dropColumnFamily(Class<T> cl);

	<T> T find(Class<T> cl, UUID uuid, boolean justMinimal);

	<T> List<T> cQuery(Class<T> cl, String field, String value, boolean justMinimal)
			throws IllegalArgumentException;

	<T> List<T> listAll(Class<T> cl, boolean justMinimal);
	
	<T> boolean rowDataExists(Class<T> cl, UUID uuid);
	
	void close();
}
