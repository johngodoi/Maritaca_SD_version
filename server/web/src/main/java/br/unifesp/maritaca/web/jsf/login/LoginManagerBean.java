package br.unifesp.maritaca.web.jsf.login;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.utils.Utils;

@ManagedBean
@SessionScoped
public class LoginManagerBean extends MaritacaJSFBean{
		
	private static final long serialVersionUID = 1L;
	
	private String redirectUri;
	private String clientId;
	private String responseType;
		
	/**
	 * Redirects the user to the successful login page, which can be: 
	 * 		<li>The home page</li>
	 * 		<li>Third-party application</li>
	 * @param userDTO DTO object that contains user data
	 */
	public void login(UserDTO userDTO) {
		try {
			String successfulLoginUrl;
			
			if (getRedirectUri()!= null && getResponseType()!=null && getClientId()!=null) {	
				// TODO implement the query to ask to the user if allows access to its resources
				successfulLoginUrl = Utils.buildServletUrl("/oauth/authorizationConfirm") + buildQueryString();
			} else {
				successfulLoginUrl = Utils.buildViewUrl("/views/home.xhtml");
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().redirect(successfulLoginUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String currentUserEmail(){
		return getCurrentUser().getEmail();
	}

	private String buildQueryString() {
		return  "client_id=" + getClientId() + "&" +
				"redirect_uri=" + getRedirectUri() + "&" +
				"response_type=" + getResponseType() + "&" +
				"user_id=" + getCurrentUser().getKey().toString();
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
