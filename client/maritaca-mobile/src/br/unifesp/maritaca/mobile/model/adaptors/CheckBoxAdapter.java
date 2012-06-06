package br.unifesp.maritaca.mobile.model.adaptors;

import java.util.List;

import br.unifesp.maritaca.mobile.activities.R;
import br.unifesp.maritaca.mobile.model.components.util.Option;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheckBoxAdapter extends ArrayAdapter<Option> {	

	private LayoutInflater layoutInflater;
	
	public CheckBoxAdapter(Context context, List<Option> optionList) {
		super( context, R.layout.checkbox, R.id.rowTextView, optionList);
		layoutInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Option option = (Option)this.getItem(position);
		CheckBox checkBox; 
		TextView textView;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.checkbox, null);
			textView = (TextView)convertView.findViewById( R.id.rowTextView);
			checkBox = (CheckBox)convertView.findViewById( R.id.rowCheckBox);
			convertView.setTag(new OptionViewHolder(textView, checkBox));
			    
			checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v ;
					Option option = (Option)cb.getTag();
					option.setChecked( cb.isChecked() );
				}
			});
		}
		else {
			OptionViewHolder viewHolder = (OptionViewHolder) convertView.getTag();
			checkBox = viewHolder.getCheckBox() ;
			textView = viewHolder.getTextView() ;
		}
		checkBox.setTag(option);
		checkBox.setChecked(option.isChecked());
		textView.setText(option.getText());      
	  
		return convertView;
	}
}