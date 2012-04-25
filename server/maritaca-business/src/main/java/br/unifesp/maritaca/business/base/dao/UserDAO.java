package br.unifesp.maritaca.business.base.dao;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.core.User;

public class UserDAO extends BaseDAO {
	
	public User findUserByEmail(String email) {
		List<User> users = entityManager.cQuery(User.class, "email", email);
		if (users == null || users.isEmpty()) {
			return null;
		} 
		else if (users.size() == 1) {
			return users.get(0);
		} 
		else {
			throw new InvalidNumberOfEntries(email, User.class);
		}
	}
	
	public User findUserByKey(UUID userKey) {
		return entityManager.find(User.class, userKey);
	}
	
	public Boolean userExists(UUID userKey) {
		return entityManager.rowDataExists(User.class, userKey);
	}
}