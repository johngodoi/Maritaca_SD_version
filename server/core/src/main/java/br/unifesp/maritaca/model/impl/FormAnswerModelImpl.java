package br.unifesp.maritaca.model.impl;

import static br.unifesp.maritaca.util.Utils.verifyEM;
import static br.unifesp.maritaca.util.Utils.verifyEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.unifesp.maritaca.access.AccessLevel;
import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
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
		verifyEM(entityManager);

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
			} else {
				// check permissions for updating
				hasPermission = currentUserHasPermission(form, Operation.EDIT);
			}

			boolean result = false;
			if (hasPermission || newForm) {
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
	 * 
	 * @param form
	 * @return
	 */
	private boolean savePermissionNewForm(Form form) {
		FormPermissions formPer = new FormPermissions();
		formPer.setForm(form);
		formPer.setGroup(userModel.getAllUsersGroup());
		formPer.setFormAccess(AccessLevel.PRIVATE_ACCESS);
		formPer.setAnswAccess(AccessLevel.PRIVATE_ACCESS);
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
		verifyEM(entityManager);

		return entityManager.find(Form.class, uid);
	}

	@Override
	public Collection<Form> listAllForms() {
		verifyEM(entityManager);

		return entityManager.listAll(Form.class);
	}

	/**
	 * List all forms in the database temporary function. must be deleted TODO:
	 * change funtion with one that accepts parameters for pagination and range
	 */
	@Override
	public Collection<Form> listAllFormsMinimal() {
		verifyEM(entityManager);

		return entityManager.listAll(Form.class, true);
	}

	// TODO In the future implement in the entitymanager
	public Collection<Form> listAllFormsSortedbyName(User user) {
		Collection<Form> forms = listAllFormsMinimalByUser(user);
		for (Form form : forms) {
			form.setFlagToOrder(0);
		}
		Collections.sort((List<Form>) forms);
		return forms;
	}

	// TODO In the future implement in the entitymanager
	public Collection<Form> listAllFormsSortedbyDate(User user) {
		Collection<Form> forms = listAllFormsMinimalByUser(user);
		for (Form form : forms) {
			form.setFlagToOrder(1);
		}
		Collections.sort((List<Form>) forms);
		return forms;
	}

	@Override
	public boolean saveAnswer(Answer response) {
		verifyEM(entityManager);

		if (response == null)
			throw new IllegalArgumentException("Response cannot be null");
		verifyEntity(response.getUser());

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
		verifyEM(entityManager);
		return entityManager.find(Answer.class, uuid);
	}

	@Override
	public Collection<Answer> listAllAnswers(UUID formId) {
		verifyEM(entityManager);
		if (formId == null)
			return entityManager.listAll(Answer.class);
		else
			return entityManager
					.cQuery(Answer.class, "form", formId.toString());
	}

	@Override
	public Collection<Answer> listAllAnswersMinimal(UUID formId) {
		verifyEM(entityManager);
		if (formId == null)
			return entityManager.listAll(Answer.class, true);
		else
			return entityManager
					.cQuery(Answer.class, "form", formId.toString());
	}

	@Override
	public void deleteForm(Form form) {
		verifyEM(entityManager);
		entityManager.delete(form);
		// TODO delete answers? permissions?
	}

	@Override
	public boolean urlForSharingExists(String url) {
		verifyEM(entityManager);
		// todo: improve this
		// look for url in the Form columnFamily
		List<Form> fsList = entityManager.cQuery(Form.class, "url", url, true);
		return fsList.size() > 0;
	}

	@Override
	public String getFormIdFromUrl(String url) {
		verifyEM(entityManager);

		// look for url in the FormShare columnFamily
		List<Form> fsList = entityManager.cQuery(Form.class, "url", url, true);
		if (fsList.size() > 0)
			return fsList.get(0).getKey().toString();
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
		verifyEM(entityManager);
		verifyEntity(form);

		List<FormPermissions> result = entityManager.cQuery(
				FormPermissions.class, "form", form.getKey().toString());
		for (FormPermissions fp : result) {
			fp.setGroup(userModel.getGroup(fp.getGroup().getKey()));
		}
		return result;
	}

	@Override
	public FormPermissions getFormPermissionById(String formPermId) {
		verifyEM(entityManager);
		if (formPermId == null)
			return null;
		return entityManager.find(FormPermissions.class,
				UUID.fromString(formPermId));
	}

	@Override
	public boolean saveFormPermission(FormPermissions fp) {
		verifyEM(entityManager);
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
		verifyEM(entityManager);
		if (getCurrentUser() == null && op == null)
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
		verifyEM(entityManager);
		verifyEntity(user);
		return entityManager.cQuery(Form.class, "user", user.getKey()
				.toString(), true);
	}

	/**
	 * @param User
	 * @param Boolean
	 *            : true to get just minimal information, false to get all
	 *            information
	 * @return all Forms that the user has access but is not the owner (Forms
	 *         that are shared through Groups)
	 */
	@Override
	public Collection<Form> listAllSharedForms(User user, boolean minimal) {
		verifyEM(entityManager);
		verifyEntity(user);

		Set<Form> forms = new HashSet<Form>();
		// get groups where user is member
		Collection<GroupUser> groups = userModel.getGroupsByMember(user);
		for (GroupUser gu : groups) {
			// get all formpermissions in each group
			Collection<FormPermissions> l1Forms = getFormPermissionsByGroup(gu
					.getGroup());
			for (FormPermissions fp : l1Forms) {
				// get the form and add it if expdate > now
				if (fp.getExpDate() > System.currentTimeMillis()) {
					Form form = fp.getForm();
					forms.add(form);
				} else {
					// TODO delete fp?, it has expired
					entityManager.delete(fp);
				}
			}
		}
		return new ArrayList<Form>(forms);
	}

	/**
	 * 
	 * @param group
	 * @return Return the permissions list of a form in a group
	 */
	@Override
	public Collection<FormPermissions> getFormPermissionsByGroup(Group group) {
		verifyEM(entityManager);
		verifyEntity(group);

		List<FormPermissions> result = entityManager.cQuery(
				FormPermissions.class, "group", group.getKey().toString());
		for (FormPermissions fp : result) {
			fp.setGroup(group);
		}
		return result;
	}

}
