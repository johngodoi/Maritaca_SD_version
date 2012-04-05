package br.unifesp.maritaca.web.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthRequestToken;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.auth.oauth.OAuthValidator;

import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

/**
 * Maritaca OAuth Http Servlet
 * This class is a replication with some modification of
 * Resteasy OAuth Http Servlet that handles Request Token creation and exchange for Access Tokens.
 * Resteasy OAuth Http Servlet @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class MaritacaOAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(MaritacaOAuthServlet.class);

	private static UserModel userModel;
	
	static {
		setUserModel(ModelFactory.getInstance().createUserModel());
	}
	
	/**
     * Servlet context parameter name for the Consumer Registration URL
     */
    final static String PARAM_CONSUMER_REGISTRATION_URL = "oauth.provider.consumer.registration";
	
	/**
	 * Servlet context parameter name for the Request Token distribution URL
	 */
	final static String PARAM_REQUEST_TOKEN_URL = "oauth.provider.tokens.request";

	/**
     * Servlet context parameter name for the Request Token authorization URL
     */
    final static String PARAM_TOKEN_AUTHORIZATION_URL = "oauth.provider.tokens.authorization";
	
	/**
	 * Servlet context parameter name for the Request Token echange URL
	 */
	final static String PARAM_ACCESS_TOKEN_URL = "oauth.provider.tokens.access";
	
	/**
	 * Servlet context parameter name for the OAuthProvider class name
	 */
	final static String PARAM_PROVIDER_CLASS = "oauth.provider.provider-class";
	
	/**
     * Relative path for the token authorization confirmation URL 
     */
    final static String CONSUMER_SCOPES_REGISTRATION_URL = "/consumer/scopes";
	
	/**
     * Relative path for the token authorization confirmation URL 
     */
    final static String TOKEN_AUTHORIZATION_CONFIRM_URL = "/authorization/confirm";
    
    /**
     * Default token authorization HTML resource 
     */
    final static String DEFAULT_TOKEN_HTML_RESOURCE = "/faces/views/login.xhtml";
    
    private String requestTokenURL, accessTokenURL, consumerRegistrationURL, authorizationURL;
    private OAuthProvider provider;
	private OAuthValidator validator;
	
	@Override
	public void init(ServletConfig config)
    throws ServletException {
		super.init(config);
		log.info("Loading OAuth Servlet");
		
		// load the context-parameters 
		ServletContext context = config.getServletContext();
		consumerRegistrationURL = context.getInitParameter(PARAM_CONSUMER_REGISTRATION_URL);
        if(consumerRegistrationURL == null)
            consumerRegistrationURL = "/consumer/registration";
		
        authorizationURL = context.getInitParameter(PARAM_TOKEN_AUTHORIZATION_URL);
        if(authorizationURL == null)
            authorizationURL = "/authorization";
        
		requestTokenURL = context.getInitParameter(PARAM_REQUEST_TOKEN_URL);
		if(requestTokenURL == null)
			requestTokenURL = "/requestToken";
		accessTokenURL = context.getInitParameter(PARAM_ACCESS_TOKEN_URL);
		if(accessTokenURL == null)
			accessTokenURL = "/accessToken";

		log.info("Request token URL: "+ requestTokenURL);
		log.info("Access token URL: "+ accessTokenURL);
		
		// now load the provider and validator
		provider = OAuthUtils.getOAuthProvider(context);
		validator = OAuthUtils.getValidator(context, provider);
		log.debug("OAuthServlet loaded");
	}
	
	@Override
	protected void service(HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException, 
	IOException{
		String pathInfo = req.getPathInfo();
		log.debug("Serving "+pathInfo);
		log.debug("Query "+req.getQueryString());
		if(pathInfo.equals(requestTokenURL))
			serveRequestToken(req, resp);
		else if(pathInfo.equals(accessTokenURL))
			serveAccessToken(req, resp);
		else if(pathInfo.equals(consumerRegistrationURL))
            serveConsumerRegistration(req, resp);
		else if(pathInfo.equals(authorizationURL))
            serveTokenAuthorization(req, resp);
		else if(pathInfo.startsWith(TOKEN_AUTHORIZATION_CONFIRM_URL)) 
		    serveTokenAuthorizationConfirmation(req, resp);
		else if(pathInfo.startsWith(CONSUMER_SCOPES_REGISTRATION_URL)) 
            serveConsumerScopesRegistrationRequest(req, resp);
		else
			resp.sendError(HttpURLConnection.HTTP_NOT_FOUND);
	}

	private void serveRequestToken(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		log.debug("Request token");
		OAuthMessage message = OAuthUtils.readMessage(req);
		try{
			// require some parameters
			message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
					OAuth.OAUTH_SIGNATURE_METHOD,
					OAuth.OAUTH_SIGNATURE,
					OAuth.OAUTH_TIMESTAMP,
					OAuth.OAUTH_NONCE);
			log.debug("Parameters present");

			String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
			// load the OAuth Consumer
			org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = provider.getConsumer(consumerKey);
			
			// create some structures for net.oauth
			OAuthConsumer _consumer = new OAuthConsumer(null, consumerKey, consumer.getSecret(), null);
			OAuthAccessor accessor = new OAuthAccessor(_consumer);
			
			// validate the message
			validator.validateMessage(message, accessor, null);

			// create a new Request Token
			String callbackURI = message.getParameter(OAuth.OAUTH_CALLBACK);
			if (callbackURI != null && consumer.getConnectURI() != null
			        && !callbackURI.startsWith(consumer.getConnectURI())) {
			    throw new OAuthException(400, "Wrong callback URI");
			}
			OAuthToken token = provider.makeRequestToken(consumerKey, 
			                            callbackURI, 
			                            req.getParameterValues("xoauth_scope"),
			                            req.getParameterValues("xoauth_permission"));

			// send the Token information to the Client
			OAuthUtils.sendValues(resp, OAuth.OAUTH_TOKEN, token.getToken(),OAuth.OAUTH_TOKEN_SECRET, 
					token.getSecret(), OAuthUtils.OAUTH_CALLBACK_CONFIRMED_PARAM, "true");
			resp.setStatus(HttpURLConnection.HTTP_OK);
			log.debug("All OK");

		} catch (OAuthException x) {
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), x.getHttpCode(), provider);
		} catch (OAuthProblemException x) {
			OAuthUtils.makeErrorResponse(resp, x.getProblem(), OAuthUtils.getHttpCode(x), provider);
		} catch (Exception x) {
			log.error("Exception ", x);
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
		}
	}

	private void serveAccessToken(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		log.debug("Access token");
		OAuthMessage message = OAuthUtils.readMessage(req);
		try{
			// request some parameters
			message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
					OAuth.OAUTH_TOKEN,
					OAuth.OAUTH_SIGNATURE_METHOD,
					OAuth.OAUTH_SIGNATURE,
					OAuth.OAUTH_TIMESTAMP,
					OAuth.OAUTH_NONCE,
					OAuthUtils.OAUTH_VERIFIER_PARAM);

			log.debug("Parameters present");
			
			// load some parameters
			String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
			String requestTokenString = message.getParameter(OAuth.OAUTH_TOKEN);
			String verifier = message.getParameter(OAuth.OAUTH_VERIFIER);
			
			// get the Request Token to exchange
			OAuthToken requestToken = provider.getRequestToken(consumerKey, requestTokenString);
			
			// build some structures for net.oauth
			OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, requestToken.getConsumer().getSecret(), null);
			OAuthAccessor accessor = new OAuthAccessor(consumer);
			accessor.requestToken = requestTokenString;
			accessor.tokenSecret = requestToken.getSecret();

			// verify the message signature
//			validator.validateMessage(message, accessor, requestToken);

			// exchange the Request Token
			OAuthToken tokens = provider.makeAccessToken(consumerKey, requestTokenString, verifier);

			// send the Access Token
			OAuthUtils.sendValues(resp, OAuth.OAUTH_TOKEN, tokens.getToken(),OAuth.OAUTH_TOKEN_SECRET, tokens.getSecret());
			resp.setStatus(HttpURLConnection.HTTP_OK);
			log.debug("All OK");

		} catch (OAuthException x) {
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), x.getHttpCode(), provider);
		} catch (OAuthProblemException x) {
			OAuthUtils.makeErrorResponse(resp, x.getProblem(), OAuthUtils.getHttpCode(x), provider);
		} catch (Exception x) {
			log.error("Exception ", x);
			OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
		}
	}
	
	private void serveConsumerRegistration(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        log.debug("Consumer registration");
        
        try{
            String[] values = req.getParameterValues(OAuth.OAUTH_CONSUMER_KEY);
            if (values == null || values.length != 1) {
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            
            String consumerKey = URLDecoder.decode(values[0], "UTF-8");
            String displayName = null;
            values = req.getParameterValues("xoauth_consumer_display_name");
            if (values != null && values.length == 1) {
                displayName = URLDecoder.decode(values[0], "UTF-8");
            }
            
            String connectURI = null;
            values = req.getParameterValues("xoauth_consumer_connect_uri");
            if (values != null && values.length == 1) {
                connectURI = URLDecoder.decode(values[0], "UTF-8");
            }
            
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = 
                provider.registerConsumer(consumerKey, displayName, connectURI);
            
            // send the shared key back to the registered consumer
            OAuthUtils.sendValues(resp, "xoauth_consumer_secret", consumer.getSecret());
            resp.setStatus(HttpURLConnection.HTTP_OK);
            log.debug("All OK");

        } catch (Exception x) {
            log.error("Exception ", x);
            OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
        }
    }
	
	/**
	 * Consumer scopes are URIs which the consumer will be able to access directly, by-passing the enduser
	 * authorization (aka OAuth 2-leg, as a replacement for Basic Auth)
	 * 
	 * This is strictly an administrative level operation and should be protected at the OAuthServlet level;
	 * Typically, it will be a domain administrator who will perform this request on behalf of the
	 * consumers.
	 * 
	 * TODO: add serveConsumerScopesRegistrationPage - which will offer a list of registered consumers
	 *       to admins; this page will eventually lead to serveConsumerScopesRegistrationRequest
	 * TODO: perhaps we may want to introduce a dedicated servlet dedicated to managing consumers 
     */
	private void serveConsumerScopesRegistrationRequest(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        log.debug("Consumer registration");
        
        try{
            String[] values = req.getParameterValues(OAuth.OAUTH_CONSUMER_KEY);
            if (values == null || values.length != 1) {
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            
            String consumerKey = URLDecoder.decode(values[0], "UTF-8");
            String[] scopes = req.getParameterValues("xoauth_scope");
            if (scopes != null) {
                provider.registerConsumerScopes(consumerKey, scopes);
            }
            
            String[] permissions = req.getParameterValues("xoauth_permission");
            if (permissions != null) {
                provider.registerConsumerPermissions(consumerKey, permissions);
            }
            
            resp.setStatus(HttpURLConnection.HTTP_OK);
            log.debug("All OK");

        } catch (Exception x) {
            log.error("Exception ", x);
            OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
        }
    }
	
	private void serveTokenAuthorization(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        log.debug("Consumer token authorization request");
        
        try{
            String[] values = req.getParameterValues(OAuth.OAUTH_TOKEN);
            if (values == null || values.length != 1) {
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            String requestTokenKey = values[0];
            
            OAuthRequestToken requestToken = provider.getRequestToken(null, requestTokenKey);
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = requestToken.getConsumer();
            
            // build the end user authentication and token authorization form
            String acceptHeader = req.getHeader("Accept");
            // TODO : properly check accept values, also support JSON
            String format = acceptHeader == null || acceptHeader.startsWith("application/xml") ? "xml" : "html";
            
            requestEndUserConfirmation(req, resp, consumer, requestToken, format);
            
        } catch (Exception x) {
            log.error("Exception ", x);
            OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
        }
    }
	
	private void requestEndUserConfirmation(HttpServletRequest req,
	                                        HttpServletResponse resp, 
	                                        org.jboss.resteasy.auth.oauth.OAuthConsumer consumer,
	                                        OAuthRequestToken requestToken,
	                                        String format) {
	    
	    if ("xml".equals(format))
	    {
	        // TODO : try to get a default XSLT template, if found then use it
	        // and only use in code formatting if no template is available
	        
            String uri = getAuthorizationConfirmURI(req, requestToken.getToken());
    	    StringBuilder sb = new StringBuilder();
    	    sb.append("<tokenAuthorizationRequest xmlns=\"http://org.jboss.com/resteasy/oauth\" ")
    	        .append("replyTo=\"").append(uri).append("\">");
    	    sb.append("<consumerId>").append(consumer.getKey()).append("</consumerId>");
    	    if (consumer.getDisplayName() != null) {
    	        sb.append("<consumerName>").append(consumer.getDisplayName()).append("</consumerName>");
    	    }
    	    if (requestToken.getScopes() != null) {
    	        sb.append("<scopes>").append(requestToken.getScopes()[0]).append("</scopes>");
    	    }
    	    if (requestToken.getPermissions() != null) {
                sb.append("<permissions>").append(requestToken.getPermissions()[0]).append("</permissions>");
            }
    	    sb.append("</tokenAuthorizationRequest>");
    	    try {
        	    resp.getWriter().append(sb.toString());
        	    resp.setStatus(HttpURLConnection.HTTP_OK);
    	    } catch (IOException ex) {
    	        resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
    	    }
	    } else if ("html".equals(format)) {
	        // TODO : try to get a default XSLT template creating XHTML output, if found then use it
            // and redirect if no template is available
	        RequestDispatcher dispatcher = req.getRequestDispatcher(DEFAULT_TOKEN_HTML_RESOURCE);
	        if (dispatcher == null) {
	            resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
	            return;
	        }
	        try {
	            req.setAttribute("oauth_consumer_id", consumer.getKey());
	            req.setAttribute("oauth_consumer_display", consumer.getDisplayName());
	            req.setAttribute("oauth_consumer_scopes", requestToken.getScopes());
	            req.setAttribute("oauth_consumer_permissions", requestToken.getPermissions());
	            req.setAttribute("oauth_request_token", requestToken.getToken());
	            req.setAttribute("oauth_token_confirm_uri", getAuthorizationConfirmURI(req, null));
	            dispatcher.forward(req, resp);
	        } catch (Exception ex) {
	            resp.setStatus(500);
	        }
	    }
	    //else if ("json".equals(format)) {
        //}
	}
	
	public String getAuthorizationConfirmURI(HttpServletRequest req, String tokenKey) {
	    String requestURI = req.getRequestURL().toString();
        int index = requestURI.lastIndexOf(authorizationURL);
        String baseURI = requestURI.substring(0, index);
        String uri = baseURI + TOKEN_AUTHORIZATION_CONFIRM_URL;
        if (tokenKey != null) 
        {
            uri += ("?" + OAuth.OAUTH_TOKEN + "=" + OAuthUtils.encodeForOAuth(tokenKey));
        }
        return uri;
	}
	
	private void serveTokenAuthorizationConfirmation(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        log.debug("Consumer registration");
        
        try{
            String[] values = req.getParameterValues(OAuth.OAUTH_TOKEN);
            if (values == null || values.length != 1) {
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            String requestTokenKey = values[0];
            
            OAuthRequestToken requestToken = provider.getRequestToken(null, requestTokenKey);
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = requestToken.getConsumer();
            
            values = req.getParameterValues("xoauth_end_user_decision");
            if (values == null || values.length != 1) {
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            
            boolean authorized = "yes".equals(values[0]) || "true".equals(values[0]);
            
            String callback = requestToken.getCallback();
            if (authorized) 
            {
            	OAuthCode oAuthCode = getUserModel().findOauthCode(requestTokenKey);
            	if(oAuthCode == null){
        			throw new OAuthException(400, "Invalid requestToken");
        		}
            	
            	String[] userValue = req.getParameterValues("userid");
            	oAuthCode.setUser(userValue[0]);
            	getUserModel().saveAuthorizationCode(oAuthCode);
            	
            	String verifier = provider.authoriseRequestToken(consumer.getKey(), requestToken.getToken());
                
                if (callback == null) {
                    OAuthUtils.sendValues(resp, OAuth.OAUTH_TOKEN, requestTokenKey, OAuth.OAUTH_VERIFIER, verifier);
                    resp.setStatus(HttpURLConnection.HTTP_OK);
                } else {
                    List<OAuth.Parameter> parameters = new ArrayList<OAuth.Parameter>();
                    parameters.add(new OAuth.Parameter(OAuth.OAUTH_TOKEN, requestTokenKey));
                    parameters.add(new OAuth.Parameter(OAuth.OAUTH_VERIFIER, verifier));
                    String location = OAuth.addParameters(callback, parameters);
                    resp.addHeader("Location", location);
                    resp.setStatus(302);
                }
            } 
            else
            {
                // TODO : make sure this response is OAuth compliant 
                OAuthUtils.makeErrorResponse(resp, "Token has not been authorized", 503, provider);
            }
            
            log.debug("All OK");

        } catch (Exception x) {
            log.error("Exception ", x);
            OAuthUtils.makeErrorResponse(resp, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
        }
    }

	public static UserModel getUserModel() {
		return userModel;
	}

	public static void setUserModel(UserModel userModel) {
		MaritacaOAuthServlet.userModel = userModel;
	}
}

