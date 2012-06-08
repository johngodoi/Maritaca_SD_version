package br.com.maritaca.questions;

import org.w3c.dom.Element;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import br.com.maritaca.MaritacaActivityController;
import br.com.maritaca.xml.parser.XMLParser;

public class Text extends Question {

	private Integer max;

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Text(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);
		Integer max = XMLParser.myParseInteger(element.getAttribute("max"));
		// FIXME o formulario ainda nao possui campo default
		// String myDefault = XMLParser.getTagValue("default", element);
		/* Elementos da classe Text */
		this.max = max;
		this.value = "";// myDefault;
		// TODO Auto-generated constructor stub
	}

	public Text(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element, int max,
			String value) {
		super(id, previous, next, help, label, required, element);

		this.max = max;
		this.value = value;
	}

	@Override
	public View getLayout(MaritacaActivityController controller) {
		EditText campoDeResposta = new EditText(controller);
		campoDeResposta.setText(getValue().toString());
		return campoDeResposta;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void save(View answer) {
		// TODO Auto-generated method stub
		value = ((TextView) answer).getText();
	}

	@Override
	public void setValue(Object obj) {
		value = obj;
	}

	@Override
	public boolean validate() {
		// TODO por enquanro verificar se o campo é obrigatorio
		if (value.toString().equals("")) {
			return !this.getRequired();
		} else {
			Integer size = value.toString().length();
			if (max != null) {
				if (size <= max)
					return true;
				return false;
			}
			return true;
		}
	}

	@Override
	public String toString() {
		String result = super.toString() + ", max: " + max + ", value: "
				+ value;
		return result;
	}
}
