package br.unifesp.maritaca.model.impl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormShare;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.PrivateAccess;
import br.unifesp.maritaca.util.Utils;

public class FormAnswerModelImpl implements FormAnswerModel {
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public boolean saveForm(Form form) {
		if (entityManager == null)
			return false;

		if (form.getUser() == null || form.getUser().getKey() == null)
			throw new IllegalArgumentException("User cannot be null");

		if (entityManager.rowDataExists(User.class, form.getUser().getKey())) {
			if (entityManager.persist(form)) {
				// create default form share (private)
				FormShare fs = new FormShare();
				fs.setForm(form);
				// create random url for sharing
				String url = Utils.randomString();
				if (!urlForSharingExists(url)) {
					fs.setUrl(url);
					fs.setPublicAccess(new PrivateAccess());
					return entityManager.persist(fs);
				} else {
					// form was save but sharing policy was not saved
					// todo: do something about that...
					return true;
				}
			} else {
				// form not saved
				return false;
			}
		} else {
			throw new IllegalArgumentException(
					"User does not exist in database");
		}
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
	public Collection<Form> listAllFormsMinimal() {
		if (entityManager == null)
			return null;

		return entityManager.listAll(Form.class, true);
	}

	@Override
	public boolean saveAnswer(Answer response) {
		if (entityManager == null)
			return false;

		if (response.getUser() == null || response.getUser().getKey() == null)
			throw new IllegalArgumentException("User cannot be null");

		if (!entityManager.rowDataExists(User.class, response.getUser()
				.getKey())) {
			throw new IllegalArgumentException(
					"User does not exist in database");
		}

		if (response.getForm() != null
				&& entityManager.rowDataExists(Form.class, response.getForm()
						.getKey())) {
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
	public Collection<Answer> listAllAnswers(UUID formId) {
		if (entityManager == null)
			return null;
		if (formId == null)
			return entityManager.listAll(Answer.class);
		else
			return entityManager
					.cQuery(Answer.class, "form", formId.toString());
	}

	@Override
	public Collection<Answer> listAllAnswersMinimal(UUID formId) {
		if (entityManager == null)
			return null;
		if (formId == null)
			return entityManager.listAll(Answer.class, true);
		else
			return entityManager
					.cQuery(Answer.class, "form", formId.toString());
	}

	@Override
	public void deleteForm(Form form) {
		if (entityManager == null)
			return;
		entityManager.delete(form);
	}

	@Override
	public FormShare getFormShare(Form form) {
		if (entityManager == null)
			return null;
		List<FormShare> result = entityManager.cQuery(FormShare.class, "form",
				form.getKey().toString());
		// todo: need to support Form sharing for different users
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	@Override
	public boolean urlForSharingExists(String url) {
		if (entityManager == null)
			return true;//todo: improve this
		//look for url in the FormShare columnFamily
		List<FormShare> fsList = entityManager.cQuery(FormShare.class, "url",
				url, true);
		return fsList.size() > 0;
	}

	@Override
	public String getFormIdFromUrl(String url) {
		if (entityManager != null) {
			//look for url in the FormShare columnFamily
			List<FormShare> fsList = entityManager.cQuery(FormShare.class,
					"url", url, true);
			if (fsList.size() > 0)
				return fsList.get(0).getForm().getKey().toString();
		}
		return null;
	}

}
