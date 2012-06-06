package br.unifesp.maritaca.business.parser;

import java.io.StringWriter;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Temporary class used to create random answers to a given
 * form.
 * @author tiagobarabasz
 */
public class RandomAnswersCreator extends XmlParser{

	private static final String ROOT = "response";
	private static final String FORMID = "formid";
	private static final String USERID = "userid";
	private static final String RESPONSEID = "responseid";
	private static final String NUMBEROFANSWERS = "numberofanswers";
	private static final String ANSWER = "answer";
	private static final String ANSWERS = "answers";
	private static final String TIMESTAMP = "timestamp";
	private static final String QUESTION = "question";
	private static final String VALUE = "value";
	
	private static final Random rand = new Random();
	
	public static String createRandomAnswersFromForm(String formXml){
		Document               document = xmlToDocument(formXml);
		
		DocumentBuilderFactory factory           = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document               responsesDocument = dBuilder.newDocument();		
		
		Element                root              = responsesDocument.createElement(ROOT);
		responsesDocument.appendChild(root);
		
		NodeList list = document.getFirstChild().getFirstChild()
				.getNextSibling().getChildNodes();

		Text text; // Pointer para armazenar os valores de um node
		Element element; // Pointer para armazenar os valores do element

		/* Answer para preencher */
		
		Element answer = responsesDocument.createElement(ANSWER);
		answer.setAttribute(TIMESTAMP, "201204011831000000");				

		for (int i = 0; i < list.getLength(); i++ ) {
			Node node = list.item(i);
			String type = node.getNodeName();

			if (type.compareTo(FORMID) == 0 || type.compareTo(USERID) == 0) {
				element = responsesDocument.createElement(type);
				text = responsesDocument.createTextNode(node.getFirstChild()
						.getNodeValue());
				element.appendChild(text);
				root.appendChild(element);
			} else {

				if (type.compareTo("number") == 0) {
					text = responsesDocument.createTextNode(Integer.toString(generateNumber()));
				} else {
					text = responsesDocument.createTextNode(generateString(5));
				}
				/* Pega o atributo para fazer um set na question */
				element = (Element) node;
				String attribute = element.getAttribute("id");
				Element question = responsesDocument.createElement(QUESTION);
				question.setAttribute("id", attribute);

				Element value = responsesDocument.createElement(VALUE);
				value.appendChild(text);
				question.appendChild(value);

				answer.appendChild(question);
			}
		}
		/* Cria o item responseid */
		element = responsesDocument.createElement(RESPONSEID);
		/* Cria o texto para responseid */
		text = responsesDocument.createTextNode(generateString(5));
		element.appendChild(text);
		root.appendChild(element);

		element = responsesDocument.createElement(NUMBEROFANSWERS);
		text = responsesDocument.createTextNode(Integer.toString(1));
		element.appendChild(text);
		root.appendChild(element);

		Element answers = responsesDocument.createElement(ANSWERS);
		answers.appendChild(answer);
		
		root.appendChild(answers);
		
		try{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(responsesDocument), new StreamResult(writer));
			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
			return output;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	private static String generateString(int i) {			
		return "String"+generateNumber();
	}

	private static int generateNumber() {
		return rand.nextInt(20)+2;
	}

}
