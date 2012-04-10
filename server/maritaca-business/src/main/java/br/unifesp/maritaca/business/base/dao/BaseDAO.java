package br.unifesp.maritaca.business.base.dao;

import br.unifesp.maritaca.persistence.EntityManager;

public class BaseDAO {

	private EntityManager em;

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
}
