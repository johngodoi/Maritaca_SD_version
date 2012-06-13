package br.com.maritaca.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import br.com.maritaca.oauth.OAuthTokenManager;
import br.org.maritaca.R;

public class MaritacaHomeActivity extends Activity {

	public static final String IP = "192.168.1.101";// "10.0.2.2";

	public static final String SECRET = "maritacasecret";
	public static final String CLIENTID = "maritacamobile";
	public static final String REQUEST_URL = "http://" + IP
			+ ":8080/maritaca/oauth/authorizationRequest";
	public static final String ACCESS_URL = "http://" + IP
			+ ":8080/maritaca/oauth/accessTokenRequest";
	public static final String AUTHORIZE_URL = "http://" + IP
			+ ":8080/maritaca/oauth/authorizationConfirm";
	// "maritaca:urlCall//";
	public static final String URL_CALLBACK = "http://localhost:8082/";// "maritaca://urlCall";

	public static Context context = null;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(getClass().getCanonicalName(), "Start");
		super.onCreate(savedInstanceState);

		MaritacaHomeActivity.context = this.getApplicationContext();

		setContentView(R.layout.main);

		if (OAuthTokenManager.tokenExists()) {
			OAuthTokenManager.loadTokenFile();
			Bundle questionBundle = new Bundle();
			questionBundle.putString("accessToken",
					OAuthTokenManager.getToken());

			// Chama a proxima activity
			Intent questionIntent = new Intent(this,
					MaritacaActivityController.class);
			questionIntent.putExtras(questionBundle);
			startActivity(questionIntent);
			finish();
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}

	// /**
	// * Cria um request para a chave Oauth
	// */
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// MaritacaHomeActivity.context = this.getApplicationContext();
	// setContentView(R.layout.main);
	//
	// try {
	// OAuthClientRequest request = OAuthClientRequest
	// .authorizationLocation(REQUEST_URL).setClientId(CLIENTID)
	// .setRedirectURI(URL_CALLBACK)
	// .setResponseType(ResponseType.CODE.toString())
	// .buildQueryMessage();
	// Intent intentURL = new Intent(Intent.ACTION_VIEW, Uri.parse(request
	// .getLocationUri()));
	// intentURL.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
	// | Intent.FLAG_ACTIVITY_NO_HISTORY
	// | Intent.FLAG_FROM_BACKGROUND);
	// startActivity(intentURL);
	//
	// } catch (OAuthSystemException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// // public void onResume() {
	// // super.onResume();
	// // Log.d("Teste", "Chamou onResume");
	// // this.onNewIntent1(getIntent());
	// // }
	//
	// /**
	// * Chamado apos o callback ao endereço maritaca://urlCallBack (definido no
	// * AndroidManifest), extrai o codigo da chave e faz um request pela
	// secret.
	// */
	// protected void onNewIntent(Intent intent) {
	// super.onNewIntent(intent);
	// Log.d("Teste", "Chamou new Intent");
	// Uri uri = intent.getData();
	// if (uri != null && uri.toString().contains("code")) {
	// Log.d("TESTE", uri.toString());
	// String code = uri.getQueryParameter("code");
	// try {
	//
	// OAuthClientRequest request = OAuthClientRequest
	// .tokenLocation(ACCESS_URL)
	// .setGrantType(GrantType.AUTHORIZATION_CODE)
	// .setClientId(CLIENTID).setClientSecret(SECRET)
	// .setRedirectURI(URL_CALLBACK).setCode(code)
	// .setParameter("response_type", "token")
	// .buildBodyMessage();
	//
	// OAuthClient oAuthClient = new OAuthClient(
	// new URLConnectionClient());
	//
	// OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
	// .accessToken(request,
	// OAuthJSONAccessTokenResponse.class);
	// // apenas para teste
	// Toast.makeText(
	// getApplicationContext(),
	// "Autenticação realizada!\nToken= "
	// + oAuthResponse.getAccessToken(),
	// Toast.LENGTH_SHORT).show();
	//
	// Bundle questionBundle = new Bundle();
	// questionBundle.putString("accessToken",
	// oAuthResponse.getAccessToken());
	//
	// // Chama a proxima activity
	// Intent questionIntent = new Intent(this,
	// MaritacaActivityController.class);
	// questionIntent.putExtras(questionBundle);
	// startActivity(questionIntent);
	// finish();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
}