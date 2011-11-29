package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@SessionScoped
public class EditAnswerBean extends AbstractBean{
	private User user;
	private Form form;
	private String xml;
	private Answer answer;
	private boolean newAnswer;
	
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
		return form;
	}
	
	public void setForm(Form form) {
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
	
	public String saveAnswer(){
		if(xml !=null && xml.length()>0){
			answer.setUser(user);
			answer.setXml(xml);
			answer.setForm(form);
			answer.setUser(user);
			if(formAnswCtrl.saveAnswer(answer))
				return "success";
		}
		
		return "error";
	}

}
