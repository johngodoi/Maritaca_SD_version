package br.unifesp.maritaca.web.oauth;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * This class is used to pass the parameters names from the
 * oAuth library to the maritaca web module.
 * @author alvarohenry, tiagobarabasz
 */
@ManagedBean
@SessionScoped
public class OAuthParametersBean {

	private final String REDIRECT_URI  = "redirect_uri";
	private final String CLIENT_ID     = "client_id";
	private final String RESPONSE_TYPE = "response_type";
	
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
