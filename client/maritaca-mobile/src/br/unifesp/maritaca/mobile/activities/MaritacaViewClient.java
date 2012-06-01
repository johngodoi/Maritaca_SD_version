package br.unifesp.maritaca.mobile.activities;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MaritacaViewClient extends WebViewClient {

	private Handler handler;
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.e("TMP LOG", url);
		
		if(url.matches(".*code=.*")){
			return true;
		} else {
			return super.shouldOverrideUrlLoading(view, url);
		}		
	}
}
