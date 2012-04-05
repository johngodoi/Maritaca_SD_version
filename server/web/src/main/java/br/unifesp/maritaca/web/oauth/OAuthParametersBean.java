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

	private final String OAUTH_TOKEN   = "oauth_token";

	public String getOAuth_Token() {
		return OAUTH_TOKEN;
	}
}
