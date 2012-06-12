package br.com.maritaca.questions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;
import android.view.View;
import br.com.maritaca.activity.MaritacaActivityController;

public abstract class Question {
	/* Elementos basicos de qualquer Question */
	protected Object value;
	private Integer id, previous, next;
	private String label, help;
	private Boolean required;

	public final static Integer END = new Integer(-1);

	private static final String LESS = "less";
	private static final String LESSEQUAL = "lessequal";
	private static final String EQUAL = "equal";
	private static final String GREATEREQUAL = "greaterequal";
	private static final String GREATER = "greater";

	// mapa com valores e perguntas
	private Map<String, String> less = new HashMap<String, String>();
	private Map<String, String> lessEqual = new HashMap<String, String>();
	private Map<String, String> equal = new HashMap<String, String>();
	private Map<String, String> greaterEqual = new HashMap<String, String>();
	private Map<String, String> greater = new HashMap<String, String>();

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
		Integer nextQuestion = this.solveNextQuestion();
		return nextQuestion != null ? nextQuestion : this.next;
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
		this.montaRespostas(element);
	}

	private void montaRespostas(Element element) {
		NodeList condicionais = element.getElementsByTagName("if");
		if (condicionais != null) {
			for (int i = 0; i < condicionais.getLength(); i++) {
				Node node = condicionais.item(i);
				if (node instanceof Element) {
					Element elementoCondicional = (Element) node;
					solverCondicional(elementoCondicional);

				}
			}
		}
	}

	private void solverCondicional(Element elementoCondicional) {
		String comparsion = elementoCondicional.getAttribute("comparison");
		String val = elementoCondicional.getAttribute("value");
		String go = elementoCondicional.getAttribute("goto");
		Log.v("comparsion", "Comparsion: " + comparsion + "\nVal: " + val
				+ "\nGoto: " + go);
		if (comparsion.equals(LESS)) {
			less.put(val, go);
		} else {
			if (comparsion.equals(LESSEQUAL)) {
				lessEqual.put(val, go);
			} else {
				if (comparsion.equals(EQUAL)) {
					equal.put(val, go);
				} else {
					if (comparsion.equals(GREATEREQUAL)) {
						greaterEqual.put(val, go);
					} else {
						if (comparsion.equals(GREATER)) {
							greater.put(val, go);
						} else {
							throw new RuntimeException(
									"Valor da tag comparsion errada");
						}
					}
				}
			}
		}
	}

	private Integer solveNextQuestion() {
		String valorDaResposta = value.toString();
		if (valorDaResposta.equals(""))
			return null;
		Log.v("VALUE", valorDaResposta);
		if (equal.containsKey(valorDaResposta))
			return Integer.valueOf(equal.get(valorDaResposta));

		for (Entry<String, String> valor : greaterEqual.entrySet()) {
			Integer valorKey = new Integer(valor.getKey());
			if (new Integer(valorDaResposta) >= valorKey)
				return Integer.valueOf(valor.getValue());
		}
		for (Entry<String, String> valor : greater.entrySet()) {
			Integer valorKey = new Integer(valor.getKey());
			if (new Integer(valorDaResposta) > valorKey)
				return Integer.valueOf(valor.getValue());
		}

		for (Entry<String, String> valor : lessEqual.entrySet()) {
			Integer valorKey = new Integer(valor.getKey());
			if (new Integer(valorDaResposta) <= valorKey)
				return Integer.valueOf(valor.getValue());
		}
		for (Entry<String, String> valor : less.entrySet()) {
			Integer valorKey = new Integer(valor.getKey());
			if (new Integer(valorDaResposta) < valorKey)
				return Integer.valueOf(valor.getValue());
		}

		return null;
	}

	/*
	 * Metodo toString para a classe que imprime os elementos basicos que todo
	 * question tem em comum
	 */
	@Override
	public String toString() {
		return "Value: " + value + "ID: " + id + ", previous: " + previous
				+ ", next: " + next + ", " + "help: " + help + ", label: "
				+ label + ", required: " + required;
	}

	/* Metodos abstratos */
	public abstract Object getValue();

	public abstract void setValue(Object obj);

	public abstract boolean validate();

	public abstract void save(View answer);

	public abstract View getLayout(MaritacaActivityController controller);
}
