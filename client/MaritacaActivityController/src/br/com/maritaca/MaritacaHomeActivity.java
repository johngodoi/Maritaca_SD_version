package br.com.maritaca;

import net.smartam.leeloo.client.OAuthClient;
import net.smartam.leeloo.client.URLConnectionClient;
import net.smartam.leeloo.client.request.OAuthClientRequest;
import net.smartam.leeloo.client.response.OAuthJSONAccessTokenResponse;
import net.smartam.leeloo.common.exception.OAuthSystemException;
import net.smartam.leeloo.common.message.types.GrantType;
import net.smartam.leeloo.common.message.types.ResponseType;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import br.org.maritaca.R;

public class MaritacaHomeActivity extends Activity {

	public static final String IP = "10.0.2.2";

	private static final String SECRET = "maritacasecret";
	private static final String CLIENTID = "maritacamobile";
	private static final String REQUEST_URL = "http://" + IP
			+ ":8080/maritaca/oauth/authorizationRequest";
	private static final String ACCESS_URL = "http://" + IP
			+ ":8080/maritaca/oauth/accessTokenRequest";
	private static final String AUTHORIZE_URL = "http://" + IP
			+ ":8080/maritaca/oauth/authorizationConfirm";
	// "maritaca:urlCall//";
	private static final String URL_CALLBACK = "maritaca://urlCall";

	/**
	 * Cria um request para a chave Oauth
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			OAuthClientRequest request = OAuthClientRequest
					.authorizationLocation(REQUEST_URL).setClientId(CLIENTID)
					.setRedirectURI(URL_CALLBACK)
					.setResponseType(ResponseType.CODE.toString())
					.buildQueryMessage();
			Intent intentURL = new Intent(Intent.ACTION_VIEW, Uri.parse(request
					.getLocationUri()));
			intentURL.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_NO_HISTORY
					| Intent.FLAG_FROM_BACKGROUND);
			startActivity(intentURL);

		} catch (OAuthSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public void onResume() {
	// super.onResume();
	// Log.d("Teste", "Chamou onResume");
	// this.onNewIntent1(getIntent());
	// }

	/**
	 * Chamado apos o callback ao endereço maritaca://urlCallBack (definido no
	 * AndroidManifest), extrai o codigo da chave e faz um request pela secret.
	 */
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("Teste", "Chamou new Intent");
		Uri uri = intent.getData();
		if (uri != null && uri.toString().contains("code")) {
			Log.d("TESTE", uri.toString());
			String code = uri.getQueryParameter("code");
			try {

				OAuthClientRequest request = OAuthClientRequest
						.tokenLocation(ACCESS_URL)
						.setGrantType(GrantType.AUTHORIZATION_CODE)
						.setClientId(CLIENTID).setClientSecret(SECRET)
						.setRedirectURI(URL_CALLBACK).setCode(code)
						.setParameter("response_type", "token")
						.buildBodyMessage();

				OAuthClient oAuthClient = new OAuthClient(
						new URLConnectionClient());

				OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
						.accessToken(request,
								OAuthJSONAccessTokenResponse.class);
				// apenas para teste
				Toast.makeText(
						getApplicationContext(),
						"Autenticação realizada!\nToken= "
								+ oAuthResponse.getAccessToken(),
						Toast.LENGTH_SHORT).show();

				Bundle questionBundle = new Bundle();
				questionBundle.putString("accessToken",
						oAuthResponse.getAccessToken());

				// Chama a proxima activity
				Intent questionIntent = new Intent(this,
						MaritacaActivityController.class);
				questionIntent.putExtras(questionBundle);
				startActivity(questionIntent);
				finish();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
