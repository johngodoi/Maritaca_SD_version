package br.unifesp.maritaca.ws.oauth.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;

import org.apache.avro.ipc.Responder;

import com.sun.jersey.api.core.HttpContext;

import net.smartam.leeloo.as.issuer.MD5Generator;
import net.smartam.leeloo.as.issuer.OAuthIssuer;
import net.smartam.leeloo.as.issuer.OAuthIssuerImpl;
import net.smartam.leeloo.as.request.OAuthAuthzRequest;
import net.smartam.leeloo.as.request.OAuthTokenRequest;
import net.smartam.leeloo.as.response.OAuthASResponse;
import net.smartam.leeloo.common.OAuth;
import net.smartam.leeloo.common.message.OAuthResponse;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.ws.oauth.api.AuthorizationServer;
import br.unifesp.maritaca.ws.util.UtilsWS;

@Path("/oauth")
public class AuthorizationServerImpl implements AuthorizationServer {

	private static final String EXPIRATION_TIME = "3600";
	private final  String    WEB_LOGIN_URI = "/faces/views/login.xhtml";
	private static UserModel userModel;
	
	static{
		setUserModel(ModelFactory.getInstance().createUserModel());
	}
	
	@Override
	public void requestAuthorizationCode(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String redirectUrl = UtilsWS.buildApplicationUrl(request, WEB_LOGIN_URI);
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void generateAuthorizationCode(HttpServletRequest request,
			HttpServletResponse response) {
		
		try {
			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
			
			OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
			String          authCode        = oauthIssuerImpl.authorizationCode();
			getUserModel().saveAuthorizationCode(authCode);
			
			String redirectURI = oauthRequest
					.getParam(OAuth.OAUTH_REDIRECT_URI);
			
			// build OAuth response
			OAuthResponse resp = OAuthASResponse
						.authorizationResponse(HttpServletResponse.SC_FOUND)
						.setCode(authCode)
						.location(redirectURI).buildQueryMessage();
			
			response.sendRedirect(resp.getLocationUri());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void generateAccessToken(HttpContext request,
			HttpServletResponse response) {
		OAuthTokenRequest oauthRequest = null;

		OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

		try {
			Map map = request.getRequest().getFormParameters(); 
				
//			oauthRequest = new OAuthTokenRequest(request.getRequest());

			String authCode = oauthRequest.getCode();
			if( getUserModel().isRegisteredOAuthCode(authCode) != true) {
				return;
			}
			

			String accessToken = oauthIssuerImpl.accessToken();
			String refreshToken = oauthIssuerImpl.refreshToken();

			OAuthResponse oAuthResponse = OAuthASResponse
					.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken).setExpiresIn(EXPIRATION_TIME)
					.setRefreshToken(refreshToken).buildBodyMessage();

			response.setStatus(oAuthResponse.getResponseStatus());
			PrintWriter pw = response.getWriter();
			pw.print(oAuthResponse.getBody());
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static UserModel getUserModel() {
		return userModel;
	}

	public static void setUserModel(UserModel userModel) {
		AuthorizationServerImpl.userModel = userModel;
	}

}
