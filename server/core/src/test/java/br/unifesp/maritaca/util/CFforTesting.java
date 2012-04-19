package br.unifesp.maritaca.util;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;
import br.unifesp.maritaca.persistence.annotations.JSONValue;

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
}