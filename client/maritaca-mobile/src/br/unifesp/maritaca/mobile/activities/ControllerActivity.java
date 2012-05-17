package br.unifesp.maritaca.mobile.activities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import br.unifesp.maritaca.mobile.model.Model;
import br.unifesp.maritaca.mobile.view.Viewer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ControllerActivity extends Activity {

	private String formId = "02753970-9f5c-11e1-b730-4666cfaa37dc";
	
	private Viewer viewer;
	private Model model;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			InputStream is;
			String url = "http://172.20.22.7:8080/maritaca/ws/form/" + formId; // + "/?" + accessToken;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			ResponseHandler<String> handler = new BasicResponseHandler();
			
			String result = httpClient.execute(request, handler);
			
			httpClient.getConnectionManager().shutdown();
	        Log.i("REQUEST", result);
	        
	        is = new ByteArrayInputStream(result.getBytes("UTF-8"));
			
			//is = getResources().getAssets().open("sample.xml");
			model = new Model(is);

			if (!model.isQuestionsEmpty()) {
				viewer = new Viewer(this, model.getCurrentQuestion());
				//Show it
				setContentView(viewer.getView());
			}
		} catch (Exception e) {
			Log.e("ERROR", e.getMessage());
		}
		
	}

	@Override
	protected void onPause() {
		// TODO in this method its the last method called in your activity,
		// so on this method should save any data that we want to keep around
		// for the next time.
		super.onPause();
	}

	public void next() {
		//TODO validate!
		if (!model.getCurrentQuestion().validate()){
			Toast.makeText(this, "TODO: Incorrect input!", Toast.LENGTH_SHORT).show();
		}
		else if (model.next()){

			//Create new layout
			Log.v("ARLINDO", "MaritacaActivity::next, view screen " + model.getCurrentQuestion().getId());
			viewer = new Viewer(this, model.getCurrentQuestion());			
			setContentView(viewer.getView());
		}
		else{
			model.save();
			Toast.makeText(this, "TODO: save data!!!", Toast.LENGTH_SHORT).show();
		}
	}

	public void previous() {
		if (model.validate() && model.hasPrevious()){
			model.previous();
			//Create new layout
			viewer = new Viewer(this, model.getCurrentQuestion());
			setContentView(viewer.getView());
		}
		else{
			Toast.makeText(this, "TODO: disable this button. Nothing to do before here!!!", Toast.LENGTH_SHORT).show();
		}

	}

	public void help() {
		Toast.makeText(this, model.getCurrentQuestion().getHelp(), Toast.LENGTH_SHORT).show();
	}
	
}
