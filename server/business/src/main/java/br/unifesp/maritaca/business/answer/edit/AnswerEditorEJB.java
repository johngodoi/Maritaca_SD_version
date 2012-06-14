package br.unifesp.maritaca.business.answer.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

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
	private Logger logger = Logger
			.getLogger(this.getClass().getCanonicalName());

	@Inject
	private AnswerEditorDAO answerEditorDAO;

	public void saveAnswers(DataCollectedDTO collectedDTO) {
		Answer answer;
		for (AnswerWSDTO answerDTO : collectedDTO.getAnswerList().getAnswers()) {
			answer = new Answer();
			answer.setForm(UUID.fromString(collectedDTO.getFormId()));
			answer.setUser(UUID.fromString(collectedDTO.getUserId()));
			answer.setKey(UUID.fromString(collectedDTO.getFormId()));
			answer.setCreationDate(answerDTO.getTimestamp());
			logger.info("FORMID " + collectedDTO.getFormId());
			logger.info("USERID " + collectedDTO.getUserId());

			List<QuestionAnswer> questions = new ArrayList<QuestionAnswer>();
			for (QuestionAnswerDTO qaDTO : answerDTO.getQuestions()) {
				QuestionAnswer qa = UtilsBusiness.reflectClasses(qaDTO,
						QuestionAnswer.class);
				questions.add(qa);
			}

			answer.setQuestions(questions);

			getAnswerEditorDAO().saveAnswer(answer);
		}

	}

	public AnswerEditorDAO getAnswerEditorDAO() {
		return answerEditorDAO;
	}

	public void setAnswerEditorDAO(AnswerEditorDAO answerEditorDAO) {
		this.answerEditorDAO = answerEditorDAO;
	}
}
