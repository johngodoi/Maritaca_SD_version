package br.unifesp.maritaca.ws.api.resp;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="error")
public class ErrorResponse extends MaritacaResponse{
	private String message;
	
	public ErrorResponse() {
		setStatus(Response.Status.INTERNAL_SERVER_ERROR);
		setStatus(FAIL);
	}
	
	public ErrorResponse(Throwable e) {
		setStatus(Response.Status.INTERNAL_SERVER_ERROR);
		setMessage(e.getMessage());
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
