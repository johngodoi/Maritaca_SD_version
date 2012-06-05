package br.unifesp.maritaca.mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
import br.unifesp.maritaca.mobile.model.Clause;
import br.unifesp.maritaca.mobile.model.Question;
import br.unifesp.maritaca.mobile.model.comparators.Equal;
import br.unifesp.maritaca.mobile.model.comparators.Greater;
import br.unifesp.maritaca.mobile.model.comparators.GreaterEqual;
import br.unifesp.maritaca.mobile.model.comparators.Less;
import br.unifesp.maritaca.mobile.model.comparators.LessEqual;
import br.unifesp.maritaca.mobile.model.components.ComboBoxQuestion;
import br.unifesp.maritaca.mobile.model.components.DateQuestion;
import br.unifesp.maritaca.mobile.model.components.Number;
import br.unifesp.maritaca.mobile.model.components.RadioButtonQuestion;
import br.unifesp.maritaca.mobile.model.components.Text;
import br.unifesp.maritaca.mobile.model.components.util.Option;

public class XMLFormParser {

	private Document document; // DOM
	private InputStream file;
	private String formId;
	
	public XMLFormParser(InputStream is) {
		this.file = is;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(is);
			document.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Question[] getQuestions() {
		try {			
			NodeList nodeList = document.getElementsByTagName(Constants.FORM_ID);
			Node node = nodeList.item(0);
			node.getFirstChild().getNodeValue();
			
			nodeList = document.getElementsByTagName(Constants.FORM_TITLE);
			node = nodeList.item(0);
			node.getFirstChild().getNodeValue();
			
			nodeList = document.getElementsByTagName(Constants.FORM_URL);
			node = nodeList.item(0);
			node.getFirstChild().getNodeValue();
			
			int totalQuestions = computeTotalQuestions();
			if(totalQuestions == 0) {
				return null;
			}
			Question[] questions = new Question[totalQuestions];
			
			nodeList = document.getElementsByTagName(Constants.FORM_QUESTIONS);
			node = nodeList.item(0);
			nodeList = node.getChildNodes();
			
			int questionIndex = 0;
			int x = nodeList.getLength();
			
			for (int i = 0; i < x; i++) {
				node = nodeList.item(i);

				Integer previous = questionIndex - 1;
				if (previous < 0) {
					previous = null;
				}
				if(node instanceof Element && node.hasAttributes()) {
					Element element  = (Element) node;
					Integer id 		 = UtilConverters.convertStringToInteger(element.getAttribute(Constants.XML_ID));				
					Integer next 	 = UtilConverters.convertStringToInteger(element.getAttribute(Constants.XML_NEXT));
					Boolean required = Boolean.parseBoolean(element.getAttribute(Constants.XML_REQUIRED));
					String 	help	 = getTagValue(Constants.XML_HELP, element);
					String 	label 	 = getTagValue(Constants.XML_LABEL, element);
	
					// create a question
					questions[questionIndex] = specificQuestion(node.getNodeName(), id, previous, next, help, 
													label, required, element);
					questionIndex++;
				}
			}
			return questions;
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
		}
		return null;
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
				question = new ComboBoxQuestion(id, previous, next, help, label, required, element);
				break;
			case RADIOBOX:
				question = new RadioButtonQuestion(id, previous, next, help, label, required, element);
				break;
			case CHECKBOX:
				break;
			case DATE:
				question = new DateQuestion(id, previous, next, help, label, required, element);
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
			case GPS:
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
		NodeList list = element.getElementsByTagName(tag);
		if(list.item(0) == null)
			return null;
		list = list.item(0).getChildNodes();
		Node node = (Node) list.item(0);
		return node != null ? node.getNodeValue() : null;
	}
	
	public static String getDateFormat(String string) {		
		if(string == null || "".equals(string))
			return Constants.DATE_ISO8601FORMAT;
		else
			return string;
	}
	
	public static List<Option> getOptions(Element element) {
		NodeList list = element.getElementsByTagName(Constants.XML_OPTION);		
		List<Option> options = new ArrayList<Option>(list.getLength());
		for(int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Option option = new Option(i, node.getFirstChild().getNodeValue());
			options.add(option);
		}		
		return options;
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