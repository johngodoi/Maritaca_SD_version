package br.unifesp.maritaca.business.login.dao;

import java.util.List;

import br.unifesp.maritaca.business.base.BaseDAO;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.InvalidNumberOfEntries;

public class LoginDAO extends BaseDAO {
	
	public User findUserByEmail(String email){
		List<User> users = getEntityManager().cQuery(User.class, "email", email);
		if (users == null || users.size() == 0) {
			return null;
		} else if ( users.size() == 1 ){
			return users.get(0);
		} else {
			throw new InvalidNumberOfEntries(email, User.class);
		}
	}

}
