package br.unifesp.maritaca.persistence.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.unifesp.maritaca.persistence.annotation.Column;
import br.unifesp.maritaca.persistence.annotation.Minimal;
import br.unifesp.maritaca.persistence.util.ConstantsPersistence;

@Entity
@Table(name="OAuthCode")
public class OAuthCode {
	
	@Id
	private UUID key;
	
	@Column(indexed = true, ttl=ConstantsPersistence.OAUTH_CODE_TIME_TO_LIVE)
	@Minimal
	private String code;
	
	@Column
	@Minimal
	private String clientId;
	
	@Column
	@Minimal
	private User user;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setUser(String key){
		User user = new User();
		user.setKey(key);
		setUser(user);
	}
	
}
