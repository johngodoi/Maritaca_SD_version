package br.unifesp.maritaca.mobile.activities;

import br.unifesp.maritaca.mobile.activities.R;
import br.unifesp.maritaca.mobile.util.OAuthTokenManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends Activity{

	private static Context context;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {         	
    	super.onCreate(savedInstanceState);
    	
		HomeActivity.context = getApplicationContext();
    	
        setContentView(R.layout.main);
        
        if(OAuthTokenManager.tokenExists()){        	
        	OAuthTokenManager.loadTokenFile();
        	Intent intent = new Intent(this, MenuActivity.class);
        	startActivity(intent);        	
        } else {
        	Intent intent = new Intent(this, LoginActivity.class);
        	startActivity(intent);
        }        
    }

	public static Context getContext() {
		return context;
	}
}