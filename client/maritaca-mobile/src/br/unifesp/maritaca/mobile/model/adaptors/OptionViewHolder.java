package br.unifesp.maritaca.mobile.model.adaptors;

import android.widget.CheckBox;
import android.widget.TextView;

public class OptionViewHolder {

	TextView textView;
	CheckBox checkBox;		
	
	public OptionViewHolder(TextView textView, CheckBox checkBox) {
		this.textView = textView;
		this.checkBox = checkBox;
	}
	public TextView getTextView() {
		return textView;
	}
	public void setTextView(TextView textView) {
		this.textView = textView;
	}
	public CheckBox getCheckBox() {
		return checkBox;
	}
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}
}
