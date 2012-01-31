package br.unifesp.maritaca.web.jsf.answer;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@ViewScoped
public class ListAnswersBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	private Form form;
	private Collection<Answer> answers;

	public ListAnswersBean() {
		super(true, false);
		answers = new ArrayList<Answer>();
	}

	public Collection<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(Collection<Answer> answers) {
		this.answers = answers;
	}

	public String getViewAnswer() {
		return "viewAnswer";
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
		if (form != null) {
			answers = formAnswCtrl.listAllAnswersMinimal(form.getKey());
		}
	}
}
