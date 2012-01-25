package br.unifesp.maritaca.web.jsf.home;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.unifesp.maritaca.core.User;

@ManagedBean
@SessionScoped
public class CurrentUserBean implements Serializable{
	private static final long serialVersionUID = 1L;	
	private User user;
	private boolean authenticated;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession hs = (HttpSession) fc.getExternalContext().getSession(false);
		hs.setAttribute("currentuser", user);
	}
	public boolean isAuthenticated() {
		return authenticated;
	}
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	public String logout(){
		setUser(null);
		return "/faces/views/login";
	}
}
