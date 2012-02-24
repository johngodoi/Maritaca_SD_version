package br.unifesp.maritaca.core;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

@XmlRootElement
@Entity
public class OAuthToken {
	@Id
	private String refreshToken;
	@Column(indexed=true, ttl=3600)
	@Minimal
	private String accessToken;
	@Column(indexed=true)
	@Minimal
	private User user;
	@Column(indexed=true, ttl=3600)
	@Minimal
	private Date expirationDate;
	
	@XmlElement(name="refresh_token")
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	@XmlElement(name="access_token")
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	@XmlTransient
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
	
	@XmlTransient
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	@XmlElement(name="expires_in")
	public long getExpiresIn(){
		return 3600;//temporal
	}
	
}
