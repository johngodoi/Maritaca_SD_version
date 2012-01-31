package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@Entity
public class GroupUser {
	@Id
	private UUID key;
	@Column(indexed = true)
	@Minimal
	private Group group;
	@Column(indexed = true)
	@Minimal
	private User user;

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public void setKey(String ks) {
		this.key = UUID.fromString(ks);
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setGroup(String uid) {
		Group group = new Group();
		group.setKey(uid);
		setGroup(group);
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
