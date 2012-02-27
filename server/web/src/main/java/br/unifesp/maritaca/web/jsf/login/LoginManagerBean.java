package br.unifesp.maritaca.web.jsf.login;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.utils.Utils;

@ManagedBean
@SessionScoped
public class LoginManagerBean {

	private String redirectUri;
	private String clientId;
	private String responseType;
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUser;
	
	/**
	 * Redirects the user to the successful login page, which can be: <li>The
	 * home page</li> <li>localhost page (for mobile client)</li>
	 */
	public void login() {
		try {
			String successfulLoginUrl;
			
			if (getRedirectUri()!= null && getResponseType()!=null && getClientId()!=null) {
				successfulLoginUrl = Utils.buildServletUrl("/oauth/oauth/generatecode") + buildQueryString();
			} else {
				successfulLoginUrl = Utils.buildViewUrl("/views/home.xhtml");
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
			//context.getExternalContext().dispatch(successfulLoginUrl);
			context.getExternalContext().redirect(successfulLoginUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String buildQueryString() {
		return  "client_id=" + getClientId() + "&" +
				"redirect_uri=" + getRedirectUri() +  "&" +
				"response_type=" + getResponseType() + "&" +
				"userid=" + getCurrentUser().getUser().getKey();
		
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		if(!"".equals(redirectUri))
			this.redirectUri = redirectUri;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		if(!"".equals(responseType))
			this.responseType = responseType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		if(!"".equals(clientId))
			this.clientId = clientId;
	}

	public CurrentUserBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(CurrentUserBean currentUser) {
		this.currentUser = currentUser;
	}
}
