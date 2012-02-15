package br.unifesp.maritaca.web.jsf;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import br.unifesp.maritaca.web.utils.MaritacaMessage;

/**
 * This is a Backing bean that processes all messages in FacesContext to be
 * presented to the user It accepts 4 types of messages: error, info, warn and
 * default.
 * 
 * @author emiguel
 * 
 */
@ManagedBean
public class MaritacaMessages {
	public static final String ERROR = "error";
	public static final String INFO = "info";
	public static final String WARNING = "warn";
	private ArrayList<MaritacaMessage> messages;

	public MaritacaMessages() {
		this.messages = new ArrayList<MaritacaMessage>(0);
	}

	public void addMessage(MaritacaMessage message) {
		messages.add(message);
	}

	/**
	 * ] Reads the messages from the FacesContext and converts to
	 * MaritacaMessage to be presented
	 * 
	 * @return
	 */
	public ArrayList<MaritacaMessage> getMessages() {
		FacesContext fc = FacesContext.getCurrentInstance();
		for (FacesMessage fcmsg : fc.getMessageList()) {
			if (!fcmsg.isRendered()) {
				MaritacaMessage mtcmsg = new MaritacaMessage();
				mtcmsg.setMessage(fcmsg.getSummary());
				if (fcmsg.getSeverity().equals(FacesMessage.SEVERITY_ERROR)) {
					mtcmsg.setType(ERROR);
				} else if (fcmsg.getSeverity().equals(
						FacesMessage.SEVERITY_WARN)) {
					mtcmsg.setType(WARNING);
				} else if (fcmsg.getSeverity().equals(
						FacesMessage.SEVERITY_INFO)) {
					mtcmsg.setType(INFO);
				} else {
					mtcmsg.setType("default");
				}
				addMessage(mtcmsg);
			}
		}
		return messages;
	}
}
