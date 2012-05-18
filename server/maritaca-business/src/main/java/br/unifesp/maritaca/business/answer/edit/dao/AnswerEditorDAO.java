package br.unifesp.maritaca.business.answer.edit.dao;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.core.Answer;

public class AnswerEditorDAO extends BaseDAO {

	public void saveAnswers(Answer answers) {
		entityManager.persist(answers);
	}

}
