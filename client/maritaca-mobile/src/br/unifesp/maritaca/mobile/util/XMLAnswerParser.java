package br.unifesp.maritaca.mobile.util;

import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import br.unifesp.maritaca.mobile.model.Question;

import android.util.Xml;

public class XMLAnswerParser {
	
	private XmlPullParser pullParser;
    private XmlSerializer serializer;
    private String formId;
    private String userId;
    private Question[] questions;
    
    private XMLAnswerParser(XmlPullParser pullParser, XmlSerializer serializer, String formId, String userId, Question[] questions) {
        this.pullParser = pullParser;
        this.serializer = serializer;
        this.setFormId(formId);
        this.setUserId(userId);
        this.questions	 = questions;
    }
	
	public static String  updateContentFile(BufferedReader reader, String formId, String userId, Question[] questions) {
		try {
			Writer writer = new StringWriter();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser pullParser = factory.newPullParser();
            pullParser.setInput(reader);
            XmlSerializer serializer = factory.newSerializer();
            serializer.setOutput(writer);
            (new XMLAnswerParser(pullParser, serializer, formId, userId, questions)).updateContentFile();
            return writer.toString();
        }
        catch (Exception ex) {
            //ex
        }
		return "";
	}
	
	private void updateContentFile() {
		try {
            pullParser.nextToken();
            updateContentFile(XmlPullParser.START_DOCUMENT);
            while(pullParser.getEventType () != XmlPullParser.END_DOCUMENT) {
            	updateContentFile (pullParser.getEventType());
                pullParser.nextToken ();
            }
            updateContentFile(XmlPullParser.END_DOCUMENT);
        } catch (Exception ex) {
            //ex
        }
	}
	
	private void updateContentFile(int eventType) {
		try {
			switch (eventType) {
		        case XmlPullParser.START_DOCUMENT:
		            serializer.startDocument("UTF-8", true);
		            break;
		            
		        case XmlPullParser.END_DOCUMENT:
		            serializer.endDocument();
		            break;
		            
		        case XmlPullParser.START_TAG:
		        	addAnswer();
		            break;
		            
		        case XmlPullParser.END_TAG:
		            serializer.endTag(pullParser.getNamespace(), pullParser.getName());
		            break;
		            
		        case XmlPullParser.IGNORABLE_WHITESPACE:
		            String s = pullParser.getText();
		            serializer.ignorableWhitespace(s);
		            break;
		            
		        case XmlPullParser.TEXT:
		            serializer.text (pullParser.getText());
		            break;
		            
		        case XmlPullParser.ENTITY_REF:
		            serializer.entityRef(pullParser.getName());
		            break;
		            
		        case XmlPullParser.CDSECT:
		            serializer.cdsect(pullParser.getText());
		            break;
		            
		        case XmlPullParser.PROCESSING_INSTRUCTION:
		            serializer.processingInstruction(pullParser.getText());
		            break;
		            
		        case XmlPullParser.COMMENT:
		            serializer.comment(pullParser.getText());
		            break;
		            
		        case XmlPullParser.DOCDECL:
		            serializer.docdecl(pullParser.getText());
		            break;
		    }
		}
		catch(Exception ex) {
			//ex
		}
	}
	
	private void addAnswer() {
		try {
			if (!pullParser.getFeature (XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES)) {
		        for (int i = pullParser.getNamespaceCount (pullParser.getDepth ()-1);
		                 i <= pullParser.getNamespaceCount (pullParser.getDepth ())-1; i++) {
		            serializer.setPrefix
		                (pullParser.getNamespacePrefix (i),
		                		pullParser.getNamespaceUri (i));
		        }
		    }
		    serializer.startTag(pullParser.getNamespace (), pullParser.getName ());       
		    
		    for (int i = 0; i < pullParser.getAttributeCount (); i++) {
		        serializer.attribute
		            (pullParser.getAttributeNamespace (i),
		            		pullParser.getAttributeName (i),
		             pullParser.getAttributeValue (i));		    
			}
		    //
		    if(pullParser.getName().equals(Constants.ANSWER_ANSWERS)) {
				serializer.startTag("", Constants.ANSWER_ANSWER);
				
				serializer.attribute("", Constants.XML_TIMESTAMP, ""+System.currentTimeMillis());
				for(Question q : questions) {
					serializer.startTag("", Constants.ANSWER_QUESTION);
					serializer.attribute("", Constants.XML_ID, q.getId().toString());
						serializer.startTag("", Constants.XML_VALUE);
						serializer.text(q.getValue().toString());
						serializer.endTag("", Constants.XML_VALUE);
						serializer.endTag("", Constants.ANSWER_QUESTION);
				}
				serializer.endTag("", Constants.ANSWER_ANSWER);
			}    	
    	} catch (Exception e) {
			//ex
		}
    }

	public static String buildContentFile(String formId, String userId, Question[] questions) {
		try {
			XmlSerializer serializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", Constants.ANSWER_RESPONSE);
				serializer.startTag("", Constants.ANSWER_FORMID);
				serializer.text(formId);
				serializer.endTag("", Constants.ANSWER_FORMID);
				
				serializer.startTag("", Constants.ANSWER_USERID);
				serializer.text(userId);
				serializer.endTag("", Constants.ANSWER_USERID);
				
				serializer.startTag("", Constants.ANSWER_ANSWERS);
					//
					serializer.startTag("", Constants.ANSWER_ANSWER);
					serializer.attribute("", Constants.XML_TIMESTAMP, ""+System.currentTimeMillis());
					for(Question q : questions) {
						serializer.startTag("", Constants.ANSWER_QUESTION);
						serializer.attribute("", Constants.XML_ID, q.getId().toString());
							serializer.startTag("", Constants.XML_VALUE);
							serializer.text(q.getValue().toString());
							serializer.endTag("", Constants.XML_VALUE);
						serializer.endTag("", Constants.ANSWER_QUESTION);
					}
					serializer.endTag("", Constants.ANSWER_ANSWER);
					//
				serializer.endTag("", Constants.ANSWER_ANSWERS);
				
			serializer.endTag("", Constants.ANSWER_RESPONSE);
			serializer.endDocument();
			return writer.toString();
		}
		catch(Exception ex) {
			//error
		}
		return null;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}