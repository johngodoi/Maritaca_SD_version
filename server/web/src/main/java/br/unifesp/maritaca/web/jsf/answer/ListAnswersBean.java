package br.unifesp.maritaca.web.jsf.answer;

import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@SessionScoped
public class ListAnswersBean extends AbstractBean {
	private Form form;
	private Collection<Answer> answers;
	
	public ListAnswersBean() {
		super(true, false);
	}
	
	public Collection<Answer> getAnswers() {
		answers = formAnswCtrl.listAllAnswersMinimal(form.getKey());
		return answers;
	}
	public void setAnswers(Collection<Answer> answers) {
		this.answers = answers;
	}
	
	public String viewAnswer(){
		return "viewAnswer";
	}
	
	public Form getForm() {
		return form;
	}
	
	public void setForm(Form form) {
		this.form = form;
	}
}
