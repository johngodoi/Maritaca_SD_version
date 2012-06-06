package br.unifesp.maritaca.persistence.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.unifesp.maritaca.persistence.annotation.Column;
import br.unifesp.maritaca.persistence.annotation.Minimal;

@Entity
@Table(name="Configuration")
public class Configuration {
	@Id
	private UUID key;
	@Column(indexed = true)
	@Minimal
	private String name;
	@Column
	@Minimal
	private String value;

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}
	
	public void setKey(String uid){
		setKey(UUID.fromString(uid));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
