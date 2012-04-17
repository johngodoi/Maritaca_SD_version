package br.unifesp.maritaca.business.answer.list;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.answer.list.dao.ListAnswersDAO;
import br.unifesp.maritaca.business.answer.list.dto.AnswerListDTO;
import br.unifesp.maritaca.business.form.dto.FormDTO;

@Stateless
public class ListAnswersEJB {

	@Inject
	private ListAnswersDAO listAnswersDAO;

	/**
	 * Returns all the answers to the given form.
	 * TODO Must check permissions!
	 * @param form
	 * @return
	 * @author tiagobarabasz
	 */
	public AnswerListDTO findAnswerListFromForm(FormDTO form){
		AnswerListDTO answerListDTO = new AnswerListDTO();
		
		answerListDTO.setFormTitle(form.getTitle());
		answerListDTO.setQuestions(listAnswersDAO.findQuestionsFromForm(form));
		answerListDTO.setAnswers(listAnswersDAO.findAnswersFromForm(form));				
				
		return answerListDTO;
	}	
}
