package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@SessionScoped
public class ViewFormBean extends AbstractBean{
	Form form;

	public ViewFormBean() {
		super(true,false);
	}

	public Form getForm() {
		if (form.getXml() == null) {
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
