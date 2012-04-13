package br.unifesp.maritaca.business.list.dto;

import java.util.UUID;

import br.unifesp.maritaca.business.base.dto.BaseDTO;

public class MaritacaListDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;
	
	private UUID key;
	private String name;
	private String description;
	private String owner;
	
	public MaritacaListDTO() {
	}

	public MaritacaListDTO(UUID key, String name, String description, String owner) {
		this.key = key;
		this.name = name;
		this.description = description;
		this.owner = owner;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}