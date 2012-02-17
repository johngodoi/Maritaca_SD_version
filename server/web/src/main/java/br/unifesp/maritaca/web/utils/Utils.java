package br.unifesp.maritaca.web.utils;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

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
	
    /**
     * Create the current url and add another url path fragment on it.
     * Obtain from the current context the url and add another url path fragment at
     * the end.
     * @param urlExtension f.e. /nextside.xhtml
     * @return the hole url including the new fragment
     */
    public static String buildViewUrl(String urlExtension) {
        FacesContext context = FacesContext.getCurrentInstance();
        String returnToUrl = buildServerAddressUrl() + 
        		context.getApplication().getViewHandler().getActionURL(context, urlExtension);
        return returnToUrl;
    }
    
    public static String buildServerAddressUrl(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        return "http://" + request.getServerName() + ":" + request.getServerPort();
    }
}
