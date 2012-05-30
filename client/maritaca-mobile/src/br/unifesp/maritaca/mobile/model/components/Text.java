package br.unifesp.maritaca.mobile.model.components;

import org.w3c.dom.Element;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public class Text extends Question {
	
	private Integer max;
	
	public Text(	Integer id, Integer previous, 
					Integer next, String help,
					String label, Boolean required, 
					Element element) {
		super(id, previous, next, help, label, required, element);
		
		this.max = XMLFormParser.convertStringToInteger(element.getAttribute(Constants.MAX));
		super.value = XMLFormParser.getTagValue(Constants.DEFAULT, element);
	}
	
	@Override
	public ComponentType getComponentType() {
		return ComponentType.TEXT;
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
		EditText field = new EditText(activity);
		field.setInputType(InputType.TYPE_CLASS_TEXT);
		field.setText(getValue().toString());
		return field;	
	}
	
	@Override
	public boolean validate() {
		if (getValue().length() <= max) {
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
						", max: " + max + 
						", default: "	+ value;
		return result;
	}
}
