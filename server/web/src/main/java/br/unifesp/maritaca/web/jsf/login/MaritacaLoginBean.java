package br.unifesp.maritaca.web.jsf.login;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.login.LoginEJB;
import br.unifesp.maritaca.business.login.dto.LoginDTO;
import br.unifesp.maritaca.persistence.dto.MaritacaUserDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.utils.Utils;

@ManagedBean
@RequestScoped
public class MaritacaLoginBean extends MaritacaJSFBean {
	
	private static final long serialVersionUID = 1L;
	
	@Inject private LoginEJB loginEJB;
	
	private LoginDTO loginDTO;
	private String status;
	
	@ManagedProperty("#{currentUserBean}")	
	private CurrentUserBean currentUserBean;
	
	@ManagedProperty("#{loginManagerBean}") 
	private LoginManagerBean loginManagerBean;
	
	public MaritacaLoginBean() {
		setLoginDTO(new LoginDTO());
		setStatus("");
	}
	
	public String submit() {
		MaritacaUserDTO maritacaUser = loginEJB.doLogin(loginDTO);		
		if(maritacaUser != null) {
			//Set the data of the current user in session
			getRequest().getSession().setAttribute(MaritacaConstants.CURRENT_USER, maritacaUser);
			//TODO: Next step: delete CurrentUserBean
			getCurrentUserBean().setUser(maritacaUser);			
			getLoginManagerBean().login();
			return "";
		}
		else {
			setStatus(Utils.getMessageFromResourceProperties("login_failed"));
			return MaritacaConstants.FACES_LOGIN;
		}
	}	

	/*public Collection<User> getUserList() {
		return userCtrl.listAllUsersMinimal();
	}*/

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CurrentUserBean getCurrentUserBean() {
		return currentUserBean;
	}

	public void setCurrentUserBean(CurrentUserBean currentUserBean) {
		this.currentUserBean = currentUserBean;
	}

	public LoginManagerBean getLoginManagerBean() {
		return loginManagerBean;
	}

	public void setLoginManagerBean(LoginManagerBean loginManagerBean) {
		this.loginManagerBean = loginManagerBean;
	}
	
	public LoginDTO getLoginDTO() {
		return loginDTO;
	}

	public void setLoginDTO(LoginDTO loginDTO) {
		this.loginDTO = loginDTO;
	}
}