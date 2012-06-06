package br.unifesp.maritaca.business.form.dto;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="form")
public class FormWSDTO {

	private UUID key;
	
	private String title;
	
	private String url;
	
	private List<Object> questions;

	@XmlElement(name = "id")
	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Object> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Object> questions) {
		this.questions = questions;
	}
	
}
