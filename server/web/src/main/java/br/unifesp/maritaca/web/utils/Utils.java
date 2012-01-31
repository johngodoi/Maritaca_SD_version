package br.unifesp.maritaca.web.utils;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class Utils {
	public static final String SPACEWILDCARD="_";	
	public static final String EMAIL_REG_EXP=".+@.+\\.[a-z]+";
	
	public static String getCompactedVersion(String str){
		if(str == null) return null;
		return str.replaceAll(" ", SPACEWILDCARD);
	}
	
	/**
	 * Get message from message.properties
	 * @param value 
	 * @return
	 */
	public static String getMessageFromResourceProperties(String value) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String messageBoundleName = fc.getApplication().getMessageBundle();
		ResourceBundle bundle = ResourceBundle.getBundle(messageBoundleName);
		return bundle.getString(value);
	}
}
