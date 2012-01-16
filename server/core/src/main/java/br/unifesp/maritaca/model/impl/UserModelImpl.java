package br.unifesp.maritaca.model.impl;

import java.util.Collection;
import java.util.UUID;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;

public class UserModelImpl implements UserModel {
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

	/**
	 * Returns the first user with the given email address.
	 * Email addresses should be unique in the database.
	 * @param email Address of the searched user. 
	 * @return The user found.
	 */
	@Override
	public User getUser(String email) {
		if (entityManager == null)
			return null;
		return entityManager.cQuery(User.class, "email", email).get(0);
	}

}
