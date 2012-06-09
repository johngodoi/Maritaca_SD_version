package questions;

import org.w3c.dom.Element;

import parser.XMLParser;

public class MyNumber extends Question {

	/*
	 * 
	 * Nao esquecer que na classe Number default sera um Number mas como estamos
	 * no XML pode-se tratar como Integer
	 */
	public MyNumber(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		super(id, previous, next, help, label, required, element);
		Integer max = XMLParser.myParseInteger(element.getAttribute("max"));
		String myDefault = XMLParser.getTagValue("default", element);
		Integer min = XMLParser.myParseInteger(element.getAttribute("min"));

		/* Elementos da classe MyNumber */
		this.min = min;
		this.max = max;
		this.setMyDefault(XMLParser.myParseInteger(myDefault));
	}

	/*
	 * Variaveis, setters e getters da classe MyNumber
	 */

	private Integer min, max, myDefault;

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Integer getMyDefault() {
		return myDefault;
	}

	public void setMyDefault(Integer myDefault) {
		this.myDefault = myDefault;
	}

	/* Elementos da classe number */

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
		String result = super.toString() + ", min: "+ min + "," + "max: " + max + ", default: "
				+ myDefault;
		return result;
	}
	
}
