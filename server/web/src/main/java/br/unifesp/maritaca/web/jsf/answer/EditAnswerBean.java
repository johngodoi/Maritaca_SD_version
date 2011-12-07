package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.Flash;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class EditAnswerBean extends AbstractBean {
	private User user;
	private Form form;
	private String xml;
	private Answer answer;
	private boolean newAnswer;

//	@ManagedProperty("#{flash}")
//	private Flash flash;
	
//	public Flash getFlash() {
//		return flash;
//	}
//	
//	public void setFlash(Flash flash) {
//		this.flash = flash;
//	}

	public EditAnswerBean() {
		super(true, false);
		answer = new Answer();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Form getForm() {
//		if(form == null){
//			form = (Form)flash.get("form");
//		}
//		flash.keep("form");
		if (form!= null) {
			this.form = formAnswCtrl.getForm(form.getKey());
		}
		return form;
	}

	public void setForm(Form form) {
//		if(form!=null)
//			flash.put("form", form);
		this.form = form;
	}

	public void setNewAnswer(boolean newAnswer) {
		this.newAnswer = newAnswer;
	}

	public boolean isNewAnswer() {
		return newAnswer;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public String saveAnswer() {
		if (xml != null && xml.length() > 0) {
			answer.setUser(user);
			answer.setXml(xml);
			answer.setForm(getForm());
			answer.setUser(user);
			if (formAnswCtrl.saveAnswer(answer))
				return "success";
		}

		return "error";
	}

}