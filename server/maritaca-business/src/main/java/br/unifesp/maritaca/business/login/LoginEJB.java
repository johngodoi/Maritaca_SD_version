package br.unifesp.maritaca.business.login;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.base.AbstractBean;
import br.unifesp.maritaca.business.base.dto.MaritacaUserDTO;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.core.User;

@Stateless
public class LoginEJB extends AbstractBean {
	
	private static final Log log = LogFactory.getLog(LoginEJB.class);
	
	//@EJB private UserModel userModel;
	
	public LoginEJB() {
		super(false, true);
	}

	public MaritacaUserDTO doLogin(LoginDTO loginDTO) {
		log.info("LoginEJB - doLogin");
		MaritacaUserDTO maritacaUser = null;
		User dbUser = super.userCtrl.getUser(loginDTO.getEmail());
		if(loginSuccessful(loginDTO, dbUser)) {
			maritacaUser = new MaritacaUserDTO();
		}
		return maritacaUser;		
	}
	
	private boolean loginSuccessful(LoginDTO loginDTO, User dbUser) {
		return (dbUser != null && loginDTO.getPassword().equals(dbUser.getPassword()));
	}
}