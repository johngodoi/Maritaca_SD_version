package br.unifesp.maritaca.web.oauth;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import net.smartam.leeloo.as.issuer.MD5Generator;
import net.smartam.leeloo.as.issuer.OAuthIssuerImpl;
import net.smartam.leeloo.as.request.OAuthAuthzRequest;
import net.smartam.leeloo.as.response.OAuthASResponse;
import net.smartam.leeloo.common.OAuth;
import net.smartam.leeloo.common.exception.OAuthProblemException;
import net.smartam.leeloo.common.exception.OAuthSystemException;
import net.smartam.leeloo.common.message.OAuthResponse;
import net.smartam.leeloo.common.message.types.ResponseType;
import net.smartam.leeloo.common.utils.OAuthUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This Servlet provides the OAuth2 Service
 * 
 * @author alvaro
 */
public class AuthorizationServer extends HttpServlet {

	private static final int EXPIRATION_TIME = 3600;

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(AuthorizationServer.class);
	
	private OAuthAuthzRequest oauthRequest = null;
	
	private OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

	private String authorizationRequest = "/authorizationRequest";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		log.info("Loading OAuth AuthorizationServer Servlet");
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String pathInfo = request.getPathInfo();
		log.debug("Serving " + pathInfo);
		log.debug("Query " + request.getQueryString());
		
		try {
			if (pathInfo.equals(authorizationRequest)) {
				authorize(request, response);
			} else if(pathInfo.equals(authorizationRequest)) {
				return;
			}
		} catch (Exception e) {
		}
	}
	
	public void authorize(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			oauthRequest = new OAuthAuthzRequest(request);
			
			String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
			
			OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
					OAuthASResponse.authorizationResponse(HttpServletResponse.SC_FOUND);
			
			if (responseType.equals(ResponseType.CODE.toString()) || 
					responseType.equals(ResponseType.CODE_AND_TOKEN.toString())) {
				builder.setCode(oauthIssuerImpl.authorizationCode());
			}
			
			if (responseType.equals(ResponseType.TOKEN.toString()) ||
					responseType.equals(ResponseType.CODE_AND_TOKEN.toString())) {
				builder.setAccessToken(oauthIssuerImpl.accessToken());
				builder.setExpiresIn(String.valueOf(EXPIRATION_TIME));
			}
			
			String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
			
//			final OAuthResponse oauthResponse = builder.location(redirectURI).buildQueryMessage();
//			URI url = new URI(oauthResponse.getLocationUri());
			
//			this.sendValues(response, OAuth.OAUTH_CLIENT_ID, )
//			resp.setStatus(response.getResponseStatus());
			
		} catch (OAuthProblemException e) {
			try{
				final Response.ResponseBuilder responseBuilder = Response.status(HttpServletResponse.SC_FOUND);
	
	            String redirectUri = e.getRedirectUri();
	
	            if (OAuthUtils.isEmpty(redirectUri)) {
	                throw new WebApplicationException(
	                    responseBuilder.entity("OAuth callback url needs to be provided by client!!!").build());
	            }
	            final OAuthResponse oauthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
	                .error(e)
	                .location(redirectUri).buildQueryMessage();
	            final URI location = new URI(oauthResponse.getLocationUri());
	            responseBuilder.location(location).build();
			} catch (OAuthSystemException ex ){
				
			} catch (URISyntaxException ex) {
				
			}
		} catch (OAuthSystemException e) {
			
		}
	}
	
	private void sendValues(HttpServletResponse response, String... params)
			throws IOException {
		PrintWriter printWriter = response.getWriter();
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException("Arguments should be name=value*");
		} 

		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				printWriter.append('&');
			}
			printWriter.append(params[i]);
			printWriter.append('=');
			printWriter.append(params[i+1]);
		}
	}

}
