package br.unifesp.maritaca.ws.api.resp;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Response of Maritaca, mainly used in RESTful
 * A MaritacaResponse wrappers the response on the system
 * to be exported as XML or JSON, even if the response
 * is an Exception
 * @author emigueltriana
 * 
 */
public abstract class MaritacaResponse {
	public static final String FORM_TYPE = "form";
	public static final String RESPONSE_TYPE = "answer";
	public static final String FAIL = "FAIL";

	private String status;
	private int code;

	@XmlAttribute
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatus(Status status) {
		setStatus(status.getReasonPhrase());
		setCode(status.getStatusCode());
	}

	@XmlAttribute
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
