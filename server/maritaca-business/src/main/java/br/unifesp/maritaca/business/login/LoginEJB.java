package br.unifesp.maritaca.business.login;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
public class LoginEJB extends AbstractEJB {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(LoginEJB.class);
	
	public UserDTO doLogin(LoginDTO loginDTO) {
		log.info("LoginEJB - doLogin");
		UserDTO userDTO = null;
		User dbUser = findUserByEmail(loginDTO.getEmail());
		if(loginSuccessful(loginDTO, dbUser)) {
			userDTO = new UserDTO();
			userDTO.setEmail(loginDTO.getEmail());
			userDTO.setKey(dbUser.getKey());
			
			ModelFactory.getInstance().registryUser(dbUser);
		}
		return userDTO;
	}
	
	private boolean loginSuccessful(LoginDTO loginDTO, User dbUser) {
		return (dbUser != null && loginDTO.getPassword().equals(dbUser.getPassword()));
	}
	
	private User findUserByEmail(String email){
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