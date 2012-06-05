package br.unifesp.maritaca.mobile.model.components.util;

public class Option {
	
	private int id;
	private String text;
	
	private boolean checked;
	
	public Option(int id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public Option(int id, String text, boolean checked) {
		super();
		this.id = id;
		this.text = text;
		this.checked = checked;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public void toggleChecked() {
		checked = !checked ;
	}
}