package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.Flash;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;

import com.sun.faces.context.flash.ELFlash;

@ManagedBean
public class ViewFormBean extends AbstractBean{
	private Form form;
	
//	@ManagedProperty("#{flash}")
//	private Flash flash;

	public ViewFormBean() {
		super(true,false);
	}

	public Form getForm() {
//		if(form == null){
//			form = (Form)flash.get("form");
//		}
//		flash.keep("form");
		if (form!= null && form.getXml() == null) {
			this.form = formAnswCtrl.getForm(form.getKey());
		}
		return form;
	}

	public void setForm(Form form){
//		if(form!=null)
//			flash.put("form", form);
		this.form = form;
	}
	
	public String newAnswer(){
		return "newAnswer";
	}
	
	public String listAnswers(){
		return "listAnswers";
	}
	
//	public Flash getFlash() {
//		return flash;
//	}
//	
//	public void setFlash(Flash flash) {
//		this.flash = flash;
//	}
}
