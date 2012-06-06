package br.unifesp.maritaca.web.oauth;

import java.io.IOException;
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
import br.unifesp.maritaca.business.oauth.OAuthEJB;

import br.unifesp.maritaca.business.oauth.dto.DataAccessTokenDTO;
import br.unifesp.maritaca.business.oauth.dto.OAuthClientDTO;
import br.unifesp.maritaca.business.oauth.dto.OAuthCodeDTO;
import br.unifesp.maritaca.business.oauth.dto.OAuthTokenDTO;
import br.unifesp.maritaca.persistence.util.ConstantsPersistence;
import br.unifesp.maritaca.web.utils.ConstantsWeb;
import br.unifesp.maritaca.web.utils.UtilsWeb;

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

	private static final String AUTHORIZATION_REQUEST = "/authorizationRequest";
	private static final String AUTHORIZATION_CONFIRM = "/authorizationConfirm";
	private static final String ACCESS_TOKEN_REQUEST  = "/accessTokenRequest";

	private static final String USER_PARAM            = "user";

	private OAuthAuthzRequest oauthRequest    = null;	
	private OAuthIssuerImpl   oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

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

		if (AUTHORIZATION_REQUEST.equals(pathInfo)) {
			authorize(request, response);
		} else if(AUTHORIZATION_CONFIRM.equals(pathInfo)) {
			authorizationConfirm(request, response);
		} else if(ACCESS_TOKEN_REQUEST.equals(pathInfo)) {
			accessToken(request, response);
		} else {
			response.setContentType("application/json");
			UtilsWeb.sendValuesInJson(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST),
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "Bad Request");
			response.setStatus(HttpURLConnection.HTTP_OK);
		}
	}

	/**
	 * This method allows to the third-party application request an authorization
	 * through a request URI by adding the following parameters: client_id, response_type
	 * and redirect_uri, these parameters are mandatory. If the client_id exist in the
	 * system this redirect to the user login to ask if he agrees. 
	 * @param request 
	 * @param response
	 */
	public void authorize(HttpServletRequest request, HttpServletResponse response){
		
		try {
			
			oauthRequest = new OAuthAuthzRequest(request);
			// TODO In Login. Have to validate the client_id and validate again in login 
			// Verify is the client_id exist, if not throw an exception
			String clientId = oauthRequest.getClientId();
			OAuthClientDTO clientDTO = oauthEJB.findOAuthClientByClientId(clientId);
			if(clientDTO == null) {
				UtilsWeb.makeResponseInJSON(response, 
						ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
						ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "client_id not found",
						ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}

			response.setStatus(HttpURLConnection.HTTP_OK);
			request.getRequestDispatcher(ConstantsWeb.MOBILE_LOGIN_URI).forward(request, response);
			
		} catch (OAuthProblemException e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, e.getDescription(),
					ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
		} catch (Exception e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "internal server error");
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
			
			String userId = oauthRequest.getParam(ConstantsWeb.OAUTH_USER_ID);
			oauthCodeDTO.setUser(userId);
			
			oauthCodeDTO.setClientId(oauthRequest.getClientId());
			
			oauthEJB.saveAuthorizationCode(oauthCodeDTO);
			
			UtilsWeb.sendValuesInJson(response, OAuth.OAUTH_CODE, oauthCodeDTO.getCode());
			String redirectURI = oauthRequest.getRedirectURI() + "?code=" + oauthCodeDTO.getCode();
			response.sendRedirect(redirectURI);
		} catch (OAuthProblemException e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, e.getDescription(),
					ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
		} catch (Exception e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "internal server error");
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
			DataAccessTokenDTO dataDTO = oauthEJB.findOAuthCodeAndClient(code, clientId);
			
			// verify OAuthCode
			OAuthCodeDTO oauthCodeDTO = dataDTO.getOauthCodeDTO();
			if(oauthCodeDTO == null) {
				UtilsWeb.makeResponseInJSON(response, 
						ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
						ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "Invalid authorization code",
						ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}
			if (!oauthCodeDTO.getClientId().equals(clientId)) {
				UtilsWeb.makeResponseInJSON(response, 
						ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
						ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "Invalid client_id",
						ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}
			
			// Verify OauthClient
			OAuthClientDTO clientDTO = dataDTO.getOauthClientDTO();
			if(clientDTO == null) {
				UtilsWeb.makeResponseInJSON(response, 
						ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_NO_CONTENT), 
						ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "client_id not found",
						ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
				return;
			}
			if (!clientDTO.getSecret().equals(oauthRequest.getClientSecret())) {
				UtilsWeb.makeResponseInJSON(response, 
						ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED), 
						ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "Invalid client_secret",
						ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
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
			UtilsWeb.sendValuesInJson(response, 
							 OAuth.OAUTH_ACCESS_TOKEN, tokenDTO.getAccessToken(),
							 OAuth.OAUTH_EXPIRES_IN, String.valueOf(ConstantsPersistence.OAUTH_EXPIRATION_DATE),
							 OAuth.OAUTH_REFRESH_TOKEN, tokenDTO.getRefreshToken(),
							 USER_PARAM, clientDTO.getUserEmail());
			response.setStatus(HttpURLConnection.HTTP_OK);
		} catch (OAuthProblemException e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, e.getDescription(),
					ConstantsWeb.OAUTH_ERROR_URI, request.getParameter(OAuth.OAUTH_REDIRECT_URI));
		} catch (Exception e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "internal server error");
			throw new MaritacaException(e.getMessage());
		}
	}
}
