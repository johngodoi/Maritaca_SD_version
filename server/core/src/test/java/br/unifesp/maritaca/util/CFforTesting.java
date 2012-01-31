package br.unifesp.maritaca.util;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@Entity
public class CFforTesting{
	@Id
	private UUID key;
	@Column(indexed=true)
	@Minimal
	private String name;
	@Column
	private String bigData; 
	
	public UUID getKey() {
		return key;
	}
	public void setKey(UUID key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBigData() {
		return bigData;
	}
	
	public void setBigData(String bigData) {
		this.bigData = bigData;
	}
	
	
}