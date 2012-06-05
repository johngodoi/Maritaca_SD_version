package br.unifesp.maritaca.mobile.model;

import java.util.Date;

public abstract class Clause {
	
	protected String value;
	protected Integer goToIndex;
	
	public Clause(String value, Integer goToIndex) {
		this.value = value;
		this.goToIndex = goToIndex;
	}

	public abstract boolean evaluate(String value);
	
	public abstract boolean evaluate(Integer value);
	
	public abstract boolean evaluate(Float value);
	
	public abstract boolean evaluate(Date value);
	
	public Integer getGoToIndex() {
		return goToIndex;
	}
		
	public String getValue() {
		return value;
	}
}