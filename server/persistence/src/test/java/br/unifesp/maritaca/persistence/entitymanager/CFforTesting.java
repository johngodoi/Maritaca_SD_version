package br.unifesp.maritaca.persistence.entitymanager;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotation.Column;
import br.unifesp.maritaca.persistence.annotation.JSONValue;
import br.unifesp.maritaca.persistence.annotation.Minimal;

@Entity
public class CFforTesting{
	
	@Id
	private UUID key;
	
	@Column(indexed=true)
	@Minimal
	private String name;
	
	@Column
	private String bigData;

	@Column
	@JSONValue
	private List<UUID> list;
	
	@Column(multi=true)
	private List<String> multiColumnList;
	
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
	public List<UUID> getList() {
		return list;
	}
	public void setList(List<UUID> list) {
		this.list = list;
	}
	public List<String> getMultiColumnList() {
		return multiColumnList;
	}
	public void setMultiColumnList(List<String> multiColumnList) {
		this.multiColumnList = multiColumnList;
	}
}