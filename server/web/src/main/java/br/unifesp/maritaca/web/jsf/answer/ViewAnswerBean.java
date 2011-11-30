package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class ViewAnswerBean extends AbstractBean{
	private Answer answer;
	
	@ManagedProperty("#{param.answerid}")
	private String answerId;

	public ViewAnswerBean() {
		super(true,false);
	}

	public Answer getAnswer() {
		if (answer!=null && answer.getXml() == null) {
			this.answer = formAnswCtrl.getAnswer(answer.getKey());
		}return answer;
	}

	public void setAnswer(Answer ans) {
		this.answer = ans;
	}
	
	public void setAnswerId(String id){
		if(id!= null && !id.equals("new")){
			answerId = id;
			answer = new Answer();
			answer.setKey(id);
		}
	}
	
	public String getAnswerId(){
		return answerId;
	}
}
