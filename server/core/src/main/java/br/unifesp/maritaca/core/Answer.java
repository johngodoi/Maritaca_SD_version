package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import br.unifesp.maritaca.access.Policy;
import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@XmlRootElement(name = "answer")
@Entity
public class Answer {
	@Id
	private UUID key;

	@Column(indexed = true)
	@Minimal
	private Form form;

	@Column(indexed = true)
	@Minimal
	private User user;

	@Column
	private String xml;
	
	@Column
	@Minimal
	private Policy policy = Policy.PRIVATE;

	@XmlElement(name = "id")
	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public void setKey(String ks) {
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

	@Override
	public String toString() {
		if (getKey() != null) {
			return getKey().toString();
		}
		return super.toString();
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
}
