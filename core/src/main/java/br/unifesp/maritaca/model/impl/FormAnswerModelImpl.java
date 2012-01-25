package br.unifesp.maritaca.model.impl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.access.PrivateAccess;
import br.unifesp.maritaca.access.operation.Edit;
import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.UserLocator;
import br.unifesp.maritaca.util.Utils;

public class FormAnswerModelImpl implements FormAnswerModel {
	private EntityManager entityManager;
	private UserModel userModel;
	private User currentUser;

	public FormAnswerModelImpl() {
		currentUser = UserLocator.getCurrentUser();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * Save/Update a form if the User has permissions
	 */
	@Override
	public boolean saveForm(Form form) {
		if (entityManager == null)
			return false;

		if (form.getUser() == null || form.getUser().getKey() == null)
			throw new IllegalArgumentException("User cannot be null");

		if (entityManager.rowDataExists(User.class, form.getUser().getKey())) {
			boolean newForm = form.getKey() == null;// true if form is new
			boolean hasPermission = true;
			if (!newForm
					&& entityManager.find(Form.class, form.getKey()) == null) {
				// form is new
				newForm = true;
			}
			if (newForm) {
				// set unique url for a new form
				form.setUrl(getUniqueUrl());
			}else{
				//check permissions for updating
				hasPermission = currentUserHasPermission(form, Edit.getInstance());
			}
			
			boolean result=false;
			if(hasPermission || newForm){
				result = entityManager.persist(form);
			}
			if (result) {
				if (newForm) {
					// save permissions of a form
					if (savePermissionNewForm(form)) {
						// all saved correctly
						return true;
					} else {
						// default permissions don't save
						// delete form
						deleteForm(form);
						throw new RuntimeException(
								"Not possible to establish default permissions, form not saved");
					}
				} else
					return true;
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
	 * save default permission of a new form
	 * @param form
	 * @return
	 */
	private boolean savePermissionNewForm(Form form) {
		FormPermissions formPer = new FormPermissions();
		formPer.setForm(form);
		formPer.setGroup(userModel.getAllUsersGroup());
		formPer.setFormAccess(PrivateAccess.getInstance());
		formPer.setAnswAccess(PrivateAccess.getInstance());
		return entityManager.persist(formPer);
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

	/**
	 * List all forms in the database
	 * temporary function. must be deleted
	 * TODO: change funtion with one that accepts parameters
	 * for pagination and range
	 */
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

	@Override
	public List<FormPermissions> getFormPermissions(Form form) {
		if (entityManager == null)
			return null;
		if (form == null || form.getKey() == null) {
			throw new IllegalArgumentException("Form null or without Id");
		}
		List<FormPermissions> result = entityManager.cQuery(
				FormPermissions.class, "form", form.getKey().toString());
		for (FormPermissions fp : result) {
			fp.setGroup(userModel.getGroup(fp.getGroup().getKey()));
		}
		return result;
	}

	@Override
	public FormPermissions getFormPermissionById(String formPermId) {
		if (entityManager == null || formPermId == null)
			return null;
		return entityManager.find(FormPermissions.class,
				UUID.fromString(formPermId));
	}

	@Override
	public boolean saveFormPermission(FormPermissions fp) {
		if (entityManager == null)
			return false;
		// verify parameters
		if (fp == null || fp.getForm() == null || fp.getGroup() == null) {
			throw new IllegalArgumentException(
					"Incomplete parameters, form permission not saved");
		}

		// verify if form and group exists
		if (entityManager.find(Form.class, fp.getForm().getKey(), true) == null) {
			throw new IllegalArgumentException(
					"Form not exists, form permission not saved");
		}
		if (entityManager.find(Group.class, fp.getGroup().getKey(), true) == null) {
			throw new IllegalArgumentException(
					"Form not exists, form permission not saved");
		}

		// persist fp
		return entityManager.persist(fp);
	}

	/**
	 * Checks if an user has permission of an operation over an entity
	 */
	@Override
	public <T> boolean currentUserHasPermission(T entity, Operation op) {
		if (entityManager == null || getCurrentUser() == null)
			return false;
		User user = getCurrentUser();
		// check type
		if (entity instanceof Form) {
			Form form = (Form) entity;
			// if form.user equals current user, user is owner and has all
			// permissions
			if (user.equals(form.getUser())) {
				return true;
			}
			// not a owner, get permission of form
			List<FormPermissions> listFP = getFormPermissions(form);
			for (FormPermissions fp : listFP) {
				if (fp.getFormAccess().isOperationEnabled(op)
						&& userModel.userIsMemberOfGroup(user, fp.getGroup())) {
					// if operation OP is enable in FormAccess and current user
					// is member of group fp.getGroup, so it has permission
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * get all forms of an user
	 */
	@Override
	public Collection<Form> listAllFormsMinimalByUser(User user) {
		if(entityManager == null) return null;
		if(user == null || user.getKey() == null)
			throw new IllegalArgumentException("User must have a key");
		
		return entityManager.cQuery(Form.class, "user", user.getKey().toString(), true);
	}

}
