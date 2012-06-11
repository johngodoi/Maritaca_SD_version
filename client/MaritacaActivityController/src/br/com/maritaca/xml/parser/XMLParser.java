package br.com.maritaca.xml.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
import br.com.maritaca.questions.Location;
import br.com.maritaca.questions.Number;
import br.com.maritaca.questions.Picture;
import br.com.maritaca.questions.Question;
import br.com.maritaca.questions.RadioButton;
import br.com.maritaca.questions.Text;
import br.com.maritaca.questions.Timestamp;

public class XMLParser {

	/* Tipos de questionarios que podem ser gerados */
	public static final String TEXT = "text";
	public static final String NUMBER = "number";
	public static final String CHECKLIST = "checklist";
	public static final String COMBOBOX = "radiobutton";
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
	private InputStream file; // file, localizacao do arquivo XML
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

	public InputStream getFile() {
		return file;
	}

	public void setFile(InputStream file) {
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
	public XMLParser(InputStream is) {
		this.file = is;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(is);
			document.getDocumentElement().normalize();
			Log.v("XML", document.toString());
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

		Question question = null;

		if (type.compareTo(TEXT) == 0) {
			question = new Text(id, previous, next, help, label, required,
					element);
		} else if (type.compareTo(NUMBER) == 0) {
			question = new Number(id, previous, next, help, label, required,
					element);

		} else if (type.compareTo(COMBOBOX) == 0) {
			question = new RadioButton(id, previous, next, help, label,
					required, element);
		} else if (type.compareTo(SLIDE) == 0) {

		} else if (type.compareTo(DATE) == 0) {

		} else if (type.compareTo(TIME) == 0) {

		} else if (type.compareTo(TIMESTAMP) == 0) {
			question = new Timestamp(id, previous, next, help, label, required,
					element);
		} else if (type.compareTo(PICTURE) == 0) {
			question = new Picture(id, previous, next, help, label, required,
					element);

		} else if (type.compareTo(COMBOBOX) == 0) {

		} else if (type.compareTo(AUDIO) == 0) {

		} else if (type.compareTo(VIDEO) == 0) {

		} else if (type.compareTo(LOCATION) == 0) {
			question = new Location(id, previous, next, help, label, required,
					element);
		} else if (type.compareTo(TEMPERATURE) == 0) {

		} else if (type.compareTo(SIGNAL) == 0) {

		} else {
			// Ocorreu algum erro? Tipo invalido? Tipo nao presente nas
			// classes? Acho interessante uma excecao
			return null;
		}

		return question;
	}

	/* Metodo que retorna um array de Question */
	public Question[] getQuestions() {

		int sizeQuestions = size(); // tamanho do formulario

		if (sizeQuestions <= 0)
			return null; // Ocorreu algum erro do XML ? Formato invalido ?

		Question[] questions = new Question[sizeQuestions];
		NodeList list;
		Node teste;
		// FIXME comentado pois o formulario ainda nao possui formId e userId
		// Get FORMID
		// NodeList list = document.getElementsByTagName("formid");
		// Node teste = list.item(0);
		// Log.v("ARLINDO", "testes: + "+teste.getNodeName());
		// setFormid(teste.getFirstChild().getNodeValue());
		// Log.v("ARLINDO", "testes: + "+teste.getFirstChild().getNodeValue());

		// Get USERID
		// list = document.getElementsByTagName("userid");
		// teste = list.item(0);
		// Log.v("ARLINDO", "testes: + "+teste.getNodeName());
		// setUserid(teste.getFirstChild().getNodeValue());
		// Log.v("ARLINDO", "testes: + "+teste.getFirstChild().getNodeValue());

		list = document.getElementsByTagName("questions");
		teste = list.item(0);
		Log.v("ARLINDO", "testes: + " + teste.getNodeName());

		list = teste.getChildNodes();

		Log.v("ARLINDO", "testes: size " + list.getLength());

		for (int i = 1; i < list.getLength(); i++) {
			teste = list.item(i);
			Log.v("ARLINDO", "error: 2.3.1: + " + teste.getNodeName());
		}

		int questionIndex = 0; // Indice do array
		Log.d("Questions size", "" + list.getLength());
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);

			// Integer previous = i-1;//questionIndex - 1;
			Integer previous = questionIndex - 1;
			if (previous < 0) {
				previous = null;
			}

			/* Casting de Node para Element, para usa'-lo no construtor */
			if (node instanceof Element) {
				Element element = (Element) node;

				/* Elementos basicos da question, previous acima */
				Integer id = myParseInteger(element.getAttribute("id"));
				Integer next = myParseInteger(element.getAttribute("next")) != null ? myParseInteger(element
						.getAttribute("next"))
						: i + 1 < list.getLength() ? id + 1 : -1;
				Boolean required = Boolean.parseBoolean(element
						.getAttribute("required"));

				/* Elementos internos, usa o metodo getTagValue */
				String help = getTagValue("help", element);
				String label = getTagValue("label", element);

				Log.v("TAG", "ID: " + id + "\nNext: " + next + "\nRequeried: "
						+ required);

				/* Pega a question atual */
				// System.out.println (node.getNodeName());
				questions[questionIndex] = specificQuestion(node.getNodeName(),
						id, previous, next, help, label, required, element);

				questionIndex++; // incrementa o indice de index
			}
		}
		questions[questionIndex - 1].setNext(null);

		return questions;

	}

	/* Metodos static */

	/* Recebe uma tag e um element retornando o conteudo em String */
	public static String getTagValue(String tag, Element element) {
		NodeList nodeTag = element.getElementsByTagName(tag);
		if (nodeTag == null || nodeTag.getLength() == 0)
			return null;
		NodeList list = nodeTag.item(0).getChildNodes();
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

	/**
	 * Usado para show.
	 * 
	 * @param str
	 * @return true se str eh "yes", false se for "no"
	 */
	public static Boolean myParseBoolean(String str) {
		return str.equals("yes") ? true : false;
	}
}
