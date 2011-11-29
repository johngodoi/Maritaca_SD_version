package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@SessionScoped
public class ViewAnswerBean extends AbstractBean{
	Answer answer;

	public ViewAnswerBean() {
		super(true,false);
	}

	public Answer getAnswer() {
		if (answer.getXml() == null) {
			this.answer = formAnswCtrl.getAnswer(answer.getKey());
		}return answer;
	}

	public void setAnswer(Answer ans) {
		this.answer = ans;
	}
}
