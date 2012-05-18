package br.unifesp.maritaca.business.answer.list.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QuestionAnswerDTO {

	@XmlAttribute
	private String id;
	
	@XmlElement
	private String value;
	
	public QuestionAnswerDTO() {	}
	
	public String getId() {
		return id;
	}
	
	public String getValue() {
		return value;
	}
		
}
