package br.unifesp.maritaca.model.impl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.PrivateAccess;
import br.unifesp.maritaca.util.Utils;

public class FormAnswerModelImpl implements FormAnswerModel {
	private EntityManager entityManager;
	private UserModel userModel;

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
			form.setUrl(getUniqueUrl());
			if (entityManager.persist(form)) {
				// save permissions of a form
				FormPermissions formPer = new FormPermissions();
				formPer.setForm(form);
				formPer.setGroup(userModel.getAllUsersGroup());
				formPer.setFormAccess(new PrivateAccess());
				if (entityManager.persist(formPer)) {
					// all saved correctly
					return true;
				} else {
					// default permissions don't save
					// delete form
					deleteForm(form);
					throw new RuntimeException(
							"Not possible to establish default permissions, form not saved");
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

	/**
	 * Get an unique url for a form
	 * 
	 * @return
	 */
	private String getUniqueUrl() {
		// TODO: check if this random string is enough
		// maybe it is better to generate uuid-based string
		String url = Utils.randomString();
		if (!urlForSharingExists(url))
			return url;
		else
			return getUniqueUrl();
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

	// @Override
	// public FormShare getFormShare(Form form) {
	// if (entityManager == null)
	// return null;
	// List<FormShare> result = entityManager.cQuery(FormShare.class, "form",
	// form.getKey().toString());
	// // todo: need to support Form sharing for different users
	// if (result.size() > 0) {
	// return result.get(0);
	// }
	// return null;
	// }

	@Override
	public boolean urlForSharingExists(String url) {
		if (entityManager == null)
			return true;// todo: improve this
		// look for url in the Form columnFamily
		List<Form> fsList = entityManager.cQuery(Form.class, "url", url, true);
		return fsList.size() > 0;
	}

	@Override
	public String getFormIdFromUrl(String url) {
		if (entityManager != null) {
			// look for url in the FormShare columnFamily
			List<Form> fsList = entityManager.cQuery(Form.class, "url", url,
					true);
			if (fsList.size() > 0)
				return fsList.get(0).getKey().toString();
		}
		return null;
	}

	@Override
	public UserModel getUserModel() {
		return userModel;
	}

	@Override
	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

}
