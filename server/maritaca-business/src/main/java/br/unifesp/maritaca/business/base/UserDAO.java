package br.unifesp.maritaca.business.base;

import java.util.List;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.InvalidNumberOfEntries;

public class UserDAO extends BaseDAO {
	
	public User findUserByEmail(String email) {
		List<User> users = entityManager.cQuery(User.class, "email", email);
		if (users == null || users.size() == 0) {
			return null;
		} else if ( users.size() == 1 ) {
			return users.get(0);
		} else {
			throw new InvalidNumberOfEntries(email, User.class);
		}
	}

}
