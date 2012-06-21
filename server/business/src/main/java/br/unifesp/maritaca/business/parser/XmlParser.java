package br.unifesp.maritaca.business.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlParser {

	/**
	 * Parse the question labels of the given form XML.
	 * 
	 * @param formXml
	 * @return
	 * @author tiagobarabasz
	 */
	public static List parseQuestionsLabels(String formXml) {
		List<String> questions = new ArrayList<String>();
		Document document = xmlToDocument(formXml);
		NodeList list = questionNodes(document);
		List<String> types = new ArrayList<String>();
		List questionsTypes = new ArrayList<List<String>>();
		list = list.item(0).getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			types.add(node.getNodeName());
			if (node.hasChildNodes()) {
				String question = questionLabelFromNode(node);
				questions.add(question);
			} else {
				questions.add("-");
			}
		}
		questionsTypes.add(types);
		questionsTypes.add(questions);
		return questionsTypes;
	}

	/**
	 * Parse the answers to the questions of the given answer XML.
	 * 
	 * @param formXml
	 * @return
	 * @author tiagobarabasz
	 */
	public static List<String> parseAnswers(String answerXml) {
		List<String> answers = new ArrayList<String>();
		Document document = xmlToDocument(answerXml);
		NodeList list = answerNodes(document);
		list = list.item(0).getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String answer = answerFromNode(node);
			answers.add(answer);
		}
		return answers;
	}

	private static String answerFromNode(Node node) {
		return node.getFirstChild().getFirstChild().getNodeValue();
	}

	private static NodeList answerNodes(Document document) {
		return document.getFirstChild().getFirstChild().getNextSibling()
				.getNextSibling().getFirstChild().getChildNodes();
	}

	private static String questionLabelFromNode(Node node) {
		return node.getFirstChild().getNextSibling().getFirstChild()
				.getNodeValue();
	}

	private static NodeList questionNodes(Document document) {
		return document.getElementsByTagName("questions");
	}

	// TODO Change to private when the RandomAnswerCreator is eliminated
	protected static Document xmlToDocument(String formXml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();

			inStream.setCharacterStream(new StringReader(formXml));

			Document document = dBuilder.parse(inStream);
			document.getDocumentElement().normalize();

			return document;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
