package br.unifesp.maritaca.business.answer.list.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.answer.list.dto.AnswerListItemDTO;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.parser.XmlParser;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;

public class ListAnswersDAO extends BaseDAO {

	/**
	 * Find the answers to the questions to the given form. The
	 * answers are returned inside a list in the same order  of
	 * the correspondent questions.
	 * @param form
	 * @return A list containing the answers
	 * @author tiagobarabasz
	 */
	public List<AnswerListItemDTO> findAnswersFromForm(FormDTO form) {
		List<AnswerListItemDTO> answersDTO = new ArrayList<AnswerListItemDTO>();
		List<Answer> answers;

		answers = getEntityManager().cQuery(Answer.class, "form",
				form.getKey().toString());

		for (Answer answer : answers) {
			answersDTO.add(answerToDto(answer));
		}

		return answersDTO;
	}
	
	/**
	 * Returns the question labels for the given form. They
	 * are paresed from the form XML. If it is not set   in
	 * the given form, then it is searched in the database.
	 * @param form
	 * @return A String list containing the question labels
	 * @author tiagobarabasz
	 */
	public List<String> findQuestionsFromForm(FormDTO form) {
		String formXml;
		if(form.getXml()==null){
			Form f = getEntityManager().find(Form.class, form.getKey());
			formXml = f.getXml();
		} else {
			formXml = form.getXml();
		}
		return XmlParser.parseQuestionsLabels(formXml);
	}
	
	/**
	 * Converts the given answer into its DTO representation.
	 * @param answer
	 * @return answerDTO
	 * @author tiagobarabasz
	 */
	private AnswerListItemDTO answerToDto(Answer answer) {
		AnswerListItemDTO answerDTO = new AnswerListItemDTO();

		UUID userKey = answer.getUser().getKey();
		User user = getEntityManager().find(User.class, userKey);
						
		List<String> answers = XmlParser.parseAnswers(answer.getXml());

		answerDTO.setUser(user.getEmail());
		answerDTO.setCollectDate(answer.getCollectionDate());
		answerDTO.setAnswers(answers);

		return answerDTO;
	}
}