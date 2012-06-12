package br.com.maritaca.activity;

import net.smartam.leeloo.client.request.OAuthClientRequest;
import net.smartam.leeloo.common.exception.OAuthSystemException;
import net.smartam.leeloo.common.message.types.ResponseType;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import br.com.maritaca.oauth.OAuthTokenManager;
import br.com.maritaca.oauth.OAuthWebViewClient;
import br.org.maritaca.R;

public class LoginActivity extends Activity {

	private WebView webView;

	private final Handler tokenReceiverHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				closeBrowserWindow();
			}
		};
	};

	/**
	 * Performs the initial oauth request opening the embedded browser in the
	 * oauth login page.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		try {
			OAuthClientRequest request = OAuthClientRequest
					.authorizationLocation(MaritacaHomeActivity.REQUEST_URL)
					.setClientId(MaritacaHomeActivity.CLIENTID)
					.setRedirectURI(MaritacaHomeActivity.URL_CALLBACK)
					.setResponseType(ResponseType.CODE.toString())
					.buildQueryMessage();

			openBrowserInUrl(request.getLocationUri());
		} catch (OAuthSystemException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Opens the embedded web kit browser window
	 * 
	 * @param url
	 */
	private void openBrowserInUrl(String url) {
		Log.d(this.getClass().getName(), "Acessing URL: " + url);

		setWebView((WebView) findViewById(R.id.web_engine));

		WebViewClient maritacaViewClient = new OAuthWebViewClient(
				tokenReceiverHandler);
		getWebView().setWebViewClient(maritacaViewClient);

		getWebView().getSettings().setJavaScriptEnabled(true);
		getWebView().loadUrl(url);
	}

	/**
	 * Closes the embedded web kit browser window
	 */
	private void closeBrowserWindow() {
		getWebView().setVisibility(View.GONE);
		OAuthTokenManager.loadTokenFile();
		Bundle questionBundle = new Bundle();
		questionBundle.putString("accessToken", OAuthTokenManager.getToken());

		// Chama a proxima activity
		Intent questionIntent = new Intent(this,
				MaritacaActivityController.class);
		questionIntent.putExtras(questionBundle);
		startActivity(questionIntent);
		finish();
	}

	public WebView getWebView() {
		return webView;
	}

	public void setWebView(WebView webView) {
		this.webView = webView;
	}
}