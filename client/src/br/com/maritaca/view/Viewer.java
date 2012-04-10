package br.com.maritaca.view;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.maritaca.MaritacaActivityController;
import br.com.maritaca.questions.Question;

public class Viewer {

	private MaritacaActivityController activity;
	private LinearLayout baseView;

	LinearLayout top;
	LinearLayout middle;
	LinearLayout botton;
	
	private View answer;
	private Question current;

	public Viewer(MaritacaActivityController maritacaActivity, Question current) {
		activity = maritacaActivity;
		this.current = current;

		baseView = new LinearLayout(activity);
		baseView.setOrientation(LinearLayout.VERTICAL);

		getTopLayout();
		baseView.addView(this.top);

		getMiddleLayout();
		baseView.addView(this.middle);

		getBottonLayout();
		baseView.addView(this.botton);	
		
		Log.v("ARLINDO", "Viewer Constru’do: ");

	}	

	private void getTopLayout() {
		top = new LinearLayout(activity);
		top.setOrientation(LinearLayout.HORIZONTAL);
		Button prev = new Button(activity);
		Button info = new Button(activity);
		Button next = new Button(activity);
		prev.setText("<---");
		info.setText("Info");
		next.setText("--->");

		prev.setOnClickListener(new View.OnClickListener() {
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


		info.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activity.info();
			}
		});

		top.addView(prev);
		top.addView(info);
		top.addView(next);
	}


	private void getMiddleLayout() {
		middle = new LinearLayout(activity);
		middle.setOrientation(LinearLayout.VERTICAL);
		TextView tvPergunta = new TextView(activity);
		tvPergunta.setText(current.getLabel());
		middle.addView(tvPergunta);
	}


	//private void generateMidLayout(LinearLayout mid,MaritacaActivity controller,Question question){}

	private void getBottonLayout(){
		botton = new LinearLayout(activity);
		answer = current.getLayout(activity);
		botton.addView(answer);
	}


	public LinearLayout getView(){
		return baseView;
	}
}