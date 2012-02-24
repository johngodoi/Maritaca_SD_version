package br.unifesp.maritaca.web.oauth;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import net.smartam.leeloo.common.OAuth;

/**
 * This class is used to pass the parameters names from the
 * oAuth library to the maritaca web module.
 * @author alvarohenry, tiagobarabasz
 */
@ManagedBean
@SessionScoped
public class OAuthParametersBean {

	private final String REDIRECT_URI  = OAuth.OAUTH_REDIRECT_URI;
	private final String CLIENT_ID     = OAuth.OAUTH_CLIENT_ID;
	private final String RESPONSE_TYPE = OAuth.OAUTH_RESPONSE_TYPE;
	
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
