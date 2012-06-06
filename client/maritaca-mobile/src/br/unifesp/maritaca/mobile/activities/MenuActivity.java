package br.unifesp.maritaca.mobile.activities;

import br.unifesp.maritaca.mobile.util.OAuthTokenManager;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MenuActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.menu);
		
		TextView userText = (TextView) findViewById(R.id.logged_user);		
		userText.setText(OAuthTokenManager.getUser());

		
		TextView expiresText = (TextView) findViewById(R.id.expiration_time);		
		expiresText.setText(String.valueOf(OAuthTokenManager.getExpiresIn()));
	}
}
