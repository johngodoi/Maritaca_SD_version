package br.unifesp.maritaca.persistence;

import java.util.List;
import java.util.UUID;

public interface EntityManager {
	<T> boolean persist(T obj);

	<T> boolean persist(T obj, boolean createTable);

	<T> T find(Class<T> cl, UUID uuid);

	<T> boolean delete(T obj);

	<T> List<T> cQuery(Class<T> cl, String field, String value);

	<T> List<T> listAll(Class<T> cl);

	<T> List<T> listAllMinimal(Class<T> cl);

	<T> boolean createTable(Class<T> cl);

	<T> boolean tableExists(Class<T> cl);

	<T> boolean dropTable(Class<T> cl);
}
