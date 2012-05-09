package br.unifesp.maritaca.web.jsf.home;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean
public class LogoutBean {

	public String logout() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			fc.getExternalContext().getSessionMap().clear();
			fc.getExternalContext().invalidateSession();
		}
		return "home.xhtml?faces-redirect=true";
	}
}
