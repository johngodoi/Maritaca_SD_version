package br.unifesp.maritaca.web.jsf.login;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.unifesp.maritaca.web.utils.Utils;

@ManagedBean
@SessionScoped
public class LoginManagerBean {

	private String returnUrl;

	public LoginManagerBean() {
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String successfulLoginUrl) {
		this.returnUrl = successfulLoginUrl;
	}

	/**
	 * Redirects the user to the successful login page, which can be: <li>The
	 * home page</li> <li>localhost page (for mobile client)</li>
	 */
	public void login() {
		try {
			String successfulLoginUrl;
			
			if (getReturnUrl() != null) {
				successfulLoginUrl = getReturnUrl();
			} else {
				successfulLoginUrl = Utils.buildViewUrl("/views/home.xhtml");
			}
			
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().redirect(successfulLoginUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
