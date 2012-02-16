package br.unifesp.maritaca.exception;

public class InvalidNumberOfEntries extends RuntimeException {

	private String                   entry;
	private Class<? extends Object>  entity;
		
	private static final long serialVersionUID = 1L;

	public InvalidNumberOfEntries(String entity, Class<? extends Object>entityType){
		setEntry(entity);
		setEntity(entityType);
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public Class<? extends Object> getEntity() {
		return entity;
	}

	public void setEntity(Class<? extends Object> entity) {
		this.entity = entity;
	}

}
