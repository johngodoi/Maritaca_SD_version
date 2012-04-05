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
	
	private String oauth_token;
	
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUser;
	
	/**
	 * Redirects the user to the successful login page, which can be: <li>The
	 * home page</li> <li>localhost page (for mobile client)</li>
	 */
	public void login() {
		try {
			String successfulLoginUrl;
			
			if (getOauth_token()!=null) {	
				// TODO implement the query to ask to the user if allows access to its resources
				successfulLoginUrl = Utils.buildServletUrl("/oauth/authorization/confirm") + buildQueryString();
			} else {
				successfulLoginUrl = Utils.buildViewUrl("/views/home.xhtml");
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().redirect(successfulLoginUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String buildQueryString() {
		return  "oauth_token=" + getOauth_token() + "&" +
				"xoauth_end_user_decision=" + "yes" + "&" +  
				"userid=" + getCurrentUser().getUser().getKey();
	}

	public CurrentUserBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(CurrentUserBean currentUser) {
		this.currentUser = currentUser;
	}

	public String getOauth_token() {
		return oauth_token;
	}

	public void setOauth_token(String oauth_token) {
		if(!"".equals(oauth_token))
			this.oauth_token = oauth_token;
	}
}
