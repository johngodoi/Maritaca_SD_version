package br.unifesp.maritaca.persistence.entity;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.unifesp.maritaca.persistence.annotation.Column;
import br.unifesp.maritaca.persistence.annotation.JSONValue;

@Entity
@Table(name="FormAccessibleByList")
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