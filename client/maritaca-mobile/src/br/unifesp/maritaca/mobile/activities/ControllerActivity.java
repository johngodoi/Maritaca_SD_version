package br.unifesp.maritaca.mobile.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import br.unifesp.maritaca.mobile.model.Model;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.XMLAnswerParser;
import br.unifesp.maritaca.mobile.view.Viewer;

public class ControllerActivity extends Activity {

	private String formId = "02753970-9f5c-11e1-b730-4666cfaa37dc";
	private String userId = "e43dc800-9f5b-11e1-b730-4666cfaa37dc";
	
	private Viewer viewer;
	private Model model;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			InputStream is = getResources().getAssets().open("sample.xml");
			model = new Model(is);

			if (!model.isQuestionsEmpty()) {
				viewer = new Viewer(this, model.getCurrentQuestion());
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
		if (!model.getCurrentQuestion().validate()) {
			Toast.makeText(this, "TODO: Incorrect input!", Toast.LENGTH_SHORT).show();
		}
		else if (model.next()) {

			//Create new layout
			Log.v("ARLINDO", "MaritacaActivity::next, view screen " + model.getCurrentQuestion().getId());
			viewer = new Viewer(this, model.getCurrentQuestion());			
			setContentView(viewer.getView());
		}
		else {
			showDialog(Constants.SAVE_DIALOG);
		}
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
    	switch(id)
    	{
    		case Constants.SAVE_DIALOG:
    			dialog = showSaveDialog();
    			break;
    		default:
    			dialog = null;
    			break;
    	}    
    	return dialog;
	}
	
	private Dialog showSaveDialog () {
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle(R.string.label_confirmation);
		saveDialog.setMessage(R.string.msg_confirmation);
		saveDialog.setPositiveButton(R.string.button_save, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Question[] data = model.save();
				saveFile(formId, userId, data);
				dialog.cancel();
			}
		});
		saveDialog.setNegativeButton(R.string.button_cancel, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return saveDialog.create();		
	}
	
	private void saveFile(String formId, String userId, Question[] data) {
		String content = "";
		try {
			File answersFile = new File(this.getFileStreamPath(Constants.ANSWERS_FILENAME).toString());
			if(answersFile.exists()) {
				BufferedReader fin = new BufferedReader(new InputStreamReader(this.openFileInput(Constants.ANSWERS_FILENAME)));
				
				content = XMLAnswerParser.updateContentFile(fin, formId, userId, data);
				if(content != null && !"".equals(content)) {
					OutputStreamWriter fout = new OutputStreamWriter(this.openFileOutput(Constants.ANSWERS_FILENAME, Context.MODE_PRIVATE), "UTF-8");
					fout.write(content);				
					fout.close();
				}
				fin.close();
			}
			else {
				content = XMLAnswerParser.buildContentFile(formId, userId, data);
				if(content != null && !"".equals(content)) {
					OutputStreamWriter fout = new OutputStreamWriter(this.openFileOutput(Constants.ANSWERS_FILENAME, Context.MODE_PRIVATE), "UTF-8");
					fout.write(content);				
					fout.close();
				}
			}
			//
			sendCollectedData(content);
			//
		}
		catch (Exception ex) {
			//ex
		}
	}
	
	private void sendCollectedData(String collectedData) {
		try {
			String uri = "http://172.20.22.7:8080/maritaca/ws/answer/add/";
			String contentType = "application/xml";
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut putRequest = new HttpPut(uri);
			StringEntity input = new StringEntity(collectedData);
			input.setContentType(contentType);
			putRequest.setEntity(input);
			HttpResponse response = httpClient.execute(putRequest);
			
		} catch (Exception e) {
			//ex
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