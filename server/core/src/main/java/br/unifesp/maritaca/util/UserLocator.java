package br.unifesp.maritaca.util;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

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
			UserDTO maritacaUser = (UserDTO) fc.getExternalContext().getSessionMap().get("currentuser");
			if(maritacaUser != null) {
				user = new User();
				user.setKey(maritacaUser.getKey());
				user.setEmail(maritacaUser.getEmail());				
			}
			log.debug("user from jsf" + user);
		}else{
			log.debug("user must be from rest or servlet");
			//it must be a restful request
		}
		
		return user;
	}
	
	/*public static User getCurrentUser() {
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
		}
		
		return user;
	}*/
}