package br.unifesp.maritaca.business.login;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.AccountEditorDAO;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.persistence.entity.User;

@Stateless
public class LoginEJB {
	
	@Inject
	private AccountEditorDAO accountDAO;
	
	public UserDTO doLogin(LoginDTO loginDTO) {
		UserDTO userDto = findUserByEmail(loginDTO.getEmail());
		if(loginSuccessful(loginDTO, userDto)) {
			return userDto;
		} else {
			return null;
		}
	}
	
	private boolean loginSuccessful(LoginDTO loginDTO, UserDTO dbUser) {
		return (dbUser != null && loginDTO.getPassword().equals(dbUser.getPassword()));
	}

	public UserDTO findUserByEmail(String email) {
		User    user    = accountDAO.findUserByEmail(email);
		return UtilsBusiness.reflectClasses(user, UserDTO.class);
	}	
}
