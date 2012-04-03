package questions;

import org.w3c.dom.Element;

public abstract class Question {

	/* Elementos basicos de qualquer Question */
	private Integer id, previous, next;
	private String label, help;
	private Boolean required;
	/*
	 * Cuidado com Element, ele e' usado no constructor das classes que herdam
	 * de Question
	 */
	private Element element;

	/* Metodos getters e setters */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPrevious() {
		return previous;
	}

	public void setPrevious(Integer previous) {
		this.previous = previous;
	}

	public Integer getNext() {
		return next;
	}

	public void setNext(Integer next) {
		this.next = next;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	/* Construtor */
	public Question(Integer id, Integer previous, Integer next, String help,
			String label, Boolean required, Element element) {
		this.id = id;
		this.previous = previous;
		this.next = next;
		this.label = label;
		this.help = help;
		this.required = required;
		this.element = element;
	}

	/*
	 * Metodo toString para a classe que imprime os elementos basicos que todo
	 * question tem em comum
	 */
	@Override
	public String toString() {
		return "ID: " + id + ", previous: " + previous + ", next: " + next
				+ ", " + "help: " + help + ", label: " + label + ", required: "
				+ required;
	}

	/* Metodos abstratos */
	public abstract Object getValue();

	public abstract void setValue(Object obj);

	public abstract boolean validate();

	public abstract boolean save();

	public abstract void show();
	
	

}
