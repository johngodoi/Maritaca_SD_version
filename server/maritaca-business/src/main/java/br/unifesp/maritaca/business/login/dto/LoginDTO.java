package br.unifesp.maritaca.business.login.dto;

import br.unifesp.maritaca.business.base.dto.BaseDTO;
import br.unifesp.maritaca.business.base.dto.MessageDTO;

public class LoginDTO extends BaseDTO {
	
	private static final long serialVersionUID = 1L;
	
	private String email;
	private String password;
	private MessageDTO message;
	
	public LoginDTO() {
		super();
	}

	public LoginDTO(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public LoginDTO(String email, String password, MessageDTO message) {
		super();
		this.email = email;
		this.password = password;
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}