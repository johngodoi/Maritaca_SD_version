package br.unifesp.maritaca.web.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.auth.oauth.OAuthValidator;
import org.jboss.resteasy.auth.oauth.OAuthException;

/**
 * OAuth Servlet Filter that interprets OAuth Authentication messages to set the Principal and roles
 * for protected resources. 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class MaritacaOAuthFilter implements Filter {

	public static final String OAUTH_AUTH_METHOD = "OAuth";

	private static final Log log = LogFactory.getLog(MaritacaOAuthFilter.class);

	private OAuthProvider provider;
	private OAuthValidator validator;

	public void init(FilterConfig config) throws ServletException {
		log.info("Loading OAuth Filter");
		ServletContext context = config.getServletContext();
		provider = OAuthUtils.getOAuthProvider(context);
		validator = OAuthUtils.getValidator(context, provider);
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		_doFilter((HttpServletRequest)request, (HttpServletResponse)response, filterChain);
	}
	
	protected void _doFilter(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
	    
	    log.debug("Filtering " + request.getMethod() + " " + request.getRequestURL().toString());

		OAuthMessage message = OAuthUtils.readMessage(request);
        try{

            message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
                    OAuth.OAUTH_SIGNATURE_METHOD,
                    OAuth.OAUTH_SIGNATURE,
                    OAuth.OAUTH_TIMESTAMP,
                    OAuth.OAUTH_NONCE);

            String consumerKey = message.getParameter(OAuth.OAUTH_CONSUMER_KEY);
            org.jboss.resteasy.auth.oauth.OAuthConsumer consumer = provider.getConsumer(consumerKey);
        
            OAuthToken accessToken = null;
            String accessTokenString = message.getParameter(OAuth.OAUTH_TOKEN);
            
            if (accessTokenString != null) { 
                accessToken = provider.getAccessToken(consumer.getKey(), accessTokenString);
//                OAuthUtils.validateRequestWithAccessToken(
//                        request, message, accessToken, validator, consumer);
            } else {
                OAuthUtils.validateRequestWithoutAccessToken(
                        request, message, validator, consumer);
            }
            
            request = createSecurityContext(request, consumer, accessToken);
            
            // let the request through with the new credentials
            log.debug("doFilter");
            filterChain.doFilter(request, response);
            
        } catch (OAuthException x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), x.getHttpCode(), provider);
        } catch (OAuthProblemException x) {
            OAuthUtils.makeErrorResponse(response, x.getProblem(), OAuthUtils.getHttpCode(x), provider);
        } catch (Exception x) {
            OAuthUtils.makeErrorResponse(response, x.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, provider);
        }
		
	}

	protected OAuthProvider getProvider() {
	    return provider;
	}
	
	
	protected HttpServletRequest createSecurityContext(HttpServletRequest request, 
	                                                   org.jboss.resteasy.auth.oauth.OAuthConsumer consumer,
	                                                   OAuthToken accessToken) 
	{
	    return request;
	}
}
