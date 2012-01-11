package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.util.AccessLevel;

@Entity
public class FormAccess {
	@Id
	private UUID key;
	@Column(indexed=true)
	private FormShare formShare;
	@Column(indexed=true)
	private User user;
	@Column
	private AccessLevel accessLevel;
	public UUID getKey() {
		return key;
	}
	public void setKey(UUID key) {
		this.key = key;
	}
	public FormShare getFormShare() {
		return formShare;
	}
	public void setFormShare(FormShare formShare) {
		this.formShare = formShare;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public AccessLevel getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}
	
	public void setKey(String ks) {
		this.key = UUID.fromString(ks);
	}
	
	@Override
	public String toString() {
		if (getKey() != null) {
			return getKey().toString();
		}
		return super.toString();
	}
	
}
