package br.unifesp.maritaca.persistence.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.unifesp.maritaca.persistence.annotation.Column;
import br.unifesp.maritaca.persistence.annotation.Minimal;
import br.unifesp.maritaca.persistence.util.ConstantsPersistence;

@Entity
@Table(name="OAuthToken")
public class OAuthToken {
	@Id
	private UUID key;
	
	@Column(indexed=true)
	@Minimal
	private String refreshToken;
	
	@Column(indexed=true, ttl=ConstantsPersistence.OAUTH_EXPIRATION_DATE)
	@Minimal
	private String accessToken;
	
	@Column(indexed=true)
	@Minimal
	private User user;
	
	@Column
	@Minimal
	private String clientId;
	
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
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
		
	public long getExpiresIn(){
		return ConstantsPersistence.OAUTH_EXPIRATION_DATE;
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
	
}
