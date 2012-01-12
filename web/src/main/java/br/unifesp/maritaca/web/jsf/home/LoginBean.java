package br.unifesp.maritaca.web.jsf.home;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdException;
import org.expressme.openid.OpenIdManager;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@SessionScoped
public class LoginBean extends AbstractBean{
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	private User   user;
	private String status;

    private Set<String>         nonceDb    = new HashSet<String>();
    private static final long   ONE_HOUR   = 3600000L;
    private static final long   TWO_HOUR   = ONE_HOUR * 2L;
//    private static final String ATTR_MAC   = "openid_mac";
//    private static final String ATTR_ALIAS = "openid_alias";
	
	public LoginBean() {
		super(false,true);
		setUser(new User());
		setStatus("");
	}

	public Collection<User> getUserList() {
		return userCtrl.listAllUsersMinimal();
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public String submit() {
		User dbUser = super.userCtrl.getUser(getUser().getEmail());
		if(getUser().getPassword().equals(dbUser.getPassword())){
			getCurrentUserBean().setUser(dbUser);
			return "/faces/views/forms";	
		} else {
			setStatus("Login failed!");
			return "/faces/views/login";	
		}		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String openId(){
		FacesContext  context  = FacesContext.getCurrentInstance();
		String        op       = parseOp(context);
		String        nonce    = parseNonce(context);
		OpenIdManager manager  = new OpenIdManager();
		
		
        manager.setRealm("http://localhost");
        manager.setReturnTo("http://localhost/openid");
		
        if (op==null) {
            // check sign on result from Google or Yahoo:
            checkNonce(nonce);
            // get authentication:
            //byte[] mac_key = (byte[]) request.getSession().getAttribute(ATTR_MAC);
            //String alias = (String) request.getSession().getAttribute(ATTR_ALIAS);
            //Authentication authentication = manager.getAuthentication(request, mac_key, alias);
            return "";
        }
        if (op.equals("Google") || op.equals("Yahoo")) {
            // redirect to Google or Yahoo sign on page:
            Endpoint endpoint = manager.lookupEndpoint(op);
            Association association = manager.lookupAssociation(endpoint);
            //request.getSession().setAttribute(ATTR_MAC, association.getRawMacKey());
            //request.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
            String url = manager.getAuthenticationUrl(endpoint, association);
            
            //response.sendRedirect(url);
        } else {
            throw new RuntimeException("Unsupported OP: " + op);
        }        
		
		return "/faces/views/forms";
	}
	
	
	private String parseNonce(FacesContext context) {
		Map<String, String> params = context.getExternalContext().
				getRequestParameterMap();
				return params.get("nonce");	}

	private void checkNonce(String nonce) {
        // check response_nonce to prevent replay-attack:
        if (nonce==null || nonce.length()<20)
            throw new OpenIdException("Verify failed.");
        // make sure the time of server is correct:
        long nonceTime = getNonceTime(nonce);
        long diff = Math.abs(System.currentTimeMillis() - nonceTime);
        if (diff > ONE_HOUR)
            throw new OpenIdException("Bad nonce time.");
        if (isNonceExist(nonce))
            throw new OpenIdException("Verify nonce failed.");
        storeNonce(nonce, nonceTime + TWO_HOUR);
    }
	
	private boolean isNonceExist(String nonce) {
        return nonceDb.contains(nonce);
    }

	private long getNonceTime(String nonce) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .parse(nonce.substring(0, 19) + "+0000")
                    .getTime();
        }
        catch(ParseException e) {
            throw new OpenIdException("Bad nonce time.");
        }
    }
	
    void storeNonce(String nonce, long expires) {
        nonceDb.add(nonce);
    }

	private String parseOp(FacesContext context) {
		Map<String, String> params = context.getExternalContext().
				getRequestParameterMap();
				return params.get("op");
	}

	public CurrentUserBean getCurrentUserBean() {
		return currentUserBean;
	}

	public void setCurrentUserBean(CurrentUserBean currentUserBean) {
		this.currentUserBean = currentUserBean;
	}
}
