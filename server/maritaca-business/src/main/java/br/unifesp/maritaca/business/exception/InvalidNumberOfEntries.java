package br.unifesp.maritaca.business.exception;

public class InvalidNumberOfEntries extends RuntimeException {

	private String                   value;
	private Class<? extends Object>  entity;
		
	private static final long serialVersionUID = 1L;

	public InvalidNumberOfEntries(String value, Class<? extends Object>entityType){
		setValue(value);
		setEntity(entityType);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Class<? extends Object> getEntity() {
		return entity;
	}

	public void setEntity(Class<? extends Object> entity) {
		this.entity = entity;
	}

}
