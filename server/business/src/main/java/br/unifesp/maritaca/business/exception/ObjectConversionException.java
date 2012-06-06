package br.unifesp.maritaca.business.exception;

import java.lang.reflect.Field;

public class ObjectConversionException extends MaritacaException {

	private static final long   serialVersionUID = 1L;

	private static final String LOG_ERROR = "Error converting class %s to class %s on field %s";
	
	private Class<?> objClass;
	private Class<?> targetClass;
	private Field field;
	
	public ObjectConversionException(Class<?> objClass,	Class<?> targetClass, Field field) {
		this.setObjClass(objClass);
		this.setTargetClass(targetClass);
		this.setField(field);
	}
	
	@Override
	public String getMessage() {
		return String.format(LOG_ERROR, 
				this.getObjClass(),
				this.getTargetClass(),
				this.getField());
	}

	public Class<?> getObjClass() {
		return objClass;
	}

	public void setObjClass(Class<?> objClass) {
		this.objClass = objClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}		
}
