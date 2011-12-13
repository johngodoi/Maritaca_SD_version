package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@XmlRootElement(name="form")
@Entity
public class Form {
	@Id
	private UUID key;
	
	@Column
	private String xml;
	
	@Column(indexed = true)
	@Minimal
	private User user;
	
	@Column
	@Minimal
	private String title;	

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

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@XmlTransient
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void setUser(String userId) {
		User f = new User();
		f.setKey(userId);
		setUser(f);
	}
	
	@Override
	public String toString() {
		if(getKey()!=null){
			return getKey().toString();
		}
		return super.toString();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
