package br.unifesp.maritaca.business.exception;

public class InvalidNumberOfEntries extends MaritacaException {

	private static final long serialVersionUID = 1L;
	private static final String LOG_ERROR = "Entity: %s have multiple entries for value: %s";

	private String                   value;
	private String					 property;
	private Class<? extends Object>  entity;
		

	public InvalidNumberOfEntries(Class<? extends Object>entityType, String property, String value){
		setProperty(property);
		setValue(value);
		setEntity(entityType);
	}
	
	@Override
	public String getMessage() {
		return String.format(LOG_ERROR, 
				this.getEntity().getName(), 
				this.getValue());
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

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
