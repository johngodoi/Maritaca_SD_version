package br.unifesp.maritaca.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public interface EntityManager {
	<T> boolean persist(T obj)throws IllegalArgumentException,
	IllegalAccessException, SecurityException, NoSuchMethodException,
	InvocationTargetException ;
	
	<T> boolean persist(T obj, boolean createTable)throws IllegalArgumentException,
	IllegalAccessException, SecurityException, NoSuchMethodException,
	InvocationTargetException ;
	
	<T> T find(Class<T> cl, UUID uuid)throws InstantiationException,
	IllegalAccessException, IllegalArgumentException,
	SecurityException, InvocationTargetException, NoSuchMethodException;
	
	<T>boolean delete(T obj) throws IllegalArgumentException, SecurityException, 
	IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	
	<T> List<T> cQuery(Class<T> cl, String field, String value)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			NoSuchFieldException;
	
	<T> List<T> listAll(Class<T> cl)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			NoSuchFieldException;
	
	<T> boolean createTable(Class<T> cl)throws Exception;
	<T> boolean tableExists(Class<T> cl)throws Exception;
	<T> boolean dropTable(Class<T> cl)throws Exception;
}
