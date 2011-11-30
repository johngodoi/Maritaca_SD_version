package br.unifesp.maritaca.web.jsf.form;

import java.util.Collection;

import javax.faces.bean.ManagedBean;

import com.sun.faces.context.flash.ELFlash;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class ListFormsBean extends AbstractBean {
	private Collection<Form> forms;
	
	public ListFormsBean() {
		super(true, false);
	}
	
	public Collection<Form> getForms() {
		forms = formAnswCtrl.listAllFormsMinimal();
		return forms;
	}
	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}
	
	public String viewForm(){
		return "viewForm";
	}
}
