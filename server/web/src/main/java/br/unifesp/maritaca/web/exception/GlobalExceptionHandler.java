package br.unifesp.maritaca.web.exception;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import me.prettyprint.hector.api.exceptions.HectorException;

/**
 * Class to handle all exception and create human-readable messages
 * 
 * @author emiguel
 * 
 */
public class GlobalExceptionHandler extends ExceptionHandlerWrapper {
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
			} else if (thr.getMessage().equals("null source")) {
				ViewExpiredException ex = new ViewExpiredException(
						"session closed", thr.getCause(), "");
				handleViewExpiredException(ex);
			} else if (thr instanceof HectorException) {
				addMessage("error_hector_exception",
						FacesMessage.SEVERITY_ERROR);
			} else if (thr instanceof IllegalArgumentException) {
				addMessage("error_illegal_argument",
						FacesMessage.SEVERITY_ERROR);
			} else {
				addMessage("error_unexpected", FacesMessage.SEVERITY_ERROR);
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
			getFacesContext().getExternalContext().redirect(
					getFacesContext().getExternalContext()
							.getRequestContextPath());
		} catch (IOException e) {
			e.printStackTrace();
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
