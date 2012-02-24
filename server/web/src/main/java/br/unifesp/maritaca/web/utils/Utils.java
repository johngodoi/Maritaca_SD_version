package br.unifesp.maritaca.web.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
        String scheme = request.getScheme();
        return scheme + "://" + request.getServerName() + ":" + request.getServerPort();
    }
    
    public static String buildXMLFromObject(Object obj) {
    	String xmlObject;
    	try{    		
    		JAXBContext  jc = JAXBContext.newInstance(obj.getClass().getCanonicalName());    	
    		Marshaller   m  = jc.createMarshaller();    		
    		StringWriter st = new StringWriter(); 
    		
    		m.marshal( obj, st );
    		xmlObject = st.toString();
    	} catch (JAXBException e){
    		xmlObject = "";
    	}        
    	return xmlObject;
    }

	public static String buildServletUrl(String string) {
		FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String contextPath = request.getContextPath();
        String queryString = request.getQueryString();
		return buildServerAddressUrl() + contextPath + string + "?";
	}
}
