package br.com.maritaca;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import br.com.maritaca.questions.Model;
import br.com.maritaca.view.Viewer;

public class MaritacaActivityController extends Activity {
	/** Called when the activity is first created. */

	Viewer viewer;
	Model model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
				
		InputStream is;
		try {
			//reference for the XML file
			is = getResources().getAssets().open("parseado.xml");

			//parse XML and populate model
			model = new Model(is);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if (model.getSize() > 0){
			//Create layout
			viewer = new Viewer(this, model.getCurrent());
			//Show it
			setContentView(viewer.getView());
		}
	}

	public void next() {
		if (model.validate()&&model.hasNext()){
			model.next();

			//Create new layout
			viewer = new Viewer(this, model.getCurrent());
			
			setContentView(viewer.getView());
		}
		else{
			Toast.makeText(this, "TODO: save data!!!", Toast.LENGTH_SHORT).show();
		}
	}

	public void previous() {
		if (model.validate()&&model.hasPrevious()){
			model.previous();

			//Create new layout
			viewer = new Viewer(this, model.getCurrent());
			
			setContentView(viewer.getView());
		}
		else{
			Toast.makeText(this, "TODO: disable this button. Nothing to do before here!!!", Toast.LENGTH_SHORT).show();
		}

	}

	public void info() {
		Toast.makeText(this, model.getCurrent().getHelp(), Toast.LENGTH_SHORT).show();
	}
}