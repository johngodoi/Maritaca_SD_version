package br.unifesp.maritaca.business.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class MaritacaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static String GENERIC_MESSAGE = "exception_internal_server_error";
	
	private String userMessage;
	
	public MaritacaException(){
		setUserMessage(GENERIC_MESSAGE);
	}
	
	public MaritacaException(String msg){
		super(msg);
		setUserMessage(GENERIC_MESSAGE);
	}
	
	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
	
}
