package br.unifesp.maritaca.web.jsf.login;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.utils.UtilsWeb;

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
			
			if (isValidOAuthParameters()) {	
				// TODO implement the query to ask to the user if allows access to its resources
				successfulLoginUrl = UtilsWeb.buildServletUrl("/oauth/authorizationConfirm") + buildQueryString();
			} else {
				successfulLoginUrl = UtilsWeb.buildViewUrl("/views/home.xhtml");
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().redirect(successfulLoginUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isValidOAuthParameters() {
		boolean validRedirectUri  = getRedirectUri()!=null && !getRedirectUri().equals("");
		boolean validResponseType = getResponseType()!=null && !getResponseType().equals("");
		boolean validClientId     = getClientId()!=null && !getClientId().equals("");
		
		return validRedirectUri && validResponseType && validClientId;
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
