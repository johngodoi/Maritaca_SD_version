package br.unifesp.maritaca.web.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.ResourceBundle;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import br.unifesp.maritaca.business.exception.MaritacaException;

public class UtilsWeb {
	public static final String SPACEWILDCARD="_";	
	
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
		return buildServerAddressUrl() + contextPath + string + "?";
	}
	
	public static HttpServletRequest clientRequest() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        return request;
    }

	/**
	 * This method makes responses in JSON format. Generally is use to 
	 * treat exceptions and send back to the user an specific message. 
	 * @param response
	 * @param params
	 */
	public static void makeResponseInJSON(HttpServletResponse response, 
													String... params) {
		HttpServletResponse response1 = response;
		response1.setContentType("application/json");
		sendValuesInJson(response1, params);
		response1.setStatus(HttpURLConnection.HTTP_OK);
    }
	
	/**
	 * This method writes, in JSON format, params in the HttpServletResponse.
	 * @param response
	 * @param params
	 * @throws IOException
	 */
	public static void sendValuesInJson(HttpServletResponse response, String... params) {
		PrintWriter printWriter;
		try {
			printWriter = response.getWriter();
			if (params.length % 2 != 0) {
				throw new IllegalArgumentException("Arguments should be name=value*");
			} 
			
			printWriter.append('{');
			for (int i = 0; i < params.length; i+=2) {
				if (i > 0) {
					printWriter.append(',');
				}
				printWriter.append('"');
				printWriter.append(params[i]);
				printWriter.append('"');
				printWriter.append(':');
				printWriter.append('"');
				printWriter.append(params[i+1]);
				printWriter.append('"');
			}
			printWriter.append('}');
		} catch (IOException e) {			
			throw new MaritacaException(e.getMessage());
		}
	}
}
