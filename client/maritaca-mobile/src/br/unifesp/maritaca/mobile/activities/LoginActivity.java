package br.unifesp.maritaca.mobile.activities;

import static br.unifesp.maritaca.mobile.util.Constants.AUTHORIZATION_REQUEST;
import static br.unifesp.maritaca.mobile.util.Constants.CLIENT_URL;
import static br.unifesp.maritaca.mobile.util.Constants.MARITACA_MOBILE;
import static br.unifesp.maritaca.mobile.util.Constants.SERVER_URL;
import br.unifesp.maritaca.mobile.util.OAuthTokenManager;
import net.smartam.leeloo.client.request.OAuthClientRequest;
import net.smartam.leeloo.common.exception.OAuthSystemException;
import net.smartam.leeloo.common.message.types.ResponseType;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends Activity {
	
	private WebView webView;
	
	private final Handler tokenReceiverHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==0){
				closeBrowserWindow();
			}
		};
	};
	
	/**
	 * Performs the initial oauth request opening the embedded
	 * browser in the oauth login page.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		try {
			OAuthClientRequest request = OAuthClientRequest
					.authorizationLocation(SERVER_URL + AUTHORIZATION_REQUEST)
					.setClientId(MARITACA_MOBILE).setRedirectURI(CLIENT_URL)
					.setResponseType(ResponseType.CODE.toString())
					.buildQueryMessage();
		
			openBrowserInUrl(request.getLocationUri());			
		} catch (OAuthSystemException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Opens the embedded web kit browser window 
	 * @param url
	 */
	private void openBrowserInUrl(String url) {
		Log.d(this.getClass().getName(), "Acessing URL: "+url);
						
		setWebView((WebView) findViewById(R.id.web_engine));
		
		WebViewClient maritacaViewClient = new OAuthWebViewClient(tokenReceiverHandler); 		
		getWebView().setWebViewClient(maritacaViewClient);
		
		getWebView().getSettings().setJavaScriptEnabled(true);
		getWebView().loadUrl(url);
	}

	/**
	 * Closes the embedded web kit browser window 
	 */
	private void closeBrowserWindow(){
		getWebView().setVisibility(View.GONE);
		OAuthTokenManager.loadTokenFile();
		setContentView(R.layout.menu);		
	}

	public WebView getWebView() {
		return webView;
	}

	public void setWebView(WebView webView) {
		this.webView = webView;
	}
}