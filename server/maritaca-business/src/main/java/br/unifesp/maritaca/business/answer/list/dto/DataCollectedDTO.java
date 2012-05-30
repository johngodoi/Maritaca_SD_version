package br.unifesp.maritaca.business.answer.list.dto;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="collecteddata")
public class DataCollectedDTO {

	@XmlElement
	private String formId;
	
	@XmlElement
	private String userId;
	
	@XmlElement(name="answers")
	private AnswerListDTO answerList;
	
	public DataCollectedDTO() {	}
	
	public String getFormId() {
		return formId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public AnswerListDTO getAnswerList() {
		return answerList;
	}
	
	@Override
	public String toString(){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(DataCollectedDTO.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter(); 
			marshaller.marshal(this, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}				
	}
}
