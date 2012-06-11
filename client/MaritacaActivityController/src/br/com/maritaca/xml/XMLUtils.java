package br.com.maritaca.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;
import br.com.maritaca.questions.Model;

public class XMLUtils {

	public static String parseXMLResponse(String xmlResponse) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();

			Document doc = db.parse(new ByteArrayInputStream(xmlResponse
					.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();

			NodeList nl = doc.getElementsByTagName("xml");
			return getElementValue(nl.item(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public static String getXmlAnswerAsString(String formId, String userId,
			Model model) throws IllegalArgumentException,
			IllegalStateException, IOException {
		XmlSerializer xmlSerializer = Xml.newSerializer();
		StringWriter stringWriter = new StringWriter();
		xmlSerializer.setOutput(stringWriter);
		xmlSerializer.startDocument("UTF-8", true);
		xmlSerializer.startTag("", "datacollected");

		xmlSerializer.startTag("", "formId");
		xmlSerializer.text("testeFormId");
		xmlSerializer.endTag("", "formId");

		xmlSerializer.startTag("", "userId");
		xmlSerializer.text("testeUserId");
		xmlSerializer.endTag("", "userId");

		xmlSerializer.startTag("", "answers");
		xmlSerializer.startTag("", "answer");
		xmlSerializer.attribute("", "timestamp", "" + new Date().getTime());
		for (int i = 0; i < model.getSize(); i++) {
			xmlSerializer.startTag("", "question");

			xmlSerializer.attribute("", "id", model.getQuestionIndex(i).getId()
					.toString());
			xmlSerializer.startTag("", "value");
			xmlSerializer
					.text(model.getQuestionIndex(i).getValue() == null ? ""
							: model.getQuestionIndex(i).getValue().toString());
			xmlSerializer.endTag("", "value");
			xmlSerializer.endTag("", "question");

		}
		xmlSerializer.endTag("", "answer");
		xmlSerializer.endTag("", "answers");
		xmlSerializer.endTag("", "datacollected");

		xmlSerializer.endDocument();
		return stringWriter.toString();
	}

}
