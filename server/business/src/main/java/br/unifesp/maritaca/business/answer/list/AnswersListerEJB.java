package br.unifesp.maritaca.business.answer.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.answer.list.dto.AnswerDTO;
import br.unifesp.maritaca.business.answer.list.dto.AnswerListerDTO;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.exception.AuthorizationDenied;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.parser.XmlParser;
import br.unifesp.maritaca.persistence.entity.Answer;
import br.unifesp.maritaca.persistence.entity.Form;
import br.unifesp.maritaca.persistence.entity.QuestionAnswer;
import br.unifesp.maritaca.persistence.permission.Document;
import br.unifesp.maritaca.persistence.permission.Operation;
import br.unifesp.maritaca.persistence.permission.Permission;

@Stateless
public class AnswersListerEJB extends AbstractEJB {
	private static final long serialVersionUID = 1L;

	@Inject
	private AnswersListerDAO answersListerDAO;

	@Inject
	private UserDAO userDAO;

	/**
	 * Returns all the answers to the given form.
	 * 
	 * @param formDTO
	 * @return
	 * @author tiagobarabasz
	 */
	public AnswerListerDTO findAnswerListerDTO(FormDTO formDTO, UserDTO userDTO) {
		AnswerListerDTO answerListerDTO = new AnswerListerDTO();

		answerListerDTO.setFormTitle(formDTO.getTitle());

		// List Questions
		Form form = getAnswersListerDAO().findFormByFormKey(formDTO.getKey());
		String formXml = form.getXml();

		List<List<String>> questionsTypes = XmlParser
				.parseQuestionsLabels(formXml);
		answerListerDTO.setTypes(questionsTypes.get(0));
		answerListerDTO.setQuestions(questionsTypes.get(1));

		// List AnswersDTO
		List<AnswerDTO> listAnswersDTO = new ArrayList<AnswerDTO>();

		// Checking read answer permissions
		Permission permission = super.getPermission(form, userDTO.getKey(),
				Document.ANSWER);
		if (permission != null && permission.getRead()) {
			List<Answer> listAnswers = getAnswersListerDAO()
					.findAnswersByFormKey(formDTO.getKey());
			AnswerDTO answerDTO;

			for (Answer answer : listAnswers) {
				answerDTO = new AnswerDTO();

				List<String> listAnswerParsed = new ArrayList<String>();
				for (QuestionAnswer questionAnswer : answer.getQuestions()) {
					listAnswerParsed.add(questionAnswer.getValue());
				}

				// TODO
				// List<String> listAnswerParsed = new ArrayList<String>();

				answerDTO.setUserEmail(this.getUserEmailbyUserKey(answer
						.getUser()));
				answerDTO.setCollectDate(new Date(answer.getCreationDate()));
				answerDTO.setAnswers(listAnswerParsed);

				listAnswersDTO.add(answerDTO);
			}
		} else {
			throw new AuthorizationDenied(Document.ANSWER, form.getKey(),
					userDTO.getKey(), Operation.READ);
		}
		answerListerDTO.setAnswers(listAnswersDTO);

		return answerListerDTO;
	}

	private String getUserEmailbyUserKey(UUID userKey) {
		return getUserDAO().findUserByKey(userKey).getEmail();
	}

	public AnswersListerDAO getAnswersListerDAO() {
		return answersListerDAO;
	}

	public void setAnswersListerDAO(AnswersListerDAO answersListerDAO) {
		this.answersListerDAO = answersListerDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

}
