package br.unifesp.maritaca.web.oauth;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.smartam.leeloo.as.issuer.MD5Generator;
import net.smartam.leeloo.as.issuer.OAuthIssuerImpl;
import net.smartam.leeloo.as.request.OAuthAuthzRequest;
import net.smartam.leeloo.common.OAuth;
import net.smartam.leeloo.common.exception.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.exception.MaritacaException;
import br.unifesp.maritaca.business.oauth.DataAccessTokenDTO;
import br.unifesp.maritaca.business.oauth.OAuthClientDTO;
import br.unifesp.maritaca.business.oauth.OAuthCodeDTO;
import br.unifesp.maritaca.business.oauth.OAuthEJB;
import br.unifesp.maritaca.business.oauth.OAuthTokenDTO;
import br.unifesp.maritaca.util.ConstantsCore;

/**
 * This Servlet provides the OAuth2 Services and generate the access code and
 * access token to the third-party application to obtain limited access to
 * the restful services.
 * 
 * @author alvarohenry
 */
public class AuthorizationServer extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AuthorizationServer.class);

	private static final String WEB_LOGIN_URI = "/faces/views/login.xhtml";

	private static final String AUTHORIZATION_REQUEST = "/authorizationRequest";
	private static final String AUTHORIZATION_CONFIRM = "/authorizationConfirm";
	private static final String ACCESS_TOKEN_REQUEST = "/accessTokenRequest";

	private static final String OAUTH_USER_ID = "user_id";
	private static final String OAUTH_ERROR_URI = "error_uri";
	private static final String OAUTH_ERROR_DESCRIPTION = "error_description";
	private static final String OAUTH_ERROR = "error";

	private OAuthAuthzRequest oauthRequest = null;	
	private OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

	@Inject
	private OAuthEJB oauthEJB;
	
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

		if (pathInfo.equals(AUTHORIZATION_REQUEST)) {
			authorize(request, response);
		} else if(pathInfo.equals(AUTHORIZATION_CONFIRM)) {
			authorizationConfirm(request, response);
		} else if(pathInfo.equals(ACCESS_TOKEN_REQUEST)) {
			accessToken(request, response);
		} else {
			response.setContentType("application/json");
			sendValuesInJson(response, 
							 OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST),
							 OAUTH_ERROR_DESCRIPTION, "Bad Request");
			response.setStatus(HttpURLConnection.HTTP_OK);
		}
	}

	/**
	 * This method allows to the third-party application request an authorization
	 * through a request URI by adding the following parameters: client_id, response_type
	 * and redirect_uri, these parameters are mandatory. If the client_id exist in the
	 * system this redirect to the user login to ask if it's agree. 
	 * @param request 
	 * @param response
	 */
	public void authorize(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			oauthRequest = new OAuthAuthzRequest(request);
			
			// Verify is the client_id exist, if not throw an exception
			String clientId = oauthRequest.getClientId();
			OAuthClientDTO clientDTO = oauthEJB.findOAuthClientByClientId(clientId);
			if(clientDTO == null) {
				treatException(response, 
								OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
								OAUTH_ERROR_DESCRIPTION, "client_id not found",
								OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}

			response.setStatus(HttpURLConnection.HTTP_OK);
			request.getRequestDispatcher(WEB_LOGIN_URI).forward(request, response);
			
		} catch (OAuthProblemException e) {
			treatException(response, 
					OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), 
					OAUTH_ERROR_DESCRIPTION, e.getDescription(),
					OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
		} catch (Exception e) {
			treatException(response, 
					OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR), 
					OAUTH_ERROR_DESCRIPTION, "internal server error");
			throw new MaritacaException(e.getMessage());
		}
	}

	/**
	 * This method is called when the resource owner grants the client's
	 * access request. In this method also is generated the authorization 
	 * code and save temporarily in the DB.
	 * 
	 * If the resource owner denies: TODO
	 * @param request
	 * @param response
	 */
	private void authorizationConfirm(HttpServletRequest request, HttpServletResponse response) {
		try {
			oauthRequest = new OAuthAuthzRequest(request);
			
			OAuthCodeDTO oauthCodeDTO = new OAuthCodeDTO();
			oauthCodeDTO.setCode(oauthIssuerImpl.authorizationCode());
			
			String userId = oauthRequest.getParam(OAUTH_USER_ID);
			oauthCodeDTO.setUser(userId);
			
			oauthCodeDTO.setClientId(oauthRequest.getClientId());
			
			oauthEJB.saveAuthorizationCode(oauthCodeDTO);
			
			sendValuesInJson(response, OAuth.OAUTH_CODE, oauthCodeDTO.getCode());
			String redirectURI = oauthRequest.getRedirectURI() + "?code=" + oauthCodeDTO.getCode();
			response.sendRedirect(redirectURI);
		} catch (OAuthProblemException e) {
			treatException(response, 
							OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), 
							OAUTH_ERROR_DESCRIPTION, e.getDescription(),
							OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
		} catch (Exception e) {
			treatException(response, 
					OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR), 
					OAUTH_ERROR_DESCRIPTION, "internal server error");
			throw new MaritacaException(e.getMessage());
		}
	}
	
	/**
	 * This method generate an access and refresh token to the third-party application, 
	 * the request HAVE TO be a POST request.
	 * @param request
	 * @param response
	 */
	public void accessToken(HttpServletRequest request, HttpServletResponse response) {
		try {
			oauthRequest = new OAuthAuthzRequest(request);
			
			String code = oauthRequest.getParam(OAuth.OAUTH_CODE);
			String clientId = oauthRequest.getClientId();
			DataAccessTokenDTO dataDTO = 
					oauthEJB.findOAuthCodeAndClient(code, clientId);
			
			// verify OAuthCode
			OAuthCodeDTO oauthCodeDTO = dataDTO.getOauthCodeDTO();
			if(oauthCodeDTO == null) {
				treatException(response, 
								OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
								OAUTH_ERROR_DESCRIPTION, "Invalid authorization code",
								OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}
			if (!oauthCodeDTO.getClientId().equals(clientId)) {
				treatException(response, 
						OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
						OAUTH_ERROR_DESCRIPTION, "Invalid client_id",
						OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}
			
			// Verify OauthClient
			OAuthClientDTO clientDTO = dataDTO.getOauthClientDTO();
			if(clientDTO == null) {
				treatException(response, 
								OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
								OAUTH_ERROR_DESCRIPTION, "client_id not found",
								OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}
			if (!clientDTO.getSecret().equals(oauthRequest.getClientSecret())) {
				treatException(response, 
						OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED), 
						OAUTH_ERROR_DESCRIPTION, "Invalid client_secret",
						OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}
			
			// If all is correct :) Generate Tokens		
			OAuthTokenDTO tokenDTO = new OAuthTokenDTO();
			tokenDTO.setAccessToken(oauthIssuerImpl.accessToken());
			tokenDTO.setRefreshToken(oauthIssuerImpl.refreshToken());
			tokenDTO.setUser(oauthCodeDTO.getUser());
			tokenDTO.setClientId(clientDTO.getClientId()); 
			
			oauthEJB.saveOAuthToken(tokenDTO);
			
			response.setContentType("application/json");
			sendValuesInJson(response, 
							 OAuth.OAUTH_ACCESS_TOKEN, tokenDTO.getAccessToken(),
							 OAuth.OAUTH_EXPIRES_IN, String.valueOf(ConstantsCore.OAUTH_EXPIRATION_DATE),
							 OAuth.OAUTH_REFRESH_TOKEN, tokenDTO.getRefreshToken());
			response.setStatus(HttpURLConnection.HTTP_OK);
		} catch (OAuthProblemException e) {
			treatException(response, 
							OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), 
							OAUTH_ERROR_DESCRIPTION, e.getDescription(),
							OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
		} catch (Exception e) {
			treatException(response, 
					OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR), 
					OAUTH_ERROR_DESCRIPTION, "internal server error");
			throw new MaritacaException(e.getMessage());
		}
	}

	/**
	 * This method writes, in JSON format, params in the HttpServletResponse.
	 * @param response
	 * @param params
	 * @throws IOException
	 */
	private void sendValuesInJson(HttpServletResponse response, String... params) {
		PrintWriter printWriter;
		try {
			printWriter = response.getWriter();
			if (params.length % 2 != 0) {
				throw new IllegalArgumentException("Arguments should be name=value*");
			} 
			
			printWriter.append('{');
			for (int i = 0; i < params.length; i+=2) {
				if (i > 0) {
					printWriter.append(',');
				}
				printWriter.append('"');
				printWriter.append(params[i]);
				printWriter.append('"');
				printWriter.append(':');
				printWriter.append('"');
				printWriter.append(params[i+1]);
				printWriter.append('"');
			}
			printWriter.append('}');
		} catch (IOException e) {			
			throw new MaritacaException(e.getMessage());
		}
	}
	
	/**
	 * This method treats to catch exceptions and send back to the
	 * user an specific message. 
	 * @param response
	 * @param params
	 */
	private void treatException(HttpServletResponse response, String... params) {
		HttpServletResponse response1 = response;
		response1.setContentType("application/json");
		sendValuesInJson(response1, params);
		response1.setStatus(HttpURLConnection.HTTP_OK);
	}
	
}
