package br.unifesp.maritaca.web.exception;

import java.io.IOException;
import java.util.Iterator;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import me.prettyprint.hector.api.exceptions.HectorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.exception.AuthorizationDenied;
import br.unifesp.maritaca.exception.InvalidNumberOfEntries;

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
		Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents()
				.iterator();
		while (i.hasNext()) {
			ExceptionQueuedEvent exEvt = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) exEvt
					.getSource();
			Throwable thr = getRootCause(context.getException());

			// check what type of exception to create a custom messages
			// messages are keys in messages_properties
			if (thr instanceof ViewExpiredException) {
				handleViewExpiredException((ViewExpiredException) thr);
			} else if (thr instanceof HectorException) {
				addMessage("error_hector_exception",
						FacesMessage.SEVERITY_ERROR);
				log.error("Error accessing data", thr);
			} else if (thr instanceof IllegalArgumentException) {
				addMessage("error_illegal_argument",
						FacesMessage.SEVERITY_ERROR);
				log.error("Illegal argument", thr);
			} else if(thr instanceof EvaluationException &&
					  thr.getCause() instanceof AuthorizationDenied ){
				AuthorizationDenied authDenied = (AuthorizationDenied) thr.getCause();
				addMessage("error_authorization_denied", FacesMessage.SEVERITY_ERROR);
				log.error("Authorization denied "+
							"- operation: " + authDenied.getOperation() +
							", target: "    + authDenied.getTarget()+
							", targetId: "  + authDenied.getTargetId()+
							", userId: "    + authDenied.getUserId());				
			} else if(thr instanceof InvalidNumberOfEntries ){
				InvalidNumberOfEntries invNumEnt = (InvalidNumberOfEntries) thr;
				addMessage("error_invalid_number_of_entities", FacesMessage.SEVERITY_ERROR);
				log.error("Entity: "+ invNumEnt.getEntity().getName()+
						  " have multiple entries for value: "+invNumEnt.getEntry()
						  ,thr);
			} else if(thr instanceof ELException){
				continue;//this error are handle for JSF
			}else if (thr.getMessage()!= null && thr.getMessage().equals("null source")) {
				ViewExpiredException ex = new ViewExpiredException(
						"session closed", thr.getCause(), "");
				handleViewExpiredException(ex);
			} else{
				addMessage("error_unexpected", FacesMessage.SEVERITY_ERROR);
				log.error("Error not identified", thr);
				thr.printStackTrace();
			}
			i.remove();
		}
		getWrapped().handle();
	}

	/**
	 * redirects the page to login
	 * 
	 * @param ex
	 */
	private void handleViewExpiredException(ViewExpiredException ex) {
		try {
			log.warn("View expired", ex);
			getFacesContext().getExternalContext().redirect(
					getFacesContext().getExternalContext()
							.getRequestContextPath());
		} catch (IOException e) {
			log.error("not possible to redirect to login page", e);
		}
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
