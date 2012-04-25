package br.unifesp.maritaca.business.base.dao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class BaseDAO {

	protected EntityManager entityManager;

	private static final Log log = LogFactory.getLog(BaseDAO.class);
	
	public BaseDAO() {
		setEntityManager(EntityManagerFactory.getInstance().createEntityManager(
				EntityManagerFactory.HECTOR_MARITACA_EM));
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	protected <T> List<T> objectsStartingWith(Class<T> cl, String startingStr,
			String methodName) {
		ArrayList<T> result = new ArrayList<T>(0);
		try {
			// TODO: improve this. Retrieving all elements in a column family
			// is expensive with big collections.
			Method method = cl.getMethod(methodName);
			List<T> resultEM = entityManager.listAll(cl, true);
			for (T obj : resultEM) {

				String value = (String) method.invoke(obj);
				if (value != null && value.matches("^" + startingStr + ".*")) {
					result.add(obj);
				}
			}
		} catch (Exception e) {
			log.error("Exception executing the method " + methodName
					+ " in the class " + cl.getSimpleName());
		}
		return result;
	}
}
