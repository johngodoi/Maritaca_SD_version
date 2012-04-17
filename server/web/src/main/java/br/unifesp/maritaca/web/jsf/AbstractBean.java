package br.unifesp.maritaca.web.jsf;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.persistence.Transient;

import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

@Deprecated
public abstract class AbstractBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	protected FormAnswerModel formAnswCtrl;
	@Transient
	protected UserModel userCtrl;

	public AbstractBean(boolean useFormAnsw, boolean useUser) {
		if (useFormAnsw) {
			formAnswCtrl = ModelFactory.getInstance().createFormResponseModel();
		}

		if (useUser) {
			userCtrl = ModelFactory.getInstance().createUserModel();
		}
	}

	/**
	 * Add a message to the FacesContext
	 * @param summary: key in messages.properties
	 * @param severity
	 */
	protected void addMessage(String summary, Severity severity) {
		FacesMessage fcMsg = new FacesMessage();
		fcMsg.setSummary(summary);
		fcMsg.setSeverity(severity);
		getFacesContext().addMessage(null, fcMsg);
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

}
