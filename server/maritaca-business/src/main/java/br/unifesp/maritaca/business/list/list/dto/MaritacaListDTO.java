package br.unifesp.maritaca.business.list.list.dto;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.unifesp.maritaca.business.base.dto.BaseDTO;

public class MaritacaListDTO extends BaseDTO {
		
	private static final long serialVersionUID = 1L;
	
	private UUID   key;
	@NotNull
	@Size(min = 3, max = 20)
	private String name;
	private String description;	
	
	@NotNull
	private UUID owner;
	
	private List<UUID> users;
	
	public MaritacaListDTO() {
	}	

	public UUID getKey() {
		return key;
	}
	
	public void setKey(String key){
		this.key = UUID.fromString(key);
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

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}
	
	public void setOwner(String owner){
		this.owner = UUID.fromString(owner);
	}

	public List<UUID> getUsers() {
		return users;
	}

	public void setUsers(List<UUID> users) {
		this.users = users;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}