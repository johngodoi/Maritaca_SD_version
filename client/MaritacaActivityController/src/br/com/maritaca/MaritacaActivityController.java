package br.com.maritaca;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.maritaca.database.DBManager;
import br.com.maritaca.questions.Model;
import br.com.maritaca.view.Viewer;
import br.org.maritaca.R;

public class MaritacaActivityController extends Activity {
	/** Called when the activity is first created. */
	final static String urlTeste = "10.0.2.2";
	final static String url = "172.20.22.7";
	Viewer viewer;
	Model model;
	String formId;
	String userId;
	private DBManager dbManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO move
		super.onCreate(savedInstanceState);
		dbManager = new DBManager(this);
		inicio();
	}

	private void inicio() {
		setContentView(R.layout.main_layout);
		Button button = (Button) findViewById(R.id.botaoEnviar);
		final EditText editText = (EditText) findViewById(R.id.campoID);
		editText.setText("9b2a6220-a548-11e1-be87-c01885e5c4ed");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String id = editText.getText().toString();
				getForm(id);
			}
		});
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
						dbManager.getWritableDatabase().execSQL(
								"delete from " + DBManager.TABLE_NAME);
						inicio();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(this, "Erro na conexão", Toast.LENGTH_LONG)
							.show();
					try {
						StringBuilder stringBuilder = new StringBuilder();
						Log.v("INSERT", stringBuilder.toString());
						stringBuilder.append("INSERT INTO ")
								.append(DBManager.TABLE_NAME)
								.append(" VALUES ('").append(formId)
								.append("','").append(getXmlAsString())
								.append("')");
						dbManager.getWritableDatabase().execSQL(
								stringBuilder.toString());
					} catch (Exception e1) {

					}
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
		XmlSerializer xmlSerializer = Xml.newSerializer();
		StringWriter stringWriter = new StringWriter();
		xmlSerializer.setOutput(stringWriter);
		xmlSerializer.startDocument("UTF-8", true);
		xmlSerializer.startTag("", "datacollected");

		xmlSerializer.startTag("", "formId");
		xmlSerializer.text(this.formId);
		xmlSerializer.endTag("", "formId");

		xmlSerializer.startTag("", "userId");
		xmlSerializer.text(this.userId);
		xmlSerializer.endTag("", "userId");

		xmlSerializer.startTag("", "answers");
		xmlSerializer.startTag("", "answer");
		xmlSerializer.attribute("", "timestamp", "" + new Date().getTime());
		for (int i = 0; i < this.model.getSize(); i++) {
			xmlSerializer.startTag("", "question");

			xmlSerializer.attribute("", "id", model.getQuestionIndex(i).getId()
					.toString());
			xmlSerializer.startTag("", "value");
			xmlSerializer
					.text(model.getQuestionIndex(i).getValue().toString() == null ? ""
							: model.getQuestionIndex(i).getValue().toString());
			xmlSerializer.endTag("", "value");
			xmlSerializer.endTag("", "question");

		}
		xmlSerializer.endTag("", "answer");
		xmlSerializer.endTag("", "answers");
		xmlSerializer.endTag("", "datacollected");

		xmlSerializer.endDocument();
		return stringWriter.toString();
	}

	private boolean sendAnswer(String formId, String userId, String xmlAsString)
			throws URISyntaxException, IllegalArgumentException,
			IllegalStateException, IOException {
		// List<NameValuePair> params = new LinkedList<NameValuePair>();
		// params.add(new BasicNameValuePair("xml", xmlAsString));
		// params.add(new BasicNameValuePair("formId", formId));
		// params.add(new BasicNameValuePair("oauth_token",
		// "359999a2b143d883fba82867f191fadc"));
		//
		// URI uri = URIUtils.createURI("http", this.urlTeste, 8080,
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

			// String url = "http://" + this.urlTeste +
			// ":8080/maritaca/ws/form/"
			// + id;
			// HttpClient httpClient = new DefaultHttpClient();
			// HttpGet request = new HttpGet(url);
			// ResponseHandler<String> handler = new BasicResponseHandler();
			//
			// String result = httpClient.execute(request, handler);
			//
			// httpClient.getConnectionManager().shutdown();
			// Log.v("REQUEST", result);
			//
			// is = new ByteArrayInputStream(result.getBytes("UTF-8"));
			// if (is != null)
			// Toast.makeText(this, "RECEBI ALGO: " + is.toString(),
			// Toast.LENGTH_LONG).show();

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
}