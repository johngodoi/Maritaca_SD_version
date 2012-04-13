package br.unifesp.maritaca.persistence.dto;

import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class UserDTO {

	private UUID key;
	
	private UUID maritacaListKey;
	
	//TODO The email reg exp should be read from MaritacaConstants
	//TODO This can be done when UserDTO goes back to maritaca-business
	@Pattern(regexp = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$")
	private String email;
	
	@Size(min = 3, max = 20)
	private String firstname;
	
	@Size(max = 20)
	private String lastname;
	
	private String encryptedPassword;	

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

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public UUID getMaritacaListKey() {
		return maritacaListKey;
	}

	public void setMaritacaListKey(UUID maritacaListKey) {
		this.maritacaListKey = maritacaListKey;
	}
}
