package br.com.maritaca;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.maritaca.questions.Model;
import br.com.maritaca.questions.Number;
import br.com.maritaca.view.Viewer;
import br.org.maritaca.R;

public class MaritacaActivityController extends Activity {
	/** Called when the activity is first created. */
	final static String url="172.20.26.192";
	Viewer viewer;
	Model model;
	String formId;
	String userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO move
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		Button button = (Button) findViewById(R.id.botaoEnviar);
		final EditText editText = (EditText) findViewById(R.id.campoID);
		final MaritacaActivityController maritacaActivityController = this;
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String id = editText.getText().toString();
				getForm(id);

				InputStream is;
				try {
					// reference for the XML file
					is = getResources().getAssets().open("parseado.xml");

					// parse XML and populate model
					model = new Model(is);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (model.getSize() > 0) {
					// Create layout
					viewer = new Viewer(maritacaActivityController, model.getCurrent(), model.isCurrentLastQuestion(),
							model.isCurrentFirst());
					// Show it
					setContentView(viewer.getView());

					formId = model.getFormId();
					userId = model.getUserId();
				}
			}
		});

//		try {
//			HttpClient client = new DefaultHttpClient();
//			HttpGet request = new HttpGet();
//			request.setURI(new URI("http://192.168.1.105:8080/webServiceSD/id"));
//			HttpResponse response = client.execute(request);
//			
//
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		InputStream is;
//		try {
//			// reference for the XML file
//			is = getResources().getAssets().open("parseado.xml");
//
//			// parse XML and populate model
//			model = new Model(is);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		if (model.getSize() > 0) {
//			// Create layout
//			viewer = new Viewer(this, model.getCurrent(), model.hasNext(),
//					model.hasPrevious());
//			// Show it
//			setContentView(viewer.getView());
//
//			formId = model.getFormId();
//			userId = model.getUserId();
//		}
	}

	public void next() {
		if (model.hasNext()) {
			model.next();
			// Create new layout
			viewer = new Viewer(this, model.getCurrent(), model.isCurrentLastQuestion(),
					model.isCurrentFirst());

			setContentView(viewer.getView());
		} else {

//			try {
//				sendAnswer(formId, userId, getXmlAsString());
//				Toast.makeText(this, "Formulario enviado com sucesso", Toast.LENGTH_LONG).show();
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			

		}
	}

	public void previous() {
		if (model.hasPrevious()) {
			model.previous();

			// Create new layout
			viewer = new Viewer(this, model.getCurrent(), model.isCurrentLastQuestion(),
					model.isCurrentFirst());

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
		for (int i = 0; i < this.model.getSize(); i++) {
			// TODO usar enum para tipos possiveis de pergunta
			String pergunta = model.getQuestionIndex(i) instanceof Number ? "number"
					: "text";
			// TODO rever os campos
			// cria as tags de cada pergunta
			xmlSerializer.startTag("", pergunta);
			xmlSerializer.attribute("", "id", model.getQuestionIndex(i).getId()
					.toString());
			xmlSerializer.attribute("", "resposta", model.getQuestionIndex(i)
					.getValue()==null ? "" : model.getQuestionIndex(i)
							.getValue().toString());
			xmlSerializer.endTag("", pergunta);
		}

		xmlSerializer.endDocument();
		return stringWriter.toString();
	}

	private void sendAnswer(String formId, String userId, String xmlAsString) throws URISyntaxException, IllegalArgumentException,
			IllegalStateException, IOException {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("formId", formId));
		params.add(new BasicNameValuePair("userId", userId));
		params.add(new BasicNameValuePair("xml", xmlAsString));
		
		URI uri = URIUtils.createURI("http", this.url, 8080,"/webServiceSD/id", URLEncodedUtils.format(params, "UTF-8"), null);
		
		HttpPost request = new HttpPost(uri);
		
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        HttpClient client = new DefaultHttpClient();
        
        HttpResponse httpResponse = client.execute(request);
	}
	
	private void getForm(String id){
//		try {
//
//			HttpClient client = new DefaultHttpClient();
//			HttpGet request = new HttpGet();
//			request.setURI(new URI("http://"+url+":8080/webServiceSD/id?id="+id));
//			HttpResponse response = client.execute(request);
//			
//
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}