package parser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import questions.*;

public class XMLParser {

	/* Tipos de questionarios que podem ser gerados */
	public static final String TEXT = "text";
	public static final String NUMBER = "number";
	public static final String CHECKLIST = "checklist";
	public static final String COMBOBOX = "combobox";
	public static final String SLIDE = "slide";
	public static final String DATE = "date";
	public static final String TIME = "time";
	public static final String TIMESTAMP = "timestamp";
	public static final String PICTURE = "picture";
	public static final String AUDIO = "audio";
	public static final String VIDEO = "video";
	public static final String LOCATION = "location";
	public static final String TEMPERATURE = "temperature";
	public static final String MOVEMENT = "movement";
	public static final String SIGNAL = "signal";

	/*
	 * Atributos da classe XMLParser
	 */
	private Document document; // Usado pelo metodo DOM
	private File file; // file, localizacao do arquivo XML
	private String formid, userid; /*
									 * Localizados no XML sera setado quando for
									 * efetuado o parser
									 */

	/*
	 * Getters e setters dos atributos
	 */
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFormid() {
		return formid;
	}

	public void setFormid(String formid) {
		this.formid = formid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	/*
	 * Construtor
	 */
	public XMLParser(File file) {
		this.file = file;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(file);
			document.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Recebe uma String, a ideia e' passar as globais static final e ele conta
	 * o numero de elementos presentes daquele tipo de question no xml
	 */
	public int countQuestionType(String type) {
		if (type == null)
			return 0;
		else
			return document.getElementsByTagName(type).getLength();
	}

	/*
	 * Usa o metodo countQuestionType e varre o XML contando o numero de
	 * questions
	 */
	public int size() {
		int count = 0;

		count += countQuestionType(TEXT);
		count += countQuestionType(NUMBER);
		count += countQuestionType(CHECKLIST);
		count += countQuestionType(COMBOBOX);
		count += countQuestionType(SLIDE);
		count += countQuestionType(DATE);
		count += countQuestionType(TIME);
		count += countQuestionType(TIMESTAMP);
		count += countQuestionType(PICTURE);
		count += countQuestionType(AUDIO);
		count += countQuestionType(VIDEO);
		count += countQuestionType(LOCATION);
		count += countQuestionType(TEMPERATURE);
		count += countQuestionType(MOVEMENT);
		count += countQuestionType(SIGNAL);

		return count;
	}

	/*
	 * Refatoracao realizada metodo getQuestions muito grande e de dificil
	 * manutencao
	 */
	private Question specificQuestion(String type, Integer id,
			Integer previous, Integer next, String help, String label,
			Boolean required, Element element) {

		Question isQuestion = null;

		if (type.compareTo(TEXT) == 0) {
			isQuestion = new Text(id, previous, next, help, label, required,
					element);
		} else if (type.compareTo(NUMBER) == 0) {
			isQuestion = new MyNumber(id, previous, next, help, label,
					required, element);

		} else if (type.compareTo(COMBOBOX) == 0) {

		} else if (type.compareTo(SLIDE) == 0) {

		} else if (type.compareTo(DATE) == 0) {

		} else if (type.compareTo(TIME) == 0) {

		} else if (type.compareTo(TIMESTAMP) == 0) {

		} else if (type.compareTo(PICTURE) == 0) {

		} else if (type.compareTo(COMBOBOX) == 0) {

		} else if (type.compareTo(AUDIO) == 0) {

		} else if (type.compareTo(VIDEO) == 0) {

		} else if (type.compareTo(LOCATION) == 0) {

		} else if (type.compareTo(TEMPERATURE) == 0) {

		} else if (type.compareTo(SIGNAL) == 0) {

		} else {
			// Ocorreu algum erro? Tipo invalido? Tipo nao presente nas
			// classes? Acho interessante uma excecao
			return null;
		}

		return isQuestion;
	}

	/* Metodo que retorna um array de Question */
	public Question[] getQuestions() {
		int sizeQuestions = size(); // tamanho do formulario

		if (sizeQuestions <= 0)
			return null; // Ocorreu algum erro do XML ? Formato invalido ?

		Question[] questions = new Question[sizeQuestions];

		/*
		 * Lista os filhos essenciais para retornar, onde identificamos se o
		 * mesmo sera pertencente a qual type: text,number,radiobutton etc.
		 */

		NodeList list = document.getFirstChild().getFirstChild()
				.getNextSibling().getChildNodes();

		int index = 0; // Indice do array

		for (int i = 1; i < list.getLength() - 1; i += 2) {

			Node node = list.item(i);

			/* So executa se for diferente do formid e userid */
			if (node.getNodeName().compareTo("formid") != 0
					&& node.getNodeName().compareTo("userid") != 0) {
				Integer previous = index - 1;

				/* Previous do primeiro elemento nao contem nada, null */
				if (index - 1 < 0) {
					previous = null;
				}
				/* Casting de Node para Element, para usa'-lo no construtor */
				Element element = (Element) node;

				/* Elementos basicos da question, previous acima */
				Integer id = myParseInteger(element.getAttribute("id"));
				System.out.println("Atributo: " + element.getAttribute("next"));
				Integer next = myParseInteger(element.getAttribute("next"));
				Boolean required = Boolean.parseBoolean(element
						.getAttribute("required"));

				/* Elementos internos, usa o metodo getTagValue */
				String help = getTagValue("help", element);
				String label = getTagValue("label", element);

				/* Pega a question atual */	
				//System.out.println (node.getNodeName());
				questions[index] = specificQuestion(node.getNodeName(), id,
						previous, next, help, label, required, element);

				index++; // incrementa o indice de index

				if (index == size()) {
					questions[index - 1].setNext(null);
					/* seta o next do ultimo como null */
				}

				// Apagar o print abaixo, so para testes */
				System.out.println(questions[index - 1].toString());
			} else {
				if (node.getNodeName().compareTo("formid") == 0) {
					setFormid(node.getFirstChild().getNodeValue());
				}

				if (node.getNodeName().compareTo("userid") == 0) {
					setUserid(node.getFirstChild().getNodeValue());
				}
			}

		}
		/*
		 * Printa para testar o user e form id Apagar essa parte so para fins de
		 * testes
		 */

		// System.out.println("User: " + getUserid() + "   " + "Form: "
		// + getFormid());
		return questions;

	}

	/* Metodos static */

	/* Recebe uma tag e um element retornando o conteudo em String */
	public static String getTagValue(String tag, Element element) {
		NodeList list = element.getElementsByTagName(tag).item(0)
				.getChildNodes();
		Node node = (Node) list.item(0);
		return node.getNodeValue();
	}

	/* Retorna um null caso nao seja number */
	public static Integer myParseInteger(String str) {
		try {
			Integer value = Integer.parseInt(str);
			return value;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
