package br.unifesp.maritaca.ws.exceptions;

import br.unifesp.maritaca.ws.api.resp.ErrorResponse;

public class MaritacaWSException extends Exception {
	ErrorResponse response;
	
	public MaritacaWSException() {
	}
	
	public MaritacaWSException(ErrorResponse error) {
		response = error;
	}

	public ErrorResponse getResponse() {
		return response;
	}
	
	public void setResponse(ErrorResponse response) {
		this.response = response;
	}
}
