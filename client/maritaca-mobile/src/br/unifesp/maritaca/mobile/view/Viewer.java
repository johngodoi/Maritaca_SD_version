package br.unifesp.maritaca.mobile.view;

import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.activities.R;
import br.unifesp.maritaca.mobile.model.Question;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Viewer {

	private ControllerActivity activity;
	private LinearLayout baseView;

	LinearLayout topLayout;
	LinearLayout middleLayout;
	LinearLayout bottonLayout;
	
	private View answer;
	private Question current;

	public Viewer(ControllerActivity maritacaActivity, Question current) {
		activity = maritacaActivity;
		this.current = current;

		baseView = new LinearLayout(activity);
		baseView.setOrientation(LinearLayout.VERTICAL);

		createTopLayout();
		baseView.addView(this.topLayout);

		createMiddleLayout();
		baseView.addView(this.middleLayout);

		createBottonLayout();
		baseView.addView(this.bottonLayout);		
	}	

	/**
	 * This method creates the top layout
	 */
	private void createTopLayout() {
		topLayout = new LinearLayout(activity);
		topLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		Button previous = new Button(activity);
		Button help = new Button(activity);
		Button next = new Button(activity);
		
		previous.setText(R.string.label_previous);
		help.setText(R.string.label_help);
		next.setText(R.string.label_next);

		previous.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				current.save(answer);
				activity.previous();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				current.save(answer);
				activity.next();
			}
		});;


		help.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activity.help();
			}
		});

		topLayout.addView(previous);
		topLayout.addView(help);
		topLayout.addView(next);
	}


	private void createMiddleLayout() {
		middleLayout = new LinearLayout(activity);
		middleLayout.setOrientation(LinearLayout.VERTICAL);
		
		switch (current.getComponentType()) {
			case TEXT:				
			case NUMBER:
				TextView tvQuestion = new TextView(activity);			
				tvQuestion.setText(current.getLabel());
				middleLayout.addView(tvQuestion);
				break;
			case COMBOBOX:
				break;
			case CHECKBOX:
				break;
			case RADIOBOX:
				break;
			case DATE:
				break;
			default:
				break;
		}
	}

	private void createBottonLayout(){
		bottonLayout = new LinearLayout(activity);
		answer = current.getLayout(activity);
		bottonLayout.addView(answer);
	}


	public LinearLayout getView(){
		return baseView;
	}
}