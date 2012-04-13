package br.unifesp.maritaca.business.list.dto;

import java.util.List;

import javax.validation.constraints.Size;

import br.unifesp.maritaca.business.base.dto.BaseDTO;
import br.unifesp.maritaca.persistence.dto.UserDTO;

public class ListDTO extends BaseDTO {
	
	private static final long serialVersionUID = 1L;

	@Size(min = 4, max = 20)
	private String name;
	
	private String description;
	
	private String searchUser;
	
	private List<UserDTO> lstUsers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSearchUser() {
		return searchUser;
	}

	public void setSearchUser(String searchUser) {
		this.searchUser = searchUser;
	}

	public List<UserDTO> getLstUsers() {
		return lstUsers;
	}

	public void setLstUsers(List<UserDTO> lstUsers) {
		this.lstUsers = lstUsers;
	}
}