package br.unifesp.maritaca.control.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import br.unifesp.maritaca.control.FormResponseController;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public class FormResponseCtrlImpl implements FormResponseController {
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public boolean saveForm(Form form) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		if (entityManager == null)
			return false;

		return entityManager.persist(form);
	}

	@Override
	public Form getForm(UUID uid) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (entityManager == null)
			return null;

		return entityManager.find(Form.class, uid);
	}

}
