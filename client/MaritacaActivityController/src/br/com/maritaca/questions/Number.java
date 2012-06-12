package br.com.maritaca.questions;

import org.w3c.dom.Element;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import br.com.maritaca.activity.MaritacaActivityController;
import br.com.maritaca.xml.parser.XMLParser;

public class Number extends Question {

	/*
	 * 
	 * Nao esquecer que na classe Number default sera um Number mas como estamos
	 * no XML pode-se tratar como Integer
	 */

	private Integer min, max;

	public Number(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);
		Integer max = XMLParser.myParseInteger(element.getAttribute("max"));
		// FIXME o mesmo do Text
		// String myDefault = XMLParser.getTagValue("default", element);
		Integer min = XMLParser.myParseInteger(element.getAttribute("min"));

		/* Elementos da classe MyNumber */
		this.min = min;
		this.max = max;
		this.value = "";
		// this.setValue(XMLParser.myParseInteger(myDefault));
	}

	public Number(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element, Integer min,
			Integer max, Integer myDefault) {
		super(id, previous, next, help, label, required, element);

		// #############################IMPORTANTE###############################
		// ######################################################################
		// //OBS: Ou seja, posteriormente "max", "min" "myDefault"
		// Integer max = XMLParser.myParseInteger(element.getAttribute("max"));
		// //Não serão passados como parametro no construtor e sim
		// String myDefault = XMLParser.getTagValue("default", element); //Pelo
		// parser.
		// Integer min = XMLParser.myParseInteger(element.getAttribute("min"));
		// ######################################################################
		// ######################################################################

		/* Elementos da classe MyNumber */
		this.min = min;
		this.max = max;

		this.value = myDefault; // Trocar essa linha pela de baixo apos a adição
								// do parser.
		// this.setMyDefault(XMLParser.myParseInteger(myDefault));
	}

	/*
	 * Variaveis, setters e getters da classe MyNumber
	 */

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	@Override
	public Long getValue() throws NumberFormatException {
		// TODO Auto-generated method stub
		return value.toString().equals("") ? null : new Long(value.toString());
	}

	@Override
	public void setValue(Object obj) {
		value = obj;
	}

	// TODO checar se os campos max e min sempre vao ter valor
	@Override
	public boolean validate() {
		Long valor = null;
		try {
			valor = this.getValue();
		} catch (NumberFormatException e) {
			return false;
		}
		if (valor != null) {
			if (min != null && max != null) {
				return (valor >= min && valor <= max) ? true : false;
			}
		}
		return !this.getRequired();

	}

	@Override
	public View getLayout(MaritacaActivityController controller) {
		EditText campoDeResposta = new EditText(controller);
		campoDeResposta.setInputType(InputType.TYPE_CLASS_NUMBER);
		campoDeResposta
				.setText(getValue() == null ? "" : getValue().toString());
		return campoDeResposta;
	}

	@Override
	public String toString() {
		String result = super.toString() + ", min: " + min + "," + "max: "
				+ max + ", default: " + value;
		return result;
	}

	@Override
	public void save(View answer) {
		// TODO Auto-generated method stub
		value = ((TextView) answer).getText();
	}
}