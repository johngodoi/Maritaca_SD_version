package br.com.maritaca.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.maritaca.questions.Model;
import br.com.maritaca.utils.LocationHolder;
import br.com.maritaca.utils.XMLUtils;
import br.com.maritaca.view.Viewer;
import br.org.maritaca.R;

public class MaritacaActivityController extends Activity implements
		LocationListener {

	final static String maritacaDir = "maritaca";
	public static final File diretorio = new File(
			Environment.getExternalStorageDirectory(), maritacaDir);
	private String accessToken = "";
	private final LocationHolder locationHolder = LocationHolder
			.getInstanceOfLocationHolder();

	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	private Viewer viewer;
	private Model model;
	private String formId;
	private String userId;
	private LocationManager locationManager;
	private LocationListener locationListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO move
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "Chamou on create", Toast.LENGTH_LONG).show();
		accessToken = (String) this.getIntent().getExtras().get("accessToken");
		diretorio.mkdir();
		Log.d("ACCESSTOKEN", accessToken);
		turnOnLocation();
		inicio();

	}

	private void turnOnLocation() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			public void onLocationChanged(android.location.Location location) {
				if (location != null) {
					locationHolder.setLat(location.getLatitude());
					locationHolder.setLog(location.getLongitude());
					Log.d("LOCATION", "" + location.getLatitude());
				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
	}

	/**
	 * Seta dados do layout e baixa o formulario
	 */
	private void inicio() {
		setContentView(R.layout.main_layout);
		Button button = (Button) findViewById(R.id.botaoEnviar);
		final EditText editText = (EditText) findViewById(R.id.campoID);
		// apenas para teste
		editText.setText("e9955ef0-af3a-11e1-b4c3-c01885e5c4ed");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String id = editText.getText().toString();
				getForm(id);
			}
		});
	}

	protected void onDestroy() {
		locationManager.removeUpdates(locationListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.d("REQUEST", "" + requestCode);
				Log.d("RESULT", String.valueOf(resultCode));
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Imagem armazenada!", Toast.LENGTH_LONG)
						.show();
				Log.d("NULL", "" + (model == null) + " " + (this == null) + " ");
				// this.setContentView(MaritacaActivityController.lastView);
			} else if (resultCode == RESULT_CANCELED) {

			} else {

			}
		}
	}

	public void next() {
		if (model.hasNext()) {
			model.next();
			// Create new layout
			viewer = new Viewer(this, model.getCurrent(),
					model.isCurrentLastQuestion(), model.isCurrentFirst());
			setContentView(viewer.getView());
		} else {
			if (model.isCurrentLastQuestion()) {
				try {
					if (sendAnswer(formId, userId, getXmlAsString())) {
						Toast.makeText(this, "Formulario enviado com sucesso",
								Toast.LENGTH_LONG).show();
						inicio();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(this,
						"Foi digitado algum valor inválido, tente novamente",
						Toast.LENGTH_LONG).show();
				inicio();
			}

		}
	}

	public void previous() {
		if (model.hasPrevious()) {
			model.previous();

			// Create new layout
			viewer = new Viewer(this, model.getCurrent(),
					model.isCurrentLastQuestion(), model.isCurrentFirst());
			setContentView(viewer.getView());
		} else {
			Toast.makeText(this,
					"TODO: disable this button. Nothing to do before here!!!",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void info() {
		Toast.makeText(this, model.getCurrent().getHelp(), Toast.LENGTH_SHORT)
				.show();
	}

	private String getXmlAsString() throws IllegalArgumentException,
			IllegalStateException, IOException {
		return XMLUtils.getXmlAnswerAsString(formId, userId, model);
	}

	private boolean sendAnswer(String formId, String userId, String xmlAsString)
			throws URISyntaxException, IllegalArgumentException,
			IllegalStateException, IOException {
		Log.d(getClass().getCanonicalName(), "Enviando resposta");
		Toast.makeText(this, "Enviando resposta", Toast.LENGTH_SHORT).show();
		String uri = "http://" + MaritacaHomeActivity.IP
				+ ":8080/maritaca/ws/answer/add" + "?oauth_token="
				+ accessToken;
		String contentType = "application/xml";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPut putRequest = new HttpPut(uri);
		StringEntity input = new StringEntity(XMLUtils.getXmlAnswerAsString(
				formId, userId, model));
		input.setContentType(contentType);
		putRequest.setEntity(input);
		HttpResponse response = httpClient.execute(putRequest);
		// List<NameValuePair> params = new LinkedList<NameValuePair>();
		// params.add(new BasicNameValuePair("xml", xmlAsString));
		// params.add(new BasicNameValuePair("oauth_token", this.accessToken));
		//
		// URI uri = URIUtils.createURI("http", MaritacaHomeActivity.IP, 8080,
		// "/maritaca/ws/answer/",
		// URLEncodedUtils.format(params, "UTF-8"), null);
		//
		// HttpPost request = new HttpPost(uri);
		//
		// request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		//
		// HttpClient client = new DefaultHttpClient();
		//
		// HttpResponse httpResponse = client.execute(request);
		//
		// BufferedReader in = new BufferedReader(new InputStreamReader(
		// httpResponse.getEntity().getContent()));
		// StringBuffer sb = new StringBuffer("");
		// String line = "";
		// while ((line = in.readLine()) != null) {
		// sb.append(line).append("\n");
		// }
		// in.close();
		// if (!sb.toString().contains("error")) {
		// return true;
		// }
		// return false;
		return true;
	}

	private void getForm(String id) {
		try {
			// Log.v("FORMID", id);
			InputStream is;

			String urlAsString = "http://" + MaritacaHomeActivity.IP
					+ ":8080/maritaca/ws/form/" + id + "?oauth_token="
					+ accessToken;

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlAsString);
			HttpResponse response = httpClient.execute(request);
			String xmlString = EntityUtils.toString(response.getEntity());

			is = new ByteArrayInputStream(this.parseResponse(xmlString)
					.getBytes("UTF-8"));
			Log.d("FORMULARIO", this.parseResponse(xmlString));
			// is = connection.getInputStream();
			if (is != null)
				Toast.makeText(this, "RECEBI ALGO: " + is.toString(),
						Toast.LENGTH_LONG).show();

			is = getResources().getAssets().open("parseado.xml");
			model = new Model(is);
			if (model.getSize() > 0) {
				// Create layout
				viewer = new Viewer(this, model.getCurrent(),
						model.isCurrentLastQuestion(), model.isCurrentFirst());
				// Show it
				setContentView(viewer.getView());

				formId = model.getFormId();
				userId = model.getUserId();
			}
		} catch (Exception e) {
			// Log.v("ERROR", e.toString());
			e.printStackTrace();
			Toast.makeText(this, "Erro na comunicacao com o servidor",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Extrai o XML(o formulario) da resposta
	 * 
	 * @param xmlString
	 * @return
	 */
	private String parseResponse(String xmlString) {
		return XMLUtils.parseXMLResponse(xmlString);
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}