package br.unifesp.maritaca.web.oauth;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * This Bean is used to pass the OAuth parameters names from the
 * from the third-party application to the login user.
 * 
 * @author alvarohenry, tiagobarabasz
 */
@ManagedBean
@SessionScoped
public class OAuthParametersBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String REDIRECT_URI   = "redirect_uri";
	private final String CLIENT_ID   = "client_id";
	private final String RESPONSE_TYPE   = "response_type";

	public String getRedirectUri() {
		return REDIRECT_URI;
	}
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
	public String getClientId() {
		return CLIENT_ID;
	}
		
}
