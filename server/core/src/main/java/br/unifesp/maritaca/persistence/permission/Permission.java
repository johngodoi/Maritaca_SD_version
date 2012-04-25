package br.unifesp.maritaca.persistence.permission;

public class Permission {
	
	private Boolean read;
	private Boolean update;
	private Boolean delete;
	private Boolean share;
	
	public Permission(Boolean read, Boolean update, Boolean delete,
			Boolean share) {
		
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

	@Override
	public String toString() {
		return "Permission [read=" + read + ", update=" + update + ", delete="
				+ delete + ", share=" + share + "]";
	}
}