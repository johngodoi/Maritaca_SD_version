package br.unifesp.maritaca.core;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.JSONValue;

@Entity
public class FormAccessibleByList {
	
	@Id
	private UUID key;
	
	@Column(indexed=true)
	private UUID maritacaList;
	
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

	public UUID getMaritacaList() {
		return maritacaList;
	}

	public void setMaritacaList(UUID maritacaList) {
		this.maritacaList = maritacaList;
	}
}