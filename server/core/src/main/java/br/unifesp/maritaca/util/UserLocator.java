package br.unifesp.maritaca.util;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.User;

/**
 * Temporary class to get the current user in the session in both JSF and REST
 * 
 * @author emiguel
 * 
 */
public class UserLocator {
	public static final Log log=LogFactory.getLog(UserLocator.class);
	
	public static User getCurrentUser() {
		User user = null;
		// first, try to get the faces context
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			// jsf request
			user = (User) fc.getExternalContext().getSessionMap()
					.get("currentuser");
			log.debug("user from jsf" + user);
		}else{
			log.debug("user must be from rest or servlet");
			//it must be a restful request
			//TODO get current user from restful request
		}
		
		return user;
	}
}
