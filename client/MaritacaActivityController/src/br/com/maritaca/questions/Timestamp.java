package br.com.maritaca.questions;

import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.maritaca.MaritacaActivityController;
import br.com.maritaca.xml.parser.XMLParser;

public class Timestamp extends Question {

	private boolean show;

	public Timestamp(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);
		this.show = XMLParser.myParseBoolean(element.getAttribute("show"));

	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public void setValue(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void save(View answer) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getLayout(final MaritacaActivityController controller) {
		final Calendar calendar = Calendar.getInstance();
		if (show == false) {
			EditText campoDeResposta = new EditText(controller);
			value = "" + calendar.get(Calendar.HOUR_OF_DAY)
					+ calendar.get(Calendar.MINUTE)
					+ calendar.get(Calendar.SECOND);
			campoDeResposta.setText(value.toString());
			campoDeResposta.setKeyListener(null);
			return campoDeResposta;
		} else {
			Button button = new Button(controller);
			button.setText("Obter data");
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					calendar.setTime(new Date());
					value = "" + calendar.get(Calendar.HOUR_OF_DAY)
							+ calendar.get(Calendar.MINUTE)
							+ calendar.get(Calendar.SECOND);
					Toast.makeText(controller,
							"Timestamp salvo: " + value.toString(),
							Toast.LENGTH_LONG).show();
				}
			});
			return button;
		}
	}
}
