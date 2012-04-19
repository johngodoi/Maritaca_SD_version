package br.unifesp.maritaca.core;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.JSONValue;

@Entity
public class FormAccesibleByUser {
	
	@Id
	private UUID key;
	
	@Column(indexed=true)
	private UUID user;
	
	@Column
	@JSONValue
	private List<UUID> forms;

	
	public List<UUID> getForms() {
		return forms;
	}

	public void setForms(List<UUID> forms) {
		this.forms = forms;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public UUID getUser() {
		return user;
	}

	public void setUser(UUID user) {
		this.user = user;
	}
}