package br.unifesp.maritaca.web.jsf.home;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;

@ManagedBean
public class LogoutBean {
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUser;
	public String logout() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			fc.getExternalContext().getSessionMap().clear();
			fc.getExternalContext().invalidateSession();
		}
		//ModelFactory.getInstance().invalidateModelsForUser(getCurrentUser().getUser());
		return "home.xhtml?faces-redirect=true";

	}
	
	public CurrentUserBean getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(CurrentUserBean currentUser) {
		this.currentUser = currentUser;
	}
}
