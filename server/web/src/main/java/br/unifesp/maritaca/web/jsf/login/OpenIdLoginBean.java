package br.unifesp.maritaca.web.jsf.login;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.account.AccountEditorBean;
import br.unifesp.maritaca.web.utils.Utils;

@ManagedBean
@SessionScoped
public class OpenIdLoginBean extends AbstractBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final Log  log              = LogFactory.getLog(OpenIdLoginBean.class);

	@ManagedProperty("#{accountEditorBean}")
	private AccountEditorBean accountEditorBean;
	
	@ManagedProperty("#{loginManagerBean}")
	private LoginManagerBean loginManagerBean;
	
    private static final String ATTR_MAC       = "openid_mac";
    private static final String ATTR_ALIAS     = "openid_alias";
	private static final String ATTR_END_POINT = "openid.op_endpoint";
	
	private OpenIdManager manager;
	
	public OpenIdLoginBean() {
		super(false, true);
	}
	
	public String getOnLoad(){
		verifyOpenIdResponse();
		return "";
	}
	
	/**
     * Set the class members with date from the authentication response.
     * Extract the parameters from the authentication response (which comes
     * in as a HTTP request from the OpenID provider). Verify the response,
     * examine the verification result and extract the verified identifier.
     * @param httpReq httpRequest
     * @return users identifier.
     */
    private void verifyOpenIdResponse() {    	    	
    	FacesContext       context        = FacesContext.getCurrentInstance();    	
    	HttpServletRequest request        = (HttpServletRequest) context.getExternalContext().getRequest();
    	
    	if(!returningFromOpenIdAuth(request)){
    		return;
    	}
    	
        byte[]             mac_key        = (byte[]) request.getSession().getAttribute(ATTR_MAC);
        String             alias          = (String) request.getSession().getAttribute(ATTR_ALIAS);
        
        Authentication     authentication = getManager().getAuthentication(request, mac_key, alias);
        
        fillCurrentUser(authentication);
        
        if(getAccountEditorBean().registeredEmail()){
        	loadUserInfoFromDatabase();        	
        	getLoginManagerBean().login();
        }
    }
    
    /**
     * Loads user info from database.
     */
    private void loadUserInfoFromDatabase() {
    	String email = getAccountEditorBean().getEmail();
    	User   user  = super.userCtrl.findUserByEmail(email);
    	
    	getAccountEditorBean().getCurrentUserBean().setUser(user);		
	}

	private void facesRedirect(String url){
    	try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
    }

	private boolean returningFromOpenIdAuth(HttpServletRequest request) {
		String endPoint = request.getParameter(ATTR_END_POINT);
		if(endPoint==null){
    		return false;
    	} else {
    		return true;
    	}
	}

	/**
	 * Fills the current user with information obtained
	 * from OpenId authentication.
	 * @param authentication
	 */
	private void fillCurrentUser(Authentication authentication) {
		User currentUser = new User();
		
		currentUser.setEmail(authentication.getEmail());
		currentUser.setFirstname(authentication.getFirstname());
		currentUser.setLastname(authentication.getLastname());
		
		getAccountEditorBean().fillInformationFromUser(currentUser);
	}

	public OpenIdManager getManager() {
		return manager;
	}

	public void setManager(OpenIdManager manager) {
		this.manager = manager;
	}
	
	
	/**
	 * This function handles the openId login using an specific
	 * identity provider (such as google or yahoo).
	 */
	public void openIdOpLogin() {
		try {
			FacesContext       context     = FacesContext.getCurrentInstance();
			String             op          = parseOp(context);			
			String             returnUrl   = Utils.buildViewUrl("/views/createAccount.xhtml");
			String             returnRealm = Utils.buildServerAddressUrl();
	        HttpServletRequest request     = (HttpServletRequest) context.getExternalContext().getRequest();
									
			setManager(new OpenIdManager());
			getManager().setRealm(returnRealm);
			getManager().setReturnTo(returnUrl);

			if (op.equals("Google") || op.equals("Yahoo")) {
				// redirect to Google or Yahoo sign on page:
				Endpoint endpoint = getManager().lookupEndpoint(op);
				Association association = getManager().lookupAssociation(endpoint);
	            request.getSession().setAttribute(ATTR_MAC, association.getRawMacKey());
	            request.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
				String url = getManager()
						.getAuthenticationUrl(endpoint, association);

				facesRedirect(url);
			} else {
				log.error("Invalid OP for openid authentication: " + op);
				throw new RuntimeException();
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(
					Utils.getMessageFromResourceProperties("login_open_id_error"));
		}
	}

	private String parseOp(FacesContext context) {
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();
		return params.get("op");
	}

	public AccountEditorBean getAccountEditorBean() {
		return accountEditorBean;
	}

	public void setAccountEditorBean(AccountEditorBean accountEditorBean) {
		this.accountEditorBean = accountEditorBean;
	}

	public LoginManagerBean getLoginManagerBean() {
		return loginManagerBean;
	}

	public void setLoginManagerBean(LoginManagerBean loginManagerBean) {
		this.loginManagerBean = loginManagerBean;
	}
}
