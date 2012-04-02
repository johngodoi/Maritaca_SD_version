package br.unifesp.maritaca.core;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@XmlRootElement(name = "user")
@XmlType(propOrder = { "key", "firstname", "lastname", "email", "maritacaList" })
@Entity
public class User implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;

	@Id
	private UUID key;
	
	@Column
	@Minimal
	private String firstname;
	
	@Column
	private String lastname;
	
	@Column
	@Minimal
	private MaritacaList maritacaList;
	
	@Column(indexed=true)
	@Minimal
	private String email;
	
	@XmlTransient
	@Column
	private String password; // password is not exported to xml

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlTransient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}
	
	public void setKey(String ks){
		this.key = UUID.fromString(ks);
	}
	
	@Override
	public String toString() {
		if(getKey()!=null){
			return getKey().toString();
		}
		return super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User){
			User user = (User)obj;
			return user.getKey().equals(getKey());
		}
		return(obj.equals(getKey()));
	}
	
	@Override
	public int hashCode() {
		if(key==null){
			return super.hashCode();
		}else{
			return key.hashCode();
		}
	}

	public MaritacaList getMaritacaList() {
		return maritacaList;
	}

	public void setMaritacaList(MaritacaList maritacaList) {
		this.maritacaList = maritacaList;
	}
	
	public void setMaritacaList(String maritacaListId){
		MaritacaList maritacaList = new MaritacaList();
		maritacaList.setKey(maritacaListId);
		this.maritacaList = maritacaList;
	}
}
