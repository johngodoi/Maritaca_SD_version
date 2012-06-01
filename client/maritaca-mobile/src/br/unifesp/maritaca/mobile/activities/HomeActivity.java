package br.unifesp.maritaca.mobile.activities;

import br.unifesp.maritaca.mobile.activities.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class HomeActivity extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // set up click listeners
        View continueButton = findViewById(R.id.button_login);
        continueButton.setOnClickListener(this); 
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_login:
			Log.d("Maritaca", "logging");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			break;
		}
	}

}