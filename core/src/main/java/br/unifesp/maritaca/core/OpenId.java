package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.unifesp.maritaca.persistence.annotations.Column;
import br.unifesp.maritaca.persistence.annotations.Minimal;

/**
 * Class used to represent the openids of the users.
 *  
 * @author tiagobarabasz
 */

@Entity
public class OpenId {	
	@Id
	private UUID key;
	
	@Column(indexed = true)
	@Minimal
	private User user;

	@Column(indexed=true)
	@Minimal
	private String openIdUrl;

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getOpenIdUrl() {
		return openIdUrl;
	}

	public void setOpenIdUrl(String openIdUrl) {
		this.openIdUrl = openIdUrl;
	}
}
