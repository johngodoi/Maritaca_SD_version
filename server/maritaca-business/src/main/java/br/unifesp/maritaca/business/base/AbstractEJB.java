package br.unifesp.maritaca.business.base;

import java.io.Serializable;

import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public abstract class AbstractEJB implements Serializable {

	private static final long serialVersionUID = 1L;

	private EntityManager entityManager;

	public AbstractEJB() {
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
