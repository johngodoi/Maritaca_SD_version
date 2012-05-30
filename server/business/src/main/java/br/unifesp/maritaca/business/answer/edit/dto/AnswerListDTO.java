package br.unifesp.maritaca.business.answer.edit.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class AnswerListDTO {

	@XmlElement(name="answer")
	private List<AnswerWSDTO> answers;
	
	public AnswerListDTO() { }
	
	public List<AnswerWSDTO> getAnswers() {
		return answers;
	}
	
}
