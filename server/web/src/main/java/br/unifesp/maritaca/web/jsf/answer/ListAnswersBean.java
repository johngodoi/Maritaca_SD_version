package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.answer.list.ListAnswersEJB;
import br.unifesp.maritaca.business.answer.list.dto.AnswerListDTO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@ViewScoped
public class ListAnswersBean extends MaritacaJSFBean {
	private static final long serialVersionUID = 1L;
	
	private AnswerListDTO answerListDTO;	
	
	@Inject
	private ListAnswersEJB listAnswersEJB;	
	
	public ListAnswersBean() {
		/* Default constructor: Nothing here */
	}
	
	public String listAnswers(FormDTO form){
		setAnswerList(listAnswersEJB.findAnswerListFromForm(form));
		getModuleManager().activeModAndSub("Answer", "listAnswers");
		return null;
	}

	public AnswerListDTO getAnswerList() {
		return answerListDTO;
	}

	public void setAnswerList(AnswerListDTO answerListDTO) {
		this.answerListDTO = answerListDTO;
	}
}
