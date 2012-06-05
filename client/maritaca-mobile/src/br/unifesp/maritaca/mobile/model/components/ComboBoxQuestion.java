package br.unifesp.maritaca.mobile.model.components;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import android.R;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.model.components.util.Option;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public class ComboBoxQuestion extends Question {
	
	private Spinner spinner;
	private List<Option> options;

	public ComboBoxQuestion(Integer id, Integer previous, 
			Integer next, String help, 
			String label, Boolean required, 
			Element element) {
		
		super(id, previous, next, help, label, required, element);
		
		super.value = XMLFormParser.getTagValue(Constants.DEFAULT, element);
		this.options = XMLFormParser.getOptions(element);		
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.COMBOBOX;
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
		int pos = -1;
		spinner = new Spinner(activity);
		List<String> optionsLst = new ArrayList<String>(options.size());
		for(Option s : options) {			
			optionsLst.add(s.getText());
			if(s.getText().equals(getValue())) {
				pos = s.getId();
			}
		}		
		//TODO: Create a custom adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, optionsLst);
		spinner.setAdapter(adapter);
		spinner.setSelection(pos);
		return spinner;
	}

	@Override
	public boolean validate() {
		return required ? (!getValue().equals("") ? true : false) : true;
	}

	@Override
	public void save(View answer) {
		value = spinner.getSelectedItem().toString();
	}
}