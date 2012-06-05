package br.unifesp.maritaca.persistence.permission;


public class Permission {
	
	private Boolean read;
	private Boolean update;
	private Boolean delete;
	private Boolean share;
	
	public Permission(Operation... operations) {
		this.read   = false;
		this.update = false;
		this.delete = false;
		this.share  = false;
		for(Operation op : operations) {
			if(op == Operation.READ) 			{ this.read = true; }
			else if(op == Operation.UPDATE) 	{ this.update = true; }
			else if(op == Operation.DELETE) 	{ this.delete = true; }
			else if(op == Operation.SHARE) 		{ this.share = true; }			
		}
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