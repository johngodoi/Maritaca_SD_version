package br.unifesp.maritaca.business.oauth;

import java.util.UUID;

public class OAuthClientDTO {

	private UUID key;
	
	private String clientId;
	
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