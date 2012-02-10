package br.unifesp.maritaca.web.jsf.account;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ModelFactory;

@ManagedBean
@SessionScoped
public class CurrentUserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private User user;
	private boolean authenticated;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if (user != null) {
			HttpSession hs = getSession(false);
			hs.setAttribute("currentuser", user);
			ModelFactory.getInstance().registryUser(user);
		}
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	private HttpSession getSession(boolean newsession) {
		FacesContext fc = FacesContext.getCurrentInstance();
		return (HttpSession) fc.getExternalContext().getSession(
				newsession);
	}
}
