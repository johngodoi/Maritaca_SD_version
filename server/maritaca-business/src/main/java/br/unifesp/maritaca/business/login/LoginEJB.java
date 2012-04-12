package br.unifesp.maritaca.business.login;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.login.dao.LoginDAO;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
@Named("loginEJB2")
public class LoginEJB {
	
	private static final Log log = LogFactory.getLog(LoginEJB.class);
	
	@Inject private LoginDAO loginDAO;
	
	public UserDTO doLogin(LoginDTO loginDTO) {
		log.info("LoginEJB - doLogin");
		UserDTO userDTO = null;
		User dbUser = loginDAO.findUserByEmail(loginDTO.getEmail());
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
	
}