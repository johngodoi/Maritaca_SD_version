package br.unifesp.maritaca.persistence.entity;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.unifesp.maritaca.persistence.annotation.Column;
import br.unifesp.maritaca.persistence.annotation.JSONValue;
import br.unifesp.maritaca.persistence.annotation.Minimal;

@Entity
@Table(name="MaritacaList")
public class MaritacaList {
	@Id
	private UUID key;
	@Column(indexed = true)
	@Minimal
	private User owner;

	@Column(indexed = true)
	@Minimal
	private String name;

	@Column
	@Minimal
	private String description;
	
	@Column
	@JSONValue
	private List<UUID> users;

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public void setKey(String ks) {
		if (ks == "")
			this.key = null;
		this.key = UUID.fromString(ks);
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setOwner(String uid) {
		User owner = new User();
		owner.setKey(uid);
		setOwner(owner);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if (getKey() != null) {
			return getKey().toString();
		}
		return super.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MaritacaList){
			MaritacaList grp = (MaritacaList)obj;
			return grp.getKey().equals(getKey());
		} else {
			return false;
		}
	}

	public List<UUID> getUsers() {
		return users;
	}

	public void setUsers(List<UUID> users) {
		this.users = users;
	}
}
