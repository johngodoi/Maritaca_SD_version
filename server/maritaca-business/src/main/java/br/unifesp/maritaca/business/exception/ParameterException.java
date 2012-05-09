package br.unifesp.maritaca.business.exception;

public class ParameterException extends MaritacaException {

	private static final long serialVersionUID = 1L;

	private Class<?> parameterClass;
	private Object   value;
	
	public ParameterException(Class<?> parameterClass) {
		setParameterClass(parameterClass);
	}
	
	public ParameterException(String value) {
		setValue(value);
	}
	
	public ParameterException(Object value, Class<?> parameterClass) {
		setParameterClass(parameterClass);
		setValue(value);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getParameterClass() {
		return parameterClass;
	}

	public void setParameterClass(Class<?> parameterClass) {
		this.parameterClass = parameterClass;
	}

}
