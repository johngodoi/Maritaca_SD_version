package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@Entity
public class MaritacaListUser {
	@Id
	private UUID key;
	@Column(indexed = true)
	@Minimal
	private MaritacaList maritacaList;
	@Column(indexed = true)
	@Minimal
	private User user;

	public MaritacaListUser(){
		
	}
	
	public MaritacaListUser(MaritacaList list, User user){
		setMaritacaList(list);
		setUser(user);
	}
	
	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public void setKey(String ks) {
		this.key = UUID.fromString(ks);
	}

	public MaritacaList getMaritacaList() {
		return maritacaList;
	}

	public void setMaritacaList(MaritacaList maritacaList) {
		this.maritacaList = maritacaList;
	}

	public void setMaritacaList(String uid) {
		MaritacaList list = new MaritacaList();
		list.setKey(uid);
		setMaritacaList(list);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUser(String uid) {
		User user = new User();
		user.setKey(uid);
		setUser(user);
	}

	@Override
	public String toString() {
		if (getKey() != null) {
			return getKey().toString();
		}
		return super.toString();
	}
}
