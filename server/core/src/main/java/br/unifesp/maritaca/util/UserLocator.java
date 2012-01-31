package br.unifesp.maritaca.util;

import javax.faces.context.FacesContext;

import br.unifesp.maritaca.core.User;

/**
 * Temporary class to get the current user in the session in both JSF and REST
 * 
 * @author emiguel
 * 
 */
public class UserLocator {
	public static User getCurrentUser() {
		User user = null;
		// first, try to get the faces context
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			// jsf request
			user = (User) fc.getExternalContext().getSessionMap()
					.get("currentuser");
		}else{
			//it must be a restful request
			//TODO get current user from restful request
		}
		return user;
	}
}
