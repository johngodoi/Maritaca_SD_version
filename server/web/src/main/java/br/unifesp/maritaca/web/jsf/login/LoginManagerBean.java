package br.unifesp.maritaca.web.jsf.login;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import net.smartam.leeloo.common.OAuth;

import br.unifesp.maritaca.web.utils.Utils;

@ManagedBean
@SessionScoped
public class LoginManagerBean {

	private String redirectUri;
	private String clientId;
	private String responseType;	

	public LoginManagerBean() {
	}
	
	public void saveRequest(){
		FacesContext        context  = FacesContext.getCurrentInstance();
		HttpServletRequest  request  = (HttpServletRequest) context.getExternalContext().getRequest();
		
		System.out.println(request.toString());
	}
	
	/**
	 * Redirects the user to the successful login page, which can be: <li>The
	 * home page</li> <li>localhost page (for mobile client)</li>
	 */
	public void login() {
		try {
			String successfulLoginUrl;
			
			if (getRedirectUri()!= null && getResponseType()!=null && getClientId()!=null) {
				successfulLoginUrl = Utils.buildServletUrl("/ws/oauth/generatecode") + buildQueryString();
			} else {
				successfulLoginUrl = Utils.buildViewUrl("/views/home.xhtml");
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().redirect(successfulLoginUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String buildQueryString() {
		return  OAuth.OAUTH_CLIENT_ID + "=" + getClientId() + "&" +
				OAuth.OAUTH_REDIRECT_URI + "=" + getRedirectUri() +  "&" +
				OAuth.OAUTH_RESPONSE_TYPE + "=" + getResponseType();
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
