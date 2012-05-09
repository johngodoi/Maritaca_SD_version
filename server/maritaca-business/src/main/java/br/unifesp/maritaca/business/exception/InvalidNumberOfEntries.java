package br.unifesp.maritaca.business.exception;

public class InvalidNumberOfEntries extends MaritacaException {

	private static final long serialVersionUID = 1L;
	private static final String LOG_ERROR = "Entity: %s have %d entries for value: %s";

	private String                   value;
	private String					 property;
	private Integer					 invalidNumber;
	private Class<? extends Object>  entity;
		

	public InvalidNumberOfEntries(Class<? extends Object>entityType, String property, 
			String value, Integer invalidNumber){
		setProperty(property);
		setValue(value);
		setEntity(entityType);
		setInvalidNumber(invalidNumber);
	}
	
	@Override
	public String getMessage() {
		return String.format(LOG_ERROR, 
				this.getEntity().getName(),
				this.getInvalidNumber(),
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

	public Integer getInvalidNumber() {
		return invalidNumber;
	}

	public void setInvalidNumber(Integer invalidNumber) {
		this.invalidNumber = invalidNumber;
	}

}
