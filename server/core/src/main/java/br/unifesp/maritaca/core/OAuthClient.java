package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@Entity
public class OAuthClient {
	@Id
	private UUID key;

	@Column(indexed = true)
	@Minimal
	private String clientId;
	
	@Column
	private String secret;

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
	
	public String getSecret() {
		return secret;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
}
