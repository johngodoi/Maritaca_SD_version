package br.unifesp.maritaca.mobile.activities;

import static br.unifesp.maritaca.mobile.util.Constants.ACCESS_TOKEN_REQUEST;
import static br.unifesp.maritaca.mobile.util.Constants.CLIENT_URL;
import static br.unifesp.maritaca.mobile.util.Constants.MARITACA_MOBILE;
import static br.unifesp.maritaca.mobile.util.Constants.MARITACA_SECRET;
import static br.unifesp.maritaca.mobile.util.Constants.RESPONSE_TYPE;
import static br.unifesp.maritaca.mobile.util.Constants.OAUTH_URL;
import br.unifesp.maritaca.mobile.util.OAuthTokenManager;
import net.smartam.leeloo.client.OAuthClient;
import net.smartam.leeloo.client.URLConnectionClient;
import net.smartam.leeloo.client.request.OAuthClientRequest;
import net.smartam.leeloo.client.response.OAuthJSONAccessTokenResponse;
import net.smartam.leeloo.common.message.types.GrantType;
import net.smartam.leeloo.common.message.types.ResponseType;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OAuthWebViewClient extends WebViewClient {
	
	private Handler handler;	
	
	public OAuthWebViewClient(Handler handler){
		setHandler(handler);
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if(url.matches(".*code=.*")){
			String code = url.split("=")[1];			
			retrieveAccessToken(code);
			
			return true;
		} else {
			return super.shouldOverrideUrlLoading(view, url);
		}		
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	private void retrieveAccessToken(String code) {
		try {
			OAuthClientRequest request = OAuthClientRequest
					.tokenLocation(OAUTH_URL + ACCESS_TOKEN_REQUEST)
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.setClientId(MARITACA_MOBILE)
					.setClientSecret(MARITACA_SECRET)
					.setRedirectURI(CLIENT_URL).setCode(code)
					.setParameter(RESPONSE_TYPE, ResponseType.TOKEN.toString())
					.buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			
			Log.d(OAuthWebViewClient.class.getName(),
					"Retrieving access token from: "+request.getLocationUri());
			OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
					.accessToken(request, OAuthJSONAccessTokenResponse.class);

			String TOKEN   = oAuthResponse.getAccessToken();
			String EXPIRES = oAuthResponse.getExpiresIn();
			
			Log.i(OAuthWebViewClient.class.getName(), "Received token: " + TOKEN
					+ ", expires in: " + EXPIRES + " seconds.");
						
			OAuthTokenManager.saveToken(oAuthResponse);
			
			getHandler().sendEmptyMessage(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
