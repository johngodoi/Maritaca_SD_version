package br.unifesp.maritaca.business.base.dao;

import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class BaseDAO {

	private EntityManager entityManager;

	public BaseDAO() {
		setEntityManager(EntityManagerFactory.getInstance().createEntityManager(
				EntityManagerFactory.HECTOR_MARITACA_EM));
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
