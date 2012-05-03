package br.unifesp.maritaca.web.jsf.account;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.unifesp.maritaca.business.account.edit.dto.UserDTO;

@ManagedBean
@SessionScoped
@Deprecated
public class CurrentUserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	//private User user;
	private UserDTO user;
	private boolean authenticated;

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
		if (user != null) {
			HttpSession hs = getSession(false);
			hs.setAttribute("currentuser", user);
//			ModelFactory.getInstance().registryUser(user); now into LoginEJB
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
