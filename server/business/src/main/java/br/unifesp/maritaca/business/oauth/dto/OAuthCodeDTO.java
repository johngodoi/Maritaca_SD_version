package br.unifesp.maritaca.business.oauth.dto;

import java.util.UUID;

public class OAuthCodeDTO {
	
	private UUID key;
	
	private String code;
	
	private String clientId;
	
	private String user;

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
