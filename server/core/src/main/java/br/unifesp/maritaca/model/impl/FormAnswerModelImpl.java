package br.unifesp.maritaca.model.impl;

import static br.unifesp.maritaca.util.UtilsCore.verifyEM;
import static br.unifesp.maritaca.util.UtilsCore.verifyEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.access.AccessLevel;
import br.unifesp.maritaca.access.Policy;
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
import br.unifesp.maritaca.util.UtilsCore;

public class FormAnswerModelImpl implements FormAnswerModel {
	private static final Log log = LogFactory.getLog(FormAnswerModelImpl.class);
	private EntityManager entityManager;
	private UserModel userModel;
	private User currentUser;

	public FormAnswerModelImpl() {
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public User getCurrentUser() {
		if(currentUser==null){
			setCurrentUser(UserLocator.getCurrentUser());
		}
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

		verifyEntity(form.getUser());
		
		if(form.getTitle() == null || form.getTitle().length()==0){
			return false;//with out title...
		}
		
		if (entityManager.rowDataExists(User.class, form.getUser().getKey())) {
			//TODO: user must be set from currentuser variable
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
				hasPermission = currentUserHasPermission(form, Operation.WRITE);
			}

			boolean result = false;
			if (hasPermission || newForm) {
				result = entityManager.persist(form);
			}
			if (result) {
					// save permissions of a form
					if (saveFormPermissionsByPolicy(form)) {
						// all saved correctly
						return true;
					} else {
						// default permissions don't save
						// delete form
						deleteForm(form);
						log.error("Not possible to establish default permissions, form not saved");
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
		String url = UtilsCore.randomString();
		if (!urlForSharingExists(url))
			return url;
		else
			return getUniqueUrl();
	}

	@Override
	public Form getForm(UUID uid, boolean minimal) {
		verifyEM(entityManager);
		return entityManager.find(Form.class, uid, minimal);
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

	// TODO In the future implement this on entitymanager
	public Collection<Form> listAllFormsSortedbyName(User user) {
		Collection<Form> forms = listAllFormsMinimalByUser(user);
		for (Form form : forms) {
			form.setFlagToOrder(0);
		}
		Collections.sort((List<Form>) forms);
		return forms;
	}

	// TODO In the future implement this on entitymanager
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
		//TODO: add permission verification
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

	/**
	 * Delete a Form if user has permission
	 */
	@Override
	public void deleteForm(Form form) {
		verifyEM(entityManager);
		// verify if current user has permissions
		if (currentUserHasPermission(form, Operation.DELETE)) {

			// first delete permissions
			for (FormPermissions fp : getFormPermissions(form)) {
				entityManager.delete(fp);
			}
			entityManager.delete(form);
			// TODO delete answers?
		} else {
			// user does not has permission
			// TODO: generate exception?
		}
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

		if (fp.getKey() != null
				&& !currentUserHasPermission(fp.getForm(), Operation.WRITE)) {
			// user does not have permission to edit
			return false;
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

		// verify if group-form pair exists in form permissions for new
		// permission
		if (fp.getKey() == null) {
			for (FormPermissions formperm : getFormPermissions(fp.getForm())) {
				if (formperm.getGroup().getKey().equals(fp.getGroup().getKey())) {
					// update the formpermissions, set old key to update the
					// data
					fp.setKey(formperm.getKey());
				}
			}
		}

		// persist fp
		return entityManager.persist(fp);
	}

	/**
	 * Checks if current user has permission of an operation over an entity
	 */
	@Override
	public <T> boolean currentUserHasPermission(T entity, Operation op) {
		User user = getCurrentUser();
		return userHasPermission(user, entity, op);
	}

	/**
	 * Checks if an user has permission of an operation over an entity
	 */
	@Override
	public <T> boolean userHasPermission(User user, T entity, Operation op) {
		verifyEM(entityManager);
		if (user == null && op == null)
			return false;
		// check type
		if (entity instanceof Form) {
			return userHasPermissionInForm(user, (Form) entity, op);
		} else if (entity instanceof FormPermissions) {
			return userHasPermissionInFormPermissions(user,
					(FormPermissions) entity, op);
		}

		return false;
	}

	private boolean userHasPermissionInForm(User user, Form form, Operation op) {
		if (form.getUser() == null) {
			form = getForm(form.getKey(), true);
		}
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
				// is member of group fp.getGroup, so he/she has permission
				return true;
			}
		}
		return false;
	}

	private boolean userHasPermissionInFormPermissions(User user,
			FormPermissions formPerm, Operation op) {
		Group allUsers = userModel.getAllUsersGroup();
		// verify if the permission is for the AllUsers group
		if (allUsers.getKey().equals(formPerm.getGroup().getKey())) {
			if (Operation.DELETE.equals(op)) {
				// allusers group permissions CANNOT be deleted
				return false;
			}
		}
		// since operation is no deleted or group is not AllUsers, verify form
		// permissions
		return userHasPermission(user, formPerm.getForm(), op);
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
		User currentUser = getCurrentUser();

		Set<Form> forms = new HashSet<Form>();
		// get groups where user is member
		Collection<GroupUser> groups = userModel.getGroupsByMember(user);
		for (GroupUser gu : groups) {
			// get all formpermissions in each group
			Collection<FormPermissions> l1Forms = getFormPermissionsByGroup(gu
					.getGroup());
			for (FormPermissions fp : l1Forms) {
				// get the form and add it if expdate > now
				Form form = getFormWithPermission(fp, true);
				if (form != null) {
					if (!form.getUser().equals(currentUser))
						forms.add(form);
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

	/**
	 * get a Form if has permission granted
	 * 
	 * @param fp
	 * @param minimal
	 * @return
	 */
	private Form getFormWithPermission(FormPermissions fp, boolean minimal) {
		Form form = null;
		long now = System.currentTimeMillis();
		// verify expiration date
		if (fp.getExpDate() != null && fp.getExpDate() < now) {
				// TODO delete fp?, it has expired or add ttl in column?
		} else if (!fp.getFormAccess().equals(AccessLevel.NO_ACCESS)) {
			// date ok, verify access
			form = getForm(fp.getForm().getKey(), minimal);
		}
		return form;
	}

	@Override
	public void close() {
		entityManager = null;
		currentUser = null;
		userModel = null;
	}

	@Override
	public void deleteFormPermission(FormPermissions formPerm) {
		verifyEntity(formPerm);
		entityManager.delete(formPerm);
	}
	
	
	private void deleteOldFormPermissions(Form form){
		for(FormPermissions fp : getFormPermissions(form)){
			deleteFormPermission(fp);
		}		
	}
	
	private boolean saveFormPermissionsByPolicy(Form form) {
		deleteOldFormPermissions(form);
		User                  owner       = userModel.getUser(form.getUser().getKey());
		Policy                policy      = form.getPolicy();					
		Group                 ownerGrp    = owner.getUserGroup();
		Group                 allUsersGrp = userModel.getAllUsersGroup();		
		List<FormPermissions> permissions = Policy.buildPermissions(policy, ownerGrp, allUsersGrp, null);
		
		return saveFormPermissions(form,permissions);
	}

	
	private boolean saveFormPermissions(Form form,	List<FormPermissions> permissions) {
		for(FormPermissions fp : permissions){
			fp.setForm(form);
			if(!saveFormPermission(fp)){
				return false;
			}
		}
		return true;
	}
}
