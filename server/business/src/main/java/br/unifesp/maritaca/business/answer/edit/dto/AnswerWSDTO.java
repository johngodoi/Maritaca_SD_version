package br.unifesp.maritaca.business.answer.edit.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class AnswerWSDTO {

	@XmlAttribute
	private Long timestamp;
		
	@XmlElement(name="question")
	private List<QuestionAnswerDTO> questions;
	
	public AnswerWSDTO() { }
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public List<QuestionAnswerDTO> getQuestions() {
		return questions;
	}
}
