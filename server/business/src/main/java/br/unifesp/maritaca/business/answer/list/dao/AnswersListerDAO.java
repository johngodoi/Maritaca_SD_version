package br.unifesp.maritaca.business.answer.list.dao;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.persistence.entity.Answer;
import br.unifesp.maritaca.persistence.entity.Form;

public class AnswersListerDAO extends BaseDAO {

	/**
	 * Find the answers to the questions to the given form. The
	 * answers are returned inside a list in the same order  of
	 * the correspondent questions.
	 * @param form
	 * @return A list containing the answers
	 * @author tiagobarabasz
	 */
	public List<Answer> findAnswersByFormKey(UUID formKey) {
		return getEntityManager().cQuery(Answer.class, "form",
				formKey.toString());
	}
	
	/**
	 * Returns the question labels for the given form. They
	 * are paresed from the form XML. If it is not set   in
	 * the given form, then it is searched in the database.
	 * @param form
	 * @return A String list containing the question labels
	 * @author tiagobarabasz
	 */
	public Form findFormByFormKey(UUID formKey) {
		return getEntityManager().find(Form.class, formKey);
	}
}