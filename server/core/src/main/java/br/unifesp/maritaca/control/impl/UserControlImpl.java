package br.unifesp.maritaca.control.impl;

import java.util.Collection;
import java.util.UUID;

import br.unifesp.maritaca.control.UserControl;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;

public class UserControlImpl implements UserControl {
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public boolean saveUser(User user) {
		if(user == null)return false;
		return entityManager.persist(user);
	}

	@Override
	public User getUser(UUID uuid) {
		if (entityManager == null)
			return null;

		return entityManager.find(User.class, uuid);
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}

	@Override
	public Collection<User> listAllUsers() {
		if (entityManager == null)
			return null;

		return entityManager.listAll(User.class);
	}

	@Override
	public Collection<User> listAllUsersMinimal() {
		if (entityManager == null)
			return null;

		return entityManager.listAll(User.class, true);
	}

}
