package br.unifesp.maritaca.web.utils;

/**
 * Message to be presented to the user
 * message is a key from message.properties
 * type: can be error, warn, info or default
 * @author emiguel
 *
 */
public class MaritacaMessage {
	private String type;
	private String message;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
