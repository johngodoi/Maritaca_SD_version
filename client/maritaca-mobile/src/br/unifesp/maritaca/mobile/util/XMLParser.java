package br.unifesp.maritaca.mobile.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.unifesp.maritaca.mobile.model.Clause;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.model.comparators.Equal;
import br.unifesp.maritaca.mobile.model.comparators.Greater;
import br.unifesp.maritaca.mobile.model.comparators.GreaterEqual;
import br.unifesp.maritaca.mobile.model.comparators.Less;
import br.unifesp.maritaca.mobile.model.comparators.LessEqual;
import br.unifesp.maritaca.mobile.model.components.Text;
import br.unifesp.maritaca.mobile.model.components.Number;

import android.util.Log;

public class XMLParser {

	private Document document; // DOM
	private String formId;
	private String formTitle;
	private String formXml;
	
	public XMLParser(InputStream is) {
		// TODO validate the xml format with DTD
		buildHeaderForm(is);
	}

	public Question[] getQuestions() {
		int totalQuestions = computeTotalQuestions();

		if (totalQuestions == 0){
			return null;
		}

		Question[] questions = new Question[totalQuestions];
		
		NodeList nodeList = document.getElementsByTagName(Constants.FORM_QUESTIONS);
		Node node = nodeList.item(0);
		nodeList = node.getChildNodes();

//		int questionIndex = 0;

		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);

			Integer previous = i - 1;
			if (previous < 0) {
				previous = null;
			}

			Element element  = (Element) node;
			Integer id 		 = convertStringToInteger(element.getAttribute(Constants.XML_ID));				
			Integer next 	 = convertStringToInteger(element.getAttribute(Constants.XML_NEXT));
			Boolean required = Boolean.parseBoolean(element.getAttribute(Constants.XML_REQUIRED));
			String 	help	 = getTagValue(Constants.XML_HELP, element);
			String 	label 	 = getTagValue(Constants.XML_LABEL, element);

			// create a question
			questions[i] = specificQuestion(node.getNodeName(), 
														id,
														previous, 
														next, 
														help, 
														label, 
														required, 
														element);

		}

		return questions;
	}

	private void buildHeaderForm(InputStream is) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(is);
			document.getDocumentElement().normalize();
			
			NodeList nodeList = document.getElementsByTagName(Constants.FORM_ID);
			Node node = nodeList.item(0);
			formId = node.getFirstChild().getNodeValue();
			
			nodeList = document.getElementsByTagName(Constants.FORM_TITLE);
			node = nodeList.item(0);
			formTitle = node.getFirstChild().getNodeValue();
			
			nodeList = document.getElementsByTagName(Constants.FORM_XML);
			node = nodeList.item(0);
			formXml = node.getFirstChild().getNodeValue();
			
			// Building the form questions
			InputStream xmlIS = new ByteArrayInputStream(formXml.getBytes("UTF-8"));
			document = dBuilder.parse(xmlIS);
			document.getDocumentElement().normalize();			
			
		} catch (Exception e) {
			// TODO define report errors
			Log.e("ERROR", e.getMessage());
		}
	}
	
	/**
	 * This method computes total questions
	 * @return
	 */
	private int computeTotalQuestions() {
		int count = 0;
		for (ComponentType type : ComponentType.values()) {
			count += countQuestionType(type.getDescription());
		}
		return count;
	}
	
	private int countQuestionType(String type) {
		return (type == null) ? 0 : document.getElementsByTagName(type).getLength();
	}

	private Question specificQuestion(	String type, 
										Integer id,
										Integer previous, 
										Integer next, 
										String help, 
										String label,
										Boolean required, 
										Element element) {
		Question question = null;
		switch (ComponentType.getComponentTypeByDescription(type)) {
			case TEXT:
				question = new Text(id, previous, next, help, label, required, element);
				break;
			case NUMBER:
				question = new Number(id, previous, next, help, label, required, element);
				break;
		
			case COMBOBOX:
				break;
			case RADIOBOX:
				break;
			case CHECKBOX:
				break;
			case DATE:
				break;
			case TIME:
				break;
			case TIMESTAMP:
				break;
			case SLIDE:
				break;
			case PICTURE:
				break;
			case AUDIO:
				break;
			case VIDEO:
				break;
			case LOCATION:
				break;
			case TEMPERATURE:
				break;
			case SIGNAL:
				break;
			default:
				break;
		}

		return question;
	}

	// Static Methods 
	/**
	 * This method obtains the "tag" value from element  
	 * @param tag value
	 * @param element
	 * @return the value
	 */
	public static String getTagValue(String tag, Element element) {
		NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) list.item(0);
		return node != null ? node.getNodeValue() : null;
	}

	/**
	 * This method is to convert String to Integer.
	 * @param string that will be converted
	 * @return Integer conversion
	 */
	public static Integer convertStringToInteger(String string) {
		try {
			if("".equals(string)) {
				return null;
			}
			Integer value = Integer.parseInt(string);
			return value;
		} catch (NumberFormatException e) {
			// TODO Exception
			return null;
		}
	}

	public static Clause[] parseClauses(Element element) {
		NodeList list = element.getElementsByTagName("if");
		Clause clauses[] = new Clause[list.getLength()];

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			// TODO this order means precedence ?
			String attribute = ((Element)node).getAttribute(Constants.XML_COMPARISON); 
			String value 	 = ((Element)node).getAttribute(Constants.XML_VALUE);
			Integer goTo 	 = Integer.parseInt(((Element)node).getAttribute(Constants.XML_GOTO));
			
			if(attribute.equals(Constants.XML_EQUAL)){
				clauses[i] = new Equal(value, goTo);
			}
			if(attribute.equals(Constants.XML_LESS)){
				clauses[i] = new Less(value, goTo);
			}
			if(attribute.equals(Constants.XML_GREATER)){
				clauses[i] = new Greater(value, goTo);
			}
			if(attribute.equals(Constants.XML_LESSEQUAL)){
				clauses[i] = new LessEqual(value, goTo);
			}
			if(attribute.equals(Constants.XML_GREATEREQUAL)){
				clauses[i] = new GreaterEqual(value, goTo);
			}
		}
		return clauses;
	}
}