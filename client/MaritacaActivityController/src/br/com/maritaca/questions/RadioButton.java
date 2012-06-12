package br.com.maritaca.questions;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import br.com.maritaca.activity.MaritacaActivityController;
import br.com.maritaca.xml.parser.XMLParser;

public class RadioButton extends Question {

	List<String> radiosOptions = new ArrayList<String>();
	private String labelDefault = "";

	public RadioButton(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);

		NodeList items = element.getElementsByTagName("item");
		labelDefault = XMLParser.getTagValue("default", element);
		Element node = null;

		for (int i = 0; i < items.getLength(); i++) {
			node = (Element) items.item(i);
			radiosOptions.add(XMLParser.getTagValue("item", node));
		}
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return this.getRequired() ? value.toString() != "" ? true : false
				: true;
	}

	@Override
	public void save(View answer) {
		RadioGroup radioGroup = (RadioGroup) answer;
		Integer id = radioGroup.getCheckedRadioButtonId();
		Log.v("ID ", id.toString());
		android.widget.RadioButton radio = (android.widget.RadioButton) radioGroup
				.findViewById(id);
		if (radio != null) {
			String label = (String) radio.getText();
			for (String radioLabel : radiosOptions) {
				if (radioLabel.equals(label)) {
					value = "" + (radiosOptions.indexOf(radioLabel));
				}
			}
		} else {
			value = "";
		}
		Log.v("RADIOVALUE", value.toString());
	}

	@Override
	public View getLayout(MaritacaActivityController controller) {
		android.widget.RadioGroup radioGroup = new RadioGroup(controller);
		android.widget.RadioButton radio;
		android.widget.RadioButton radioDefault = null;

		for (String label : radiosOptions) {
			radio = new android.widget.RadioButton(controller);
			radio.setText(label);
			if (!labelDefault.equals("")) {
				if (radiosOptions.indexOf(label) == Integer
						.parseInt(labelDefault))
					radioDefault = radio;
			}
			radioGroup.addView(radio);
		}
		if (radioDefault != null) {
			radioGroup.check(radioDefault.getId());
		}
		return radioGroup;
	}

	@Override
	public void setValue(Object obj) {
		value = obj;
	}

}