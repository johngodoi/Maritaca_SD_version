package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.answer.list.AnswersListerEJB;
import br.unifesp.maritaca.business.answer.list.dto.AnswerListerDTO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@ViewScoped
public class AnswersListerBean extends MaritacaJSFBean {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private AnswersListerEJB answersListerEJB;	

	private AnswerListerDTO answerListerDTO;	
	private Integer numberOfPages;
	
	public AnswersListerBean() {
		/* Default constructor: Nothing here */
	}
	
	public String listAnswers(FormDTO form){
		setAnswerListerDTO(answersListerEJB.
				findAnswerListerDTO(form, super.getCurrentUser()));
		getModuleManager().activeModAndSub("Answer", "listAnswers");
		return null;
	}

	public AnswerListerDTO getAnswerListerDTO() {
		return answerListerDTO;
	}
	
	public void setAnswerListerDTO(AnswerListerDTO answerListerDTO) {
		this.answerListerDTO = answerListerDTO;
	}

	public Integer getNumberOfPages() {
		Integer numPages = getAnswerListerDTO()!=null?getAnswerListerDTO().getAnswers()!=null?getAnswerListerDTO().getAnswers().size():0:0;
		return super.getNumberOfPages(numPages, getItemsPerPage());
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
}
