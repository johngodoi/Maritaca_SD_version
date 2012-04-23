package br.unifesp.maritaca.business.form.list.dto;

import java.util.UUID;

import br.unifesp.maritaca.business.base.dto.BaseDTO;
import br.unifesp.maritaca.business.base.dto.PermissionDTO;

public class FormListerDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;
	
	private UUID key;
	private String title;
	private String owner; //email 
	private String creationDate;
	private String policy;
	private PermissionDTO permission;
	
	public FormListerDTO() {
	}	

	public FormListerDTO(UUID key, String title, String owner,
			String creationDate, String policy, PermissionDTO permission) {
		super();
		this.key = key;
		this.title = title;
		this.owner = owner;
		this.creationDate = creationDate;
		this.policy = policy;
		this.permission = permission;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public PermissionDTO getPermission() {
		return permission;
	}

	public void setPermission(PermissionDTO permission) {
		this.permission = permission;
	}
}