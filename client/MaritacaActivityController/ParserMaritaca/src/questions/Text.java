package questions;

import org.w3c.dom.Element;

import parser.XMLParser;

public class Text extends Question {

	/* OBS: nessa classe deve ser tratado o uso do element */

	/* Elementos da classe Text */
	private Integer max;
	private String myDefault;

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public String getMyDefault() {
		return myDefault;
	}

	public void setMyDefault(String myDefault) {
		this.myDefault = myDefault;
	}

	public Text(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);
		Integer max = XMLParser.myParseInteger(element.getAttribute("max"));
		String myDefault = XMLParser.getTagValue("default", element);
		/* Elementos da classe Text */
		this.max = max;
		this.myDefault = myDefault;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
	}

	@Override
	public String toString() {
		String result = super.toString() + ", max: " + max + ", default: "
				+ myDefault;
		return result;
	}
}
