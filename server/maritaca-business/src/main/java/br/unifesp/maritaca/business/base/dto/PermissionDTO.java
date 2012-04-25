package br.unifesp.maritaca.business.base.dto;

import java.io.Serializable;

public class PermissionDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Boolean read;
	private Boolean update;	
	private Boolean delete;
	private Boolean share;
	
	public PermissionDTO() {
	}
	
	public PermissionDTO(Boolean read, Boolean update, Boolean delete,
			Boolean share) {
		super();
		this.read = read;
		this.update = update;
		this.delete = delete;
		this.share = share;
	}

	public Boolean getRead() {
		return read;
	}
	public void setRead(Boolean read) {
		this.read = read;
	}
	public Boolean getUpdate() {
		return update;
	}
	public void setUpdate(Boolean update) {
		this.update = update;
	}
	public Boolean getDelete() {
		return delete;
	}
	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
	public Boolean getShare() {
		return share;
	}
	public void setShare(Boolean share) {
		this.share = share;
	}
}