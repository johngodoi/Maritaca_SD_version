package br.unifesp.maritaca.web.jsf.form;

import java.util.Collection;

import javax.faces.bean.ManagedBean;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.FormAnswerControl;
import br.unifesp.maritaca.core.Form;

@ManagedBean
public class ListFormsBean {
	private FormAnswerControl formAnswerCtrl;
	private Collection<Form> forms;
	
	public ListFormsBean() {
		formAnswerCtrl = ControllerFactory.getInstance().createFormResponseCtrl();
	}
	
	public Collection<Form> getForms() {
		forms = formAnswerCtrl.listAllFormsMinimal();
		return forms;
	}
	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}
	
	public String viewForm(){
		return "viewForm";
	}
}
