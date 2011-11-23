package br.unifesp.maritaca.control;

import java.util.Collection;
import java.util.UUID;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public interface FormAnswerControl {
	boolean saveForm(Form form);

	Form getForm(UUID uid);

	void setEntityManager(EntityManager em);

	Collection<Form> listAllForms();

	Collection<Form> listAllFormsMinimal();

	/************* ANSWER ***********/
	boolean saveAnswer(Answer answer);

	Answer getAnswer(UUID uuid);

	Collection<Answer> listAllAnswers(UUID formId);

	Collection<Answer> listAllAnswersMinimal(UUID formId);
}
