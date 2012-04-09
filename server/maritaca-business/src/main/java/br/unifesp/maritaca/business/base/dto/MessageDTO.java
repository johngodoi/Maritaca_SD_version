package br.unifesp.maritaca.business.base.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageDTO implements Serializable {
	
	public static enum MessageType { SUCCESS, WARNING, ERROR };
	private MessageType messageType;
	private List<String> messages;
	private String message;
	private Boolean success;
	
	public MessageDTO() {
		this.messages = new ArrayList<String>(0);
	}

	public Boolean getSuccess() {
		return success;
	}
	
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
	public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        if(messageType == MessageType.SUCCESS) {
            setSuccess((Boolean) true);
        }
        else {
            setSuccess((Boolean) false);
        }
        this.messageType = messageType;
    }
    
    public List<String> getMessages() {
        return messages;
    }

    public void addMessages(String message) {
        this.messages.add(message);
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}