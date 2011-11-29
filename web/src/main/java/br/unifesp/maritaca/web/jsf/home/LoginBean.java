package br.unifesp.maritaca.web.jsf.home;

import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@RequestScoped
public class LoginBean extends AbstractBean{
	private User user;

	public LoginBean() {
		super(false,true);
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
		// temporal
		return "success";
	}
}
