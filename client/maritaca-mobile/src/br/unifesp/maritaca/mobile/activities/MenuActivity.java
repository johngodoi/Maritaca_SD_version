package br.unifesp.maritaca.mobile.activities;

import br.unifesp.maritaca.mobile.util.OAuthTokenManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MenuActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.menu);
		
		TextView userText = (TextView) findViewById(R.id.logged_user);		
		userText.setText(OAuthTokenManager.getUser());

		
		TextView expiresText = (TextView) findViewById(R.id.expiration_time);		
		expiresText.setText(String.valueOf(OAuthTokenManager.getExpiresIn()));
		
		View collectButton = findViewById(R.id.form_collect);
        collectButton.setOnClickListener(this);
    
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.form_collect:
			Intent intent = new Intent(this, ControllerActivity.class);
			startActivity(intent);
			break;
		}
	}
}
