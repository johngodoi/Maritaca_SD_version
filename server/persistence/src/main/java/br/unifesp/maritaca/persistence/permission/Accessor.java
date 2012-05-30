package br.unifesp.maritaca.persistence.permission;

public enum Accessor {
	OWNER(0),
	LIST(1),
	ALL(2);
	
	private Integer idAccessor;
	
	private Accessor (Integer idAccessor) {
		this.idAccessor = idAccessor;
	}

	public Integer getId() {
		return idAccessor;
	}

	public void setIdAccessor(Integer idAccessor) {
		this.idAccessor = idAccessor;
	}
}