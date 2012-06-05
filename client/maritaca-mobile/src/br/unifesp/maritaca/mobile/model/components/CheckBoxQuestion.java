package br.unifesp.maritaca.mobile.model.components;

import java.util.List;

import org.w3c.dom.Element;

import android.view.View;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.model.components.util.Option;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public class CheckBoxQuestion extends Question {

	private List<Option> options;
	
	public CheckBoxQuestion(Integer id, Integer previous, 
			Integer next, String help, 
			String label, Boolean required, 
			Element element) {
		
		super(id, previous, next, help, label, required, element);
		
		super.value = XMLFormParser.getTagValue(Constants.DEFAULT, element);
		this.options = XMLFormParser.getOptions(element);
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.CHECKBOX;
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
		return null;
	}

	@Override
	public boolean validate() {
		return required ? (!getValue().equals("") ? true : false) : true;
	}

	@Override
	public void save(View answer) {
	}
}
