package br.unifesp.maritaca.mobile.activities;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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

import static br.unifesp.maritaca.mobile.util.Constants.*;

public class LoginActivity extends Activity {
	
	private WebView webView;
	
	private final Handler tokenReceiverHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what==0){
				closeBrowserWindow();
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			OAuthClientRequest request = OAuthClientRequest
					.authorizationLocation(SERVER_URL + AUTHORIZATION_REQUEST)
					.setClientId(MARITACA_MOBILE).setRedirectURI(CLIENT_URL)
					.setResponseType(ResponseType.CODE.toString())
					.buildQueryMessage();

			startPortListener();			
			openBrowserInUrl(request.getLocationUri());			
		} catch (OAuthSystemException e) {
			throw new RuntimeException(e);
		}
	}

	private void openBrowserInUrl(String url) {
		Log.d(this.getClass().getName(), "Acessing URL: "+url);
		setContentView(R.layout.main);
						
		setWebView((WebView) findViewById(R.id.web_engine));
		
		WebViewClient maritacaViewClient = new MaritacaViewClient(); 		
		getWebView().setWebViewClient(maritacaViewClient);
		
		getWebView().getSettings().setJavaScriptEnabled(true);
		getWebView().loadUrl(url);
	}
	
	public void closeBrowserWindow(){
		getWebView().setVisibility(View.GONE);
	}

	protected void startPortListener() {
		Thread fst = new Thread(new OAuthPortListener(tokenReceiverHandler));
		fst.start();
	}

	/** 
	 * Returns the ip address of your phone's network.
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}

	public WebView getWebView() {
		return webView;
	}

	public void setWebView(WebView webView) {
		this.webView = webView;
	}
}