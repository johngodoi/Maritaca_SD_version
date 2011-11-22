package br.unifesp.maritaca.ws.api.resp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="error")
public class ErrorResponse extends MaritacaResponse{
	private String message;
	
	public ErrorResponse() {
		setStatus(MaritacaResponse.FAIL);
		setCode(MaritacaResponse.SYSTEM_ERROR);
	}
	
	public ErrorResponse(Throwable e) {
		setStatus(MaritacaResponse.FAIL);
		setCode(MaritacaResponse.SYSTEM_ERROR);
		setMessage(e.getMessage());
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
