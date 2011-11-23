package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="answer")
public class Answer {
	private UUID key;
	private Form form;
	private User user;
	private String xml;

	@XmlElement(name="id")
	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}
	
	public void setKey(String ks){
		this.key = UUID.fromString(ks);
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml() {
		return xml;
	}

	public void setForm(String formId) {
		Form f = new Form();
		f.setKey(formId);
		setForm(f);
	}
	
	public void setUser(String userId) {
		User f = new User();
		f.setKey(userId);
		setUser(f);
	}
}
