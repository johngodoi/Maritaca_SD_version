package br.unifesp.maritaca.business.account.edit.dto;

import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.unifesp.maritaca.business.base.MaritacaConstants;


public class UserDTO implements Cloneable{

	private UUID key;
	
	private UUID maritacaList;
	
	@Pattern(regexp = MaritacaConstants.EMAIL_REG_EXP)
	private String email;
	
	@Size(min = 3, max = 20)
	private String firstname;
	
	@Size(max = 20)
	private String lastname;
	
	private String password;	

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
		return getEmail();
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UUID getMaritacaList() {
		return maritacaList;
	}

	public void setMaritacaList(UUID maritacaListKey) {
		this.maritacaList = maritacaListKey;
	}
	
	public void setMaritacaList(String maritacaListKey) {
		this.maritacaList = UUID.fromString(maritacaListKey);
	}
	
	@Override
	public UserDTO clone(){
		try {
			UserDTO clone = (UserDTO)super.clone();
			
			UUID cloneKey = new UUID(getKey().getMostSignificantBits(),
									 getKey().getLeastSignificantBits());			
			clone.setKey(cloneKey);			
			
			UUID listKey  = new UUID(getMaritacaList().getMostSignificantBits(),
									 getMaritacaList().getLeastSignificantBits());			
			clone.setMaritacaList(listKey);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			return null;
		}				
	}
}
