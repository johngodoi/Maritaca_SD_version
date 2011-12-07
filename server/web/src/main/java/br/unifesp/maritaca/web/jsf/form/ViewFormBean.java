package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class ViewFormBean extends AbstractBean{
	private Form form;


	public ViewFormBean() {
		super(true,false);
	}

	public Form getForm() {
		if (form!= null && form.getXml() == null) {
			this.form = formAnswCtrl.getForm(form.getKey());
		}
		return form;
	}

	public void setForm(Form form){
		this.form = form;
	}
	
	public String newAnswer(){
		return "newAnswer";
	}
	
	public String listAnswers(){
		return "listAnswers";
	}
	
}
