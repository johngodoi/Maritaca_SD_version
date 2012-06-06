package br.unifesp.maritaca.mobile.model.components;

import java.util.List;

import org.w3c.dom.Element;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.model.components.util.Option;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public class RadioButtonQuestion extends Question {
	
	private RadioGroup radioGroup;
	private int idWrapContent = RadioGroup.LayoutParams.WRAP_CONTENT;
	private List<Option> options;

	public RadioButtonQuestion(Integer id, Integer previous, 
			Integer next, String help,
			String label, Boolean required, 
			Element element) {
		
		super(id, previous, next, help, label, required, element);
		
		super.value = XMLFormParser.getTagValue(Constants.DEFAULT, element);
		this.options = XMLFormParser.getOptions(element);
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.RADIOBOX;
	}

	@Override
	public Integer getNext() {
		if(clauses.length < 1)
			return next;
		for(int i = 0; i < clauses.length; i++){
			String value = this.getValue();
			if(clauses[i].evaluate(value)) {
				return clauses[i].getGoToIndex(); 
			}
		}
		return next;
	}

	@Override
	public String getValue() {
		return value.toString();
	}

	@Override
	public View getLayout(ControllerActivity activity) {
		radioGroup = new RadioGroup(activity);
		for(int i = options.size()-1; i >= 0; i--) {
			RadioButton radioButton = new RadioButton(activity);
			radioButton.setId(options.get(i).getId());
			radioButton.setText(options.get(i).getText());
			if(radioButton.getText().toString().equals(value)) {
				radioButton.setChecked(true);
			}			
			LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(idWrapContent, idWrapContent);
			radioGroup.addView(radioButton, 0, layoutParams);
		}
		return radioGroup;	
	}

	@Override
	public boolean validate() {
		return required ? (!getValue().equals("") ? true : false) : true;
	}

	@Override
	public void save(View answer) {
		RadioButton radioButton = (RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
		value = radioButton.getText();
	}
}