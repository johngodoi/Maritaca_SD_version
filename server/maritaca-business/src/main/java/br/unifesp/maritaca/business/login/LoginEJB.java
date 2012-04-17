package br.unifesp.maritaca.business.login;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.login.dao.LoginDAO;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
public class LoginEJB {
	
	@Inject private LoginDAO loginDAO;
	
	public UserDTO doLogin(LoginDTO loginDTO) {
		UserDTO userDTO = null;
		User dbUser = loginDAO.findUserByEmail(loginDTO.getEmail());
		if(loginSuccessful(loginDTO, dbUser)) {
			userDTO = new UserDTO();
			userDTO.setEmail(loginDTO.getEmail());
			userDTO.setKey(dbUser.getKey());
			userDTO.setFirstname(dbUser.getFirstname());
			userDTO.setLastname(dbUser.getLastname());
			userDTO.setMaritacaListKey(dbUser.getMaritacaList().getKey());
			
			//ModelFactory.getInstance().registryUser(dbUser);
		}
		return userDTO;
	}
	
	private boolean loginSuccessful(LoginDTO loginDTO, User dbUser) {
		return (dbUser != null && loginDTO.getPassword().equals(dbUser.getPassword()));
	}	
}