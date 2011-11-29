package br.unifesp.maritaca.web.jsf.home;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.core.User;

@ManagedBean
@SessionScoped
public class HomeBean {
	private User currentUser;
	private boolean initialized;

	public HomeBean() {
		initialized = false;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public String listForms() {
		return "listForms";
	}

	public String newForm() {
		return "newForm";
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public String goHome(){
		return "goHome";
	}
}
