package br.unifesp.maritaca.web.base;

import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.unifesp.maritaca.persistence.dto.MaritacaUserDTO;

public abstract class MaritacaJSFBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected MaritacaUserDTO getCurrentUser() {
		//getRequest().getSession().getAttribute("currentuser");
		MaritacaUserDTO maritacaUser = (MaritacaUserDTO) getFacesContext().getExternalContext().getSessionMap().get("currentuser");
		if(maritacaUser !=null)
			return maritacaUser;
		return null;
	}
	
	protected FacesContext getFacesContext() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext;
    }
	
	protected Object getJSFSessionBean(String JSFBeanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getSessionMap().get(JSFBeanName);
    }
	
	protected Map getSession() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }
	
	protected String getContextPath() {
        String contextpath = "";
        FacesContext context = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        contextpath = servletContext.getContextPath();
        return contextpath;
    }

	protected HttpServletRequest getRequest() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        return request;
    }

	protected HttpServletResponse getResponse() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        return response;
    }
    
	protected String getText(String key) {        
        ResourceBundle bundle = ResourceBundle.getBundle(getFacesContext().getApplication().getMessageBundle(), 
        		FacesContext.getCurrentInstance().getViewRoot().getLocale());        
        return bundle.getString(key);
    }

	protected void addMessageError(String message) {
        addMessageError(null, message);
    }

	protected void addMessageWarning(String message) {
        addMessageWarning(null, message);
    }

	protected void addMessageFatal(String message) {
        addMessageFatal(null, message);
    }

	protected void addMessageInfo(String message) {
        addMessageInfo(null, message);
    }

	protected void addMessageError(String clientId, String message) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        //FacesContext.getCurrentInstance().addMessage(clientId,new FacesMessage(message));
    }

	protected void addMessageWarning(String clientId, String message) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
    }

	protected void addMessageFatal(String clientId, String message) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, message));
    }

	protected void addMessageInfo(String clientId, String message) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
    }
}