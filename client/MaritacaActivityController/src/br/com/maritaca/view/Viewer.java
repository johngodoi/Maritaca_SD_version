package br.com.maritaca.view;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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

	public Viewer(MaritacaActivityController maritacaActivity, Question current, Boolean isFinal, Boolean isFirst) {
		activity = maritacaActivity;
		this.current = current;

		baseView = new LinearLayout(activity);
		baseView.setOrientation(LinearLayout.VERTICAL);

		getTopLayout(isFinal, isFirst);
		baseView.addView(this.top);

		getMiddleLayout(current);
		baseView.addView(this.middle);

		getBottonLayout();
		baseView.addView(this.botton);	
		
		Log.v("ARLINDO", "Viewer Constru�do: ");

	}	

	private void getTopLayout(Boolean isFinal, Boolean isFirst) {
		top = new LinearLayout(activity);
		top.setOrientation(LinearLayout.HORIZONTAL);
		
		Button info = new Button(activity);
		Button next = new Button(activity);
	
		info.setText("Info");
		
		if(!isFirst){
			Button prev = new Button(activity);
			prev.setText("<---");
			prev.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					current.save(answer);
					if(current.validate()){
						activity.previous();
					}
					else{
						Toast.makeText(activity, "Valor incorreto!", Toast.LENGTH_SHORT).show();
					}
				}
			});
			top.addView(prev);
		}
		if(!isFinal){
			next.setText("--->");
		}
		else{
			next.setText("Salvar");
		}
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				current.save(answer);		
				if(current.validate()){
					activity.next();
				}
				else{
					Toast.makeText(activity, "Valor incorreto!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		


		info.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activity.info();
			}
		});

		
		top.addView(info);
		top.addView(next);
	}

	//TODO criar e colocar enum na question
	private void getMiddleLayout(Question current) {
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