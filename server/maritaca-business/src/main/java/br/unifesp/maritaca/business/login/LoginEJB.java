package br.unifesp.maritaca.business.login;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.base.AbstractBusinessBean;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.persistence.dto.MaritacaUserDTO;

@Stateless
public class LoginEJB extends AbstractBusinessBean {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(LoginEJB.class);
	
	public LoginEJB() {
		super(false, true);
	}

	public MaritacaUserDTO doLogin(LoginDTO loginDTO) {
		log.info("LoginEJB - doLogin");
		MaritacaUserDTO maritacaUser = null;
		User dbUser = super.userCtrl.getUser(loginDTO.getEmail());
		if(loginSuccessful(loginDTO, dbUser)) {
			maritacaUser = new MaritacaUserDTO();
			maritacaUser.setEmail(loginDTO.getEmail());
			maritacaUser.setKey(dbUser.getKey());
			
			ModelFactory.getInstance().registryUser(dbUser);
		}
		return maritacaUser;		
	}
	
	private boolean loginSuccessful(LoginDTO loginDTO, User dbUser) {
		return (dbUser != null && loginDTO.getPassword().equals(dbUser.getPassword()));
	}
}