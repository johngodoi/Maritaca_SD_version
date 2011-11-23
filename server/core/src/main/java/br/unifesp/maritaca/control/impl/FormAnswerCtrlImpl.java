package br.unifesp.maritaca.control.impl;

import java.util.Collection;
import java.util.UUID;

import br.unifesp.maritaca.control.FormAnswerControl;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public class FormAnswerCtrlImpl implements FormAnswerControl {
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public boolean saveForm(Form form){
		if (entityManager == null)
			return false;

		return entityManager.persist(form);
	}

	@Override
	public Form getForm(UUID uid) {
		if (entityManager == null)
			return null;

		return entityManager.find(Form.class, uid);
	}

	@Override
	public Collection<Form> listAllForms() {
		if (entityManager == null)
			return null;

		return entityManager.listAll(Form.class);
	}

	@Override
	public Collection<Form> listAllFormsMinimal(){
		if (entityManager == null)
			return null;

		return entityManager.listAllMinimal(Form.class);
	}

	@Override
	public boolean saveAnswer(Answer response){
		if (entityManager == null)
			return false;
		if (response.getForm() != null
				&& entityManager.find(Form.class, response.getForm().getKey()) != null) {
			return entityManager.persist(response);
		} else {
			throw new IllegalArgumentException("Form id not valid nor present");
		}
	}

	@Override
	public Answer getAnswer(UUID uuid) {
		if (entityManager == null)
			return null;
		return entityManager.find(Answer.class, uuid);
	}

	@Override
	public Collection<Answer> listAllAnswers(UUID formId){
		if (entityManager == null)
			return null;
		if (formId == null)
			return entityManager.listAll(Answer.class);
		else
			return entityManager.cQuery(Answer.class, "form",
					formId.toString());
	}

	@Override
	public Collection<Answer> listAllAnswersMinimal(UUID formId){
		if (entityManager == null)
			return null;
		if (formId == null)
			return entityManager.listAllMinimal(Answer.class);
		else
			return entityManager.cQuery(Answer.class, "form",
					formId.toString());
	}

}
