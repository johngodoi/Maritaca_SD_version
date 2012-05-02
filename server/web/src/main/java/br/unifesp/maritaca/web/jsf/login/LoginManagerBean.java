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
	private String oauth_token;
		
	/**
	 * Redirects the user to the successful login page, which can be: <li>The
	 * home page</li> <li>localhost page (for mobile client)</li>
	 * @param userDTO 
	 */
	public void login(UserDTO userDTO) {
		try {
			String successfulLoginUrl;
			
			if (getOauth_token()!=null) {	
				// TODO implement the query to ask to the user if allows access to its resources
				successfulLoginUrl = UtilsWeb.buildServletUrl("/oauth/authorization/confirm") + buildQueryString();
			} else {
				successfulLoginUrl = UtilsWeb.buildViewUrl("/views/home.xhtml");
			}
			setCurrentUser(userDTO);
						
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
		return  "oauth_token=" + getOauth_token() + "&" +
				"xoauth_end_user_decision=" + "yes" + "&" +  
				"userid=" + getCurrentUser().getKey();
	}

	public String getOauth_token() {
		return oauth_token;
	}

	public void setOauth_token(String oauth_token) {
		if(!"".equals(oauth_token))
			this.oauth_token = oauth_token;
	}
}
