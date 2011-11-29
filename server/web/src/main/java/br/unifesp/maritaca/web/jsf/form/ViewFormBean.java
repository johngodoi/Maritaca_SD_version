package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.FormAnswerControl;
import br.unifesp.maritaca.core.Form;

@ManagedBean
@RequestScoped
public class ViewFormBean {
	Form form;

	private FormAnswerControl formAnswerCtrl;

	public ViewFormBean() {
		formAnswerCtrl = ControllerFactory.getInstance()
				.createFormResponseCtrl();
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		if (form.getXml() == null) {
			this.form = formAnswerCtrl.getForm(form.getKey());
		} else {
			this.form = form;
		}
	}
}
