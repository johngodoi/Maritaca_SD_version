package br.unifesp.maritaca.mobile.model.components;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.UtilConverters;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public class Number extends Question {

	private Integer min;
	private Integer max;
	
	public Number(	Integer id, Integer previous, 
					Integer next, String help,
					String label, Boolean required, 
					Element element) {
		super(id, previous, next, help, label, required, element);
		
		this.min = UtilConverters.convertStringToInteger(element.getAttribute(Constants.MIN));
		this.max = UtilConverters.convertStringToInteger(element.getAttribute(Constants.MAX));
		String  attrDefault = XMLFormParser.getTagValue(Constants.DEFAULT, element);

		super.value = UtilConverters.convertStringToInteger(attrDefault);
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.NUMBER;
	}
	
	public Integer getNext() {		
		if(clauses.length < 1)
			return next;
		for(int i = 0; i < clauses.length; i++){
			Integer value = this.getValue();
			if(clauses[i].evaluate(value)) {
				return clauses[i].getGoToIndex(); 
			}
		}
		return next;
	}
	
	@Override
	public Integer getValue() {
		return value != null ? new Integer(value.toString()) : null;
	}

	@Override
	public View getLayout(ControllerActivity activity) {
		EditText field = new EditText(activity);
		field.setInputType(InputType.TYPE_CLASS_NUMBER);
		String value = getValue() != null ? getValue().toString() : ""; 
		field.setText(value);
		return field;	
	}

	@Override
	public boolean validate() {
		if (getValue() >= min && getValue() <= max) {
			return true;
		}		
		return false;
	}

	@Override
	public void save(View answer) {
		value = ((TextView) answer).getText();	
	}
	
	@Override
	public String toString() {
		String result = super.toString() + 
						", min: " + min +  
						", max: " + max + 
						", default: " + value;
		return result;
	}
}