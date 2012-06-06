package br.unifesp.maritaca.business.oauth.dto;

public class OAuthTokenDTO {
	
	private String accessToken;
	
	private String refreshToken;
	
	private String user;
	
	private Long expirationDate;
	
	private String clientId;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Long expirationDate) {
		this.expirationDate = expirationDate;
	}

}
