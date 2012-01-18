package br.unifesp.maritaca.model;

import java.util.Collection;
import java.util.UUID;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public interface FormAnswerModel {
	boolean saveForm(Form form);

	Form getForm(UUID uid);

	void setEntityManager(EntityManager em);

	Collection<Form> listAllForms();

	Collection<Form> listAllFormsMinimal();

	void deleteForm(Form form);
	
	String getFormIdFromUrl(String url);
	
	/************* ANSWER ***********/
	boolean saveAnswer(Answer answer);

	Answer getAnswer(UUID uuid);

	Collection<Answer> listAllAnswers(UUID formId);

	Collection<Answer> listAllAnswersMinimal(UUID formId);

	boolean urlForSharingExists(String url);

	void setUserModel(UserModel userModel);

	UserModel getUserModel();


}
