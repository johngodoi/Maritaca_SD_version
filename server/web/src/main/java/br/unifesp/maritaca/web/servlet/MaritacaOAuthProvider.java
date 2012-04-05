package br.unifesp.maritaca.web.servlet;

import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.auth.oauth.OAuthConsumer;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthRequestToken;
import org.jboss.resteasy.auth.oauth.OAuthToken;

import br.unifesp.maritaca.core.OAuthClient;
import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.util.UtilsCore;

/**
 * Maritaca OAuth Provider
 * This class has almost all methods to have authentication throught OAuth protocol
 *   
 */
public class MaritacaOAuthProvider implements OAuthProvider {
	
	private static final Log log = LogFactory.getLog(MaritacaOAuthProvider.class);
	private static final int TOKEN_LENGTH = 64;
	private static UserModel userModel;

	@Override
	public OAuthConsumer registerConsumer(String consumerKey,
			String displayName, String connectURI) throws OAuthException {
		log.info("in registerConsumer");
		if (userModel == null) {
			userModel = ModelFactory.getInstance().createUserModel();
		}
		
		// TODO Implement the method to persist a OAuthClient = OAuthConsumer
		
		return null;
	}

	@Override
	public void registerConsumerScopes(String consumerKey, String[] scopes)
			throws OAuthException {
		log.info("in registerConsumerScopes");
		
	}

	@Override
	public void registerConsumerPermissions(String consumerKey,
			String[] permissions) throws OAuthException {
		log.info("in registerConsumerPermissions");
		
	}

	@Override
	public String getRealm() {
		log.info("in getRealm");
		return "default";
	}

	@Override
	public OAuthConsumer getConsumer(String consumerKey) throws OAuthException {
		log.info("in getConsumer");
		if (userModel == null) {
			userModel = ModelFactory.getInstance().createUserModel();
		}

		OAuthClient client = userModel.findOauthClient(consumerKey);
		
		String key = client.getClientId();
		String secret = client.getSecret();
		String displayName = null;
		String connectURI = null;
		OAuthConsumer authConsumer = new OAuthConsumer(key, secret, displayName, connectURI);
		
		return authConsumer;
	}

	@Override
	public OAuthRequestToken getRequestToken(String consumerKey,
			String requestToken) throws OAuthException {
		log.info("in getRequestToken");
		
		OAuthCode oAuthCode = userModel.findOauthCode(requestToken);
		
		OAuthConsumer consumer = getConsumer(oAuthCode.getClientId());
		
		//OAuthRequestToken(token, secret, callback, null, null, 3600, consumer);
		OAuthRequestToken authRequestToken = new OAuthRequestToken(
				requestToken, 
				consumer.getSecret(), 
				null , 
				null, null, 
				3600, 
				consumer);
		
		return authRequestToken;
	}

	@Override
	public OAuthToken getAccessToken(String consumerKey, String accessToken)
			throws OAuthException {
		log.info("in getAccessToken");
		
		OAuthConsumer consumer = getConsumer(consumerKey); 
		if(consumer == null){
			throw new OAuthException(Status.BAD_REQUEST.ordinal(),"Consumer Key is not valid");
		}
		
		br.unifesp.maritaca.core.OAuthToken token =
				userModel.getOAuthTokenByAccessToken(accessToken);
		if(!token.getClientId().equals(consumerKey)){
			throw new OAuthException(Status.BAD_REQUEST.ordinal(),"Consumer Key is not valid");
		}
		if(!token.getAccessToken().equals(accessToken)){
			throw new OAuthException(Status.BAD_REQUEST.ordinal(),"Invalid access token");
		}
		
		OAuthToken authToken = new OAuthToken(
				token.getAccessToken(), 
				consumer.getSecret(), 
				null, null, 
				3600, 
				consumer);
		
		return authToken;
	}

	@Override
	public OAuthToken makeRequestToken(String consumerKey, String callback,
			String[] scopes, String[] permissions) throws OAuthException {
		log.info("in makeRequestToken");
		
		OAuthCode oauthCode = new OAuthCode();
		oauthCode.setCode(UtilsCore.randomString());
		oauthCode.setClientId(consumerKey);
		getUserModel().saveAuthorizationCode(oauthCode);
		
		OAuthConsumer consumer = getConsumer(consumerKey); 
		
		// OAuthToken(token, secret, scopes, permissions, timeToLive, consumer)
		OAuthToken oAuthToken = new OAuthToken(
				oauthCode.getCode(), 
				consumer.getSecret(), 
				null, 
				null, 
				3600, 
				consumer); 
		
		return oAuthToken;
	}

	@Override
	public OAuthToken makeAccessToken(String consumerKey, String requestToken,
			String verifier) throws OAuthException {

		log.info("in makeAccessToken");
		
		OAuthCode oAuthCode = userModel.findOauthCode(requestToken);
		if(oAuthCode == null){
			throw new OAuthException(Status.BAD_REQUEST.ordinal(),"code not registered");
		}
		
		if (!oAuthCode.getClientId().equals(consumerKey)) {
			throw new OAuthException(Status.BAD_REQUEST.ordinal(),"ConsumerKey is not valid");
		}
		
		if (!oAuthCode.getVerificationCode().equals(verifier)) {
			throw new OAuthException(Status.BAD_REQUEST.ordinal(),"Verification Code is not valid");
		}
		
		br.unifesp.maritaca.core.OAuthToken accessToken =
				new br.unifesp.maritaca.core.OAuthToken();
		accessToken.setAccessToken(UtilsCore.randomString(TOKEN_LENGTH));
		accessToken.setRefreshToken(UtilsCore.randomString(TOKEN_LENGTH));
		accessToken.setUser(oAuthCode.getUser());
		accessToken.setClientId(consumerKey);
		userModel.saveAuthToken(accessToken);
		
		OAuthConsumer consumer = getConsumer(consumerKey); 
		OAuthToken authToken = new OAuthToken(
				accessToken.getAccessToken(), 
				accessToken.getRefreshToken(), 
				null, null, 
				3600, 
				consumer);
		
		return authToken;
	}

	@Override
	public String authoriseRequestToken(String consumerKey, String requestToken)
			throws OAuthException {
		
		log.info("in authoriseRequestToken");
		
		OAuthCode oAuthCode = userModel.findOauthCode(requestToken);
		if(oAuthCode == null){
			throw new OAuthException(400, "Invalid requestToken");
		}
		
		OAuthConsumer consumer = getConsumer(oAuthCode.getClientId());
		String consumerKeyOAuth = consumer.getKey(); 
		if(!consumerKey.equals(consumerKeyOAuth)){
			throw new OAuthException(Status.BAD_REQUEST.ordinal(),"client id not valid for auth code");
		}

		oAuthCode.setVerificationCode(UtilsCore.randomString(TOKEN_LENGTH));
		getUserModel().saveAuthorizationCode(oAuthCode);
		
		return oAuthCode.getVerificationCode();
	}

	@Override
	public void checkTimestamp(OAuthToken token, long timestamp)
			throws OAuthException {
		log.info("in checkTimestamp");
		
	}

	@Override
	public Set<String> convertPermissionsToRoles(String[] permissions) {
		log.info("in convertPermissionsToRoles");
		return null;
	}

	public static UserModel getUserModel() {
		return userModel;
	}

	public static void setUserModel(UserModel userModel) {
		MaritacaOAuthProvider.userModel = userModel;
	}

}
