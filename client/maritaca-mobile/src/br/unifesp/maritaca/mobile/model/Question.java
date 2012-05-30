package br.unifesp.maritaca.mobile.model;

import org.w3c.dom.Element;

import android.view.View;
import br.unifesp.maritaca.mobile.activities.ControllerActivity;
import br.unifesp.maritaca.mobile.util.ComponentType;
import br.unifesp.maritaca.mobile.util.XMLFormParser;

public abstract class Question {

	/* Basic elements */
	protected Object value;
	
	protected Integer id;
	protected Integer next;
	// TODO it's not considered yet, but it'll be
	private Integer previous;
	
	protected Clause clauses[];

	protected String label;
	protected String help;

	protected Boolean required;
	
	private Element element;
	
	public Question(Integer id, Integer previous, 
					Integer next, String help,
					String label, Boolean required, 
					Element element) {
		this.id = id;
		this.previous = previous;
		this.next = next;
		this.label = label;
		this.help = help;
		this.required = required;
		this.element = element;
		this.clauses = XMLFormParser.parseClauses(element);
	}
	
	/* Abstract methods */
	public abstract ComponentType getComponentType();
	
	public abstract Integer getNext();
		
	public abstract Object getValue();	
	
	public abstract View getLayout(ControllerActivity activity);
	
	public abstract boolean validate();
	
	public abstract void save(View answer);
		
	/* Methods */
	public void setValue(Object obj) {
		this.value = obj;
	}
	
	public String toString() {
		return "Value: " + value + 
				", ID: " + id + 
				", previous: " + previous + 
				", next: " + next + 
				", help: " + help + 
				", label: " + label + 
				", required: " + required;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getHelp() {
		return help;
	}

	public Boolean getRequired() {
		return required;
	}

	public Element getElement() {
		return element;
	}
	
	public Integer getPrevious() {
		return previous;
	}

	public void setPrevious(Integer previous) {
		this.previous = previous;
	}
}
