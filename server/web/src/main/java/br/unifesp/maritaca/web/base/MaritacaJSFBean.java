package br.unifesp.maritaca.web.base;

import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.jsf.util.MaritacaConstants;
import br.unifesp.maritaca.web.utils.UtilsWeb;

public abstract class MaritacaJSFBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer itemsPerPage = MaritacaConstants.ITEMS_PER_PAGE;
	
	@ManagedProperty("#{manager}")
	private Manager moduleManager;		
	
	protected UserDTO getCurrentUser() {
		return (UserDTO)UtilsWeb.clientRequest().getSession().getAttribute(MaritacaConstants.CURRENT_USER);
	}
	
	protected void setCurrentUser(UserDTO userDto){
		UtilsWeb.clientRequest().getSession().setAttribute(MaritacaConstants.CURRENT_USER, userDto);
	}
	
	protected Integer getNumberOfPages (Integer size, Integer itemsPage) {
		Double pages = Math.ceil(size/itemsPage.doubleValue());
		return pages.intValue();
    }
	
	protected FacesContext getFacesContext() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext;
    }
	
	protected Object getJSFSessionBean(String JSFBeanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getSessionMap().get(JSFBeanName);
    }
	
	protected Map<String,Object> getSession() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }
	
	protected String getContextPath() {
        String contextpath = "";
        FacesContext context = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        contextpath = servletContext.getContextPath();
        return contextpath;
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

	public Manager getModuleManager() {
		return moduleManager;
	}

	public void setModuleManager(Manager moduleManager) {
		this.moduleManager = moduleManager;
	}
	public Integer getItemsPerPage() {						
		return itemsPerPage;
	}

	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}	
}