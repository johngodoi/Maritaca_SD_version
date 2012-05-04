package br.unifesp.maritaca.web.exception;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.exception.MaritacaException;

/**
 * Class to handle all exception and create human-readable messages
 * 
 * @author emiguel
 * 
 */
public class GlobalExceptionHandler extends ExceptionHandlerWrapper {
	private static final Log log = LogFactory.getLog(GlobalExceptionHandler.class);
	ExceptionHandler wrapped;

	public GlobalExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	/**
	 * Handle all exceptions
	 */
	@Override
	public void handle() throws FacesException {
		Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents()
				.iterator();
		while (iterator.hasNext()) {
			ExceptionQueuedEvent exEvt = iterator.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) exEvt
					.getSource();
			Throwable thr = getRootCause(context.getException());

			String logMessage  = null;			
			String userMessage = null;
			
			MaritacaException maritacaException = searchMaritacaExceptionInCause(thr);
			
			if(maritacaException!=null){
				logMessage  = maritacaException.getMessage();
				userMessage = maritacaException.getUserMessage();								
			} else {
				logMessage  = thr.getMessage();
				userMessage = MaritacaException.GENERIC_MESSAGE;
			}

			showAndLogError(logMessage,userMessage);
			
			// check what type of exception to create a custom messages
			// messages are keys in messages_properties
			iterator.remove();
		}
		getWrapped().handle();
	}
	
	private MaritacaException searchMaritacaExceptionInCause(Throwable thr) {
		Throwable cause = thr.getCause();
		while(cause!=null){
			if(cause instanceof MaritacaException){
				return (MaritacaException) cause;
			}
			cause = cause.getCause();
		}
		return null;
	}

	private void showAndLogError(String logMessage, String userMessage){
		log.error(logMessage);
		addMessage(userMessage, FacesMessage.SEVERITY_ERROR);
	}

	private FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	/**
	 * creates a FacesMessage with the key for message.properties amd the
	 * severity
	 * 
	 * @param summary
	 * @param severity
	 */
	private void addMessage(String summary, Severity severity) {
		FacesMessage fcMsg = new FacesMessage();
		fcMsg.setSummary(summary);
		fcMsg.setSeverity(severity);
		getFacesContext().addMessage(null, fcMsg);
	}
}