package br.unifesp.maritaca.business.login;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.login.dao.LoginDAO;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
public class LoginEJB {
	
	@Inject
	private LoginDAO loginDAO;
	
	public UserDTO doLogin(LoginDTO loginDTO) {
		UserDTO dbUser = loginDAO.findUserByEmail(loginDTO.getEmail());
		if(loginSuccessful(loginDTO, dbUser)) {
			return dbUser;
		} else {
			return null;
		}
	}
	
	private boolean loginSuccessful(LoginDTO loginDTO, UserDTO dbUser) {
		return (dbUser != null && loginDTO.getPassword().equals(dbUser.getPassword()));
	}	
}