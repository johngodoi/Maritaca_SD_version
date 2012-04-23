package br.unifesp.maritaca.business.base.dao;

import java.util.UUID;

import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.permission.Rule;

public class BaseDAO {

	protected EntityManager entityManager;
	protected Rule rules = Rule.getInstance();	

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
	
	public Boolean isRootUser(UUID userKey) {
		return true;
	}
}
