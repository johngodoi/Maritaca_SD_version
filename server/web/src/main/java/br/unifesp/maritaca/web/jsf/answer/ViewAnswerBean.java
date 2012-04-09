package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class ViewAnswerBean extends AbstractBean{
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty("#{param.answerid}")
	private String answerId;

	public ViewAnswerBean() {
		super(true,false);
	}

	public Answer getAnswer() {		
		return null; //TODO Not implemented yet
	}

	public void setAnswer(Answer ans) {

	}
	
	public void setAnswerId(String id){
		
	}
	
	public String getAnswerId(){
		return answerId;
	}
}
