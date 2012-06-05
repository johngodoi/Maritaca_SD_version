package br.unifesp.maritaca.business.answer.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.answer.edit.dto.AnswerWSDTO;
import br.unifesp.maritaca.business.answer.edit.dto.DataCollectedDTO;
import br.unifesp.maritaca.business.answer.edit.dto.QuestionAnswerDTO;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.persistence.entity.Answer;
import br.unifesp.maritaca.persistence.entity.QuestionAnswer;

@Stateless
public class AnswerEditorEJB extends AbstractEJB {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AnswerEditorDAO answerEditorDAO;
	
	public void saveAnswers(DataCollectedDTO collectedDTO) throws Exception{
		Answer answer;
		for (AnswerWSDTO answerDTO : collectedDTO.getAnswerList().getAnswers()) {
			answer = new Answer();
			answer.setForm(UUID.fromString(collectedDTO.getFormId()));
			answer.setUser(UUID.fromString(collectedDTO.getUserId()));
			answer.setCreationDate(answerDTO.getTimestamp());
			
			List<QuestionAnswer> questions = new ArrayList<QuestionAnswer>();
			for (QuestionAnswerDTO qaDTO : answerDTO.getQuestions()) {
				QuestionAnswer qa = UtilsBusiness.reflectClasses(qaDTO, QuestionAnswer.class);
				questions.add(qa);				
			}
			
			answer.setQuestions(questions);
			
			answerEditorDAO.saveAnswer(answer);		
		}
		
	}
}
