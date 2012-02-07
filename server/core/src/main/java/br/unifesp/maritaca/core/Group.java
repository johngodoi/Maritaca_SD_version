package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@Entity
public class Group {
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
	private Boolean allowUsersToJoin;

	@Column
	@Minimal
	private String description;

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

	public Boolean getAllowUsersToJoin() {
		return allowUsersToJoin;
	}

	public void setAllowUsersToJoin(Boolean allowUsersToJoin) {
		this.allowUsersToJoin = allowUsersToJoin;
	}
}
