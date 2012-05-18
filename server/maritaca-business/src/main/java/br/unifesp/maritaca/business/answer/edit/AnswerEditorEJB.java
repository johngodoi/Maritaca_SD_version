package br.unifesp.maritaca.business.answer.edit;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.answer.edit.dao.AnswerEditorDAO;
import br.unifesp.maritaca.business.answer.list.dto.DataCollectedDTO;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.core.Answer;

@Stateless
public class AnswerEditorEJB extends AbstractEJB {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AnswerEditorDAO answerEditorDAO;
	
	public void saveAnswers(DataCollectedDTO collectedDTO){
		
		Answer answer = new Answer();
		answer.setUser(collectedDTO.getUserId());
		answer.setForm(collectedDTO.getFormId());
		answer.setXml(collectedDTO.toString());
		
		answerEditorDAO.saveAnswers(answer);		
	}

}
