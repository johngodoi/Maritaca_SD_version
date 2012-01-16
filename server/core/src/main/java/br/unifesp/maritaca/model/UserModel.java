package br.unifesp.maritaca.model;

import java.util.Collection;
import java.util.UUID;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;

public interface UserModel {
	boolean saveUser(User user);

	User getUser(UUID uuid);
	
	User getUser(String email);

	void setEntityManager(EntityManager em);

	Collection<User> listAllUsers();

	Collection<User> listAllUsersMinimal();
}