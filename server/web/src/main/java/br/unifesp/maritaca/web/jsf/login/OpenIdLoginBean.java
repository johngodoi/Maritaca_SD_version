package br.unifesp.maritaca.web.jsf.login;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.account.AccountManagerBean;

@ManagedBean
@SessionScoped
public class OpenIdLoginBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{accountManagerBean}")
	private AccountManagerBean accountManagerBean;
	
    private static final String ATTR_MAC       = "openid_mac";
    private static final String ATTR_ALIAS     = "openid_alias";
	private static final String ATTR_END_POINT = "openid.op_endpoint";
	
	private OpenIdManager manager;
	
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
        
        if(getAccountManagerBean().registeredEmail()){
        	User user = getAccountManagerBean().getUser().clone();
        	getAccountManagerBean().getCurrentUserBean().setUser(user);
        	String returnUrl   = returnToUrl("/views/home.xhtml");
        	facesRedirect(returnUrl);
        }
    }
    
    private void facesRedirect(String url){
    	try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e) {
			e.printStackTrace();
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

	private void fillCurrentUser(Authentication authentication) {
		User currentUser = new User();
		
		currentUser.setEmail(authentication.getEmail());
		currentUser.setFirstname(authentication.getFirstname());
		currentUser.setLastname(authentication.getLastname());
		
		getAccountManagerBean().setUser(currentUser);
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
			String             returnUrl   = returnToUrl("/views/createAccount.xhtml");
			String             returnRealm = retrieveRealm();
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
				throw new RuntimeException("Unsupported OP: " + op);
			}

		} catch (Exception e) {
		}
	}
	
    /**
     * Create the current url and add another url path fragment on it.
     * Obtain from the current context the url and add another url path fragment at
     * the end.
     * @param urlExtension f.e. /nextside.xhtml
     * @return the hole url including the new fragment
     */
    private String returnToUrl(String urlExtension) {
        FacesContext context = FacesContext.getCurrentInstance();
        String returnToUrl = retrieveRealm() + 
        		context.getApplication().getViewHandler().getActionURL(context, urlExtension);
        return returnToUrl;
    }
    
    private String retrieveRealm(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        return "http://" + request.getServerName() + ":" + request.getServerPort();
    }

	private String parseOp(FacesContext context) {
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();
		return params.get("op");
	}

	public AccountManagerBean getAccountManagerBean() {
		return accountManagerBean;
	}

	public void setAccountManagerBean(AccountManagerBean accountManagerBean) {
		this.accountManagerBean = accountManagerBean;
	}
}
