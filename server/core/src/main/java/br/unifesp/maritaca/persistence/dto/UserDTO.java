package br.unifesp.maritaca.persistence.dto;

import java.util.UUID;

public class UserDTO {
	
	private UUID key;
	private String email;

	public UUID getKey() {
		return key;
	}
	
	public void setKey(UUID key) {
		this.key = key;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "MaritacaUserDTO [key=" + key + ", email=" + email + "]";
	}
}
