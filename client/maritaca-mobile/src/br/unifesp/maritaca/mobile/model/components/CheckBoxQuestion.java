package br.unifesp.maritaca.mobile.model.components;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.activities.R;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.model.adaptors.CheckBoxAdapter;
import br.unifesp.maritaca.mobile.model.adaptors.OptionViewHolder;
import br.unifesp.maritaca.mobile.model.components.util.Option;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.Constants;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public class CheckBoxQuestion extends Question {

	private ListView listView;
	private CheckBox checkBox;
	private ArrayAdapter<Option> listAdapter;
	private List<Option> options;
	private Option option;
	
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
		listView = new ListView(activity);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				Option option = listAdapter.getItem(position);
				option.toggleChecked();
				OptionViewHolder viewHolder = (OptionViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(option.isChecked());
			}
		});
		
		ArrayList<Option> optionList = new ArrayList<Option>();
	    optionList.addAll(options);
	    
	    listAdapter = new CheckBoxAdapter(activity, optionList);
	    listView.setAdapter(listAdapter);
	    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		return listView;
	}

	@Override
	public boolean validate() {
		return required ? (!getValue().equals("") ? true : false) : true;
	}

	@Override
	public void save(View answer) {
		int firstPosition = listView.getFirstVisiblePosition();
		int size = listView.getCount();
        for(int i = firstPosition; i < size; i++) {
        	View view = listView.getChildAt(i);
        	checkBox = (CheckBox)view.findViewById(R.id.rowCheckBox);
        	if(checkBox.isChecked()) {
        		option = (Option)listAdapter.getItem(i);
        		Log.i("Info", ""+option.getId() + " - " + option.getText() + " - " + option.isChecked());
        		//TODO: Save 
        	}
    	}
	}
}
