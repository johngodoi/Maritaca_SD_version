package br.unifesp.maritaca.web.jsf.login;

import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.utils.Utils;

@ManagedBean
@RequestScoped
public class MaritacaLoginBean extends AbstractBean {
	
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	
	@ManagedProperty("#{loginManagerBean}")
	private LoginManagerBean loginManagerBean;
	
	private User user;
	private String status;

	public MaritacaLoginBean() {
		super(false, true);
		setUser(new User());
		setStatus("");
	}

	public Collection<User> getUserList() {
		return userCtrl.listAllUsersMinimal();
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public String submit() {
		User dbUser = super.userCtrl.getUser(getUser().getEmail());
		if(loginSuccessful(dbUser)){
			getCurrentUserBean().setUser(dbUser);			
			getLoginManagerBean().login();
			return "";			
		} else {
			setStatus(Utils.getMessageFromResourceProperties("login_failed"));
			return "/faces/views/login";
		}
	}

	private boolean loginSuccessful(User dbUser) {
		return (dbUser!=null &&
				getUser().getPassword().equals(dbUser.getPassword()));
	}

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

}
