package br.unifesp.maritaca.oauth.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response.Status;

import br.unifesp.maritaca.core.OAuthClient;
import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.oauth.api.AuthorizationServer;
import br.unifesp.maritaca.util.Utils;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;
import br.unifesp.maritaca.ws.util.UtilsWS;

@Path("/oauth")
public class AuthorizationServerImpl implements AuthorizationServer {

	private final String WEB_LOGIN_URI = "/faces/views/login.xhtml";
	private static UserModel userModel;

	static {
		setUserModel(ModelFactory.getInstance().createUserModel());
	}

	@Override
	public void requestAuthorizationCode(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			MaritacaWSException, ServletException {
			
			String clientId = request.getParameter("client_id");
			if(clientId == null){
				throw UtilsWS.buildMaritacaWSException(Status.BAD_REQUEST,"missing parameter");
			}
			
			OAuthClient client = userModel.findOauthClient(clientId);
			
			if(client == null){
				throw UtilsWS.buildMaritacaWSException(Status.BAD_REQUEST,"client not found");
			}
			
			request.getRequestDispatcher(WEB_LOGIN_URI).forward(request, response);
	}

	@Override
	public void generateAuthorizationCode(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		OAuthCode oauthCode = new OAuthCode();
		oauthCode.setCode(Utils.randomString());
		//TODO: verify if code exists
		
		String user = request.getParameter("userid");
		oauthCode.setUser(user);
		
		oauthCode.setClientId(request.getParameter("client_id"));
		getUserModel().saveAuthorizationCode(oauthCode);

		String redirectURI = request.getParameter("redirect_uri");
		response.sendRedirect(redirectURI +"?code="+oauthCode.getCode());

	}

	@Override
	public OAuthToken generateAccessToken(String grantType, String clientId,
			String clientSecret, String code, String redirectUri)throws MaritacaWSException{
		if (grantType == null || clientId == null || clientSecret == null
				|| code == null || redirectUri == null) {
			throw UtilsWS.buildMaritacaWSException(Status.BAD_REQUEST,"missing parameters");
		}
		
		OAuthCode oAuthCode = userModel.findOauthCode(code);
		if(oAuthCode == null){
			//code not registered
			throw UtilsWS.buildMaritacaWSException(Status.BAD_REQUEST,"code not registered");
		}
		
		if(!oAuthCode.getClientId().equals(clientId)){
			throw UtilsWS.buildMaritacaWSException(Status.BAD_REQUEST,"client id not valid for auth code");
		}
		
		OAuthClient oAuthClient = userModel.findOauthClient(clientId);
		if(!oAuthClient.getSecret().equals(clientSecret)){
			throw UtilsWS.buildMaritacaWSException(Status.BAD_REQUEST,"secret does not match");
		}
		
		
		//generate tokens
		OAuthToken authToken = new OAuthToken();
		authToken.setAccessToken(Utils.randomString());
		authToken.setRefreshToken(Utils.randomString());
		//TODO: verify if tokens exist
		authToken.setUser(oAuthCode.getUser());
		
		userModel.saveAuthToken(authToken);
		return authToken;
	}

	public static UserModel getUserModel() {
		return userModel;
	}

	public static void setUserModel(UserModel userModel) {
		AuthorizationServerImpl.userModel = userModel;
	}

}
