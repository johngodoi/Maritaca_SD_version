package br.unifesp.maritaca.model.impl;

import static br.unifesp.maritaca.util.UtilsCore.verifyEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.unifesp.maritaca.access.AccessLevel;
import br.unifesp.maritaca.access.Policy;
import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.AuthorizationDenied;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.UseEntityManager;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.UserLocator;
import br.unifesp.maritaca.util.UtilsCore;

public class FormAnswerModelImpl implements FormAnswerModel, UseEntityManager, Serializable{

	private static final long serialVersionUID = 1L;
	
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

	@Override
	public boolean saveForm(Form form, List<MaritacaList> lists) {
		verifyEntity(form.getUser());
		
		if(form.getTitle() == null || form.getTitle().length()==0){
			throw new IllegalArgumentException("Form does not have a title");
		}
		
		if (entityManager.rowDataExists(User.class, form.getUser().getKey())) {
			//TODO: user must be set from currentuser variable
			boolean newForm = form.getKey() == null;// true if form is new
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
				Form originalForm = getForm(form.getKey(), true);
				if(!currentUserHasPermission(originalForm, Operation.UPDATE)){
					throw new AuthorizationDenied(Form.class, form.getKey(), getCurrentUser().getKey(), Operation.UPDATE);
				}
			}
			if (entityManager.persist(form)) {
				// save permissions of a form
				saveFormPermissionsByPolicy(form,lists);
			} else {
				return false;
			}
			return true;
		} else {
			throw new IllegalArgumentException("User does not exist in database");
		}		
	}
	
	/**
	 * Save/Update a form if the User has permissions
	 */
	@Override
	public boolean saveForm(Form form) {
		return saveForm(form, new ArrayList<MaritacaList>());
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
		if (uid == null ) {
			throw new IllegalArgumentException("Incomplete parameters");
		}
		Form form = entityManager.find(Form.class, uid, minimal);
		if(userHasPermission(getCurrentUser(), form, Operation.READ)){
			return form;	
		} else {
			throw new AuthorizationDenied(Form.class, form.getKey(), getCurrentUser().getKey(), Operation.READ);
		}		
	}

	@Override
	public Collection<Form> listAllForms() {
		return entityManager.listAll(Form.class);
	}

	/**
	 * List all forms in the database temporary function. must be deleted TODO:
	 * change funtion with one that accepts parameters for pagination and range
	 */
	@Override
	public Collection<Form> listAllFormsMinimal() {
		return entityManager.listAll(Form.class, true);
	}

	// TODO In the future implement this on entitymanager
	public Collection<Form> listAllFormsSortedbyName(User user) {
		Collection<Form> forms = listFormsFromCurrentUser(true);
		for (Form form : forms) {
			form.setFlagToOrder(0);
		}
		Collections.sort((List<Form>) forms);
		return forms;
	}

	// TODO In the future implement this on entitymanager
	public Collection<Form> listAllFormsSortedbyDate(User user) {
		Collection<Form> forms = listFormsFromCurrentUser(true);
		for (Form form : forms) {
			form.setFlagToOrder(1);
		}
		Collections.sort((List<Form>) forms);
		return forms;
	}

	@Override
	public boolean saveAnswer(Answer response) {
		//TODO: add permission verification

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
		return entityManager.find(Answer.class, uuid);
	}

	@Override
	public Collection<Answer> listAllAnswers(UUID formId) {
		if (formId == null)
			return entityManager.listAll(Answer.class);
		else
			return entityManager
					.cQuery(Answer.class, "form", formId.toString());
	}

	@Override
	public Collection<Answer> listAllAnswersMinimal(UUID formId) {
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
		// verify if current user has permissions
		if (currentUserHasPermission(form, Operation.DELETE)) {

			// first delete permissions
			for (FormPermissions fp : getFormPermissions(form)) {
				entityManager.delete(fp);
			}
			entityManager.delete(form);
			// TODO delete answers?
		} else {
			throw new AuthorizationDenied(Form.class, form.getKey(), getCurrentUser().getKey(), Operation.DELETE);
		}
	}

	@Override
	public boolean urlForSharingExists(String url) {
		// todo: improve this
		// look for url in the Form columnFamily
		List<Form> fsList = entityManager.cQuery(Form.class, "url", url, true);
		return fsList.size() > 0;
	}

	@Override
	public String getFormIdFromUrl(String url) {
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
		verifyEntity(form);

		List<FormPermissions> result = entityManager.cQuery(
				FormPermissions.class, "form", form.getKey().toString());
		for (FormPermissions fp : result) {
			MaritacaList grp = userModel.getMaritacaList(fp.getMaritacaList().getKey());
			if(grp == null){
				throw new RuntimeException(
						"Can't find MaritacaList listed in FormPermissions: " + fp.getKey());
			}
			fp.setMaritacaList(grp);
		}
		return result;
	}

	@Override
	public FormPermissions getFormPermissionById(String formPermId) {
		if (formPermId == null)
			return null;
		return entityManager.find(FormPermissions.class,
				UUID.fromString(formPermId));
	}

	private void saveFormPermission(FormPermissions fp) {
		// verify parameters
		if (fp == null || fp.getForm() == null || fp.getMaritacaList() == null) {
			throw new IllegalArgumentException(
					"Incomplete parameters, form permission not saved");
		}

		if(fp.getKey()!=null && !currentUserHasPermission(fp.getForm(),Operation.UPDATE)){
		// Form exists and user does not have permission to update
			throw new AuthorizationDenied(Form.class, fp.getForm().getKey(), getCurrentUser().getKey(), Operation.UPDATE);
		}

		// verify if form and list exists
		if (entityManager.find(Form.class, fp.getForm().getKey(), true) == null) {
			throw new IllegalArgumentException(
					"Form not exists, form permission not saved");
		}
		if (entityManager.find(MaritacaList.class, fp.getMaritacaList().getKey(), true) == null) {
			throw new IllegalArgumentException(
					"Form not exists, form permission not saved");
		}

		// verify if list-form pair exists in form permissions for new
		// permission
		if (fp.getKey() == null) {
			for (FormPermissions formperm : getFormPermissions(fp.getForm())) {
				if (formperm.getMaritacaList().getKey().equals(fp.getMaritacaList().getKey())) {
					// update the formpermissions, set old key to update the
					// data
					fp.setKey(formperm.getKey());
				}
			}
		}

		// persist fp
		if(!entityManager.persist(fp)){
			throw new RuntimeException("Error persisting FormPermission");
		}
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
		if (user == null || op == null)
			return false;
		
		User root = userModel.getRootUser();
		if(user.equals(root)){
			return true;
		}
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
		
		// Get permissions of form
		List<FormPermissions> listFP = getFormPermissions(form);
		for (FormPermissions fp : listFP) {
			if (fp.getFormAccess().isOperationEnabled(op)
					&& userModel.userIsMemberOfMaritacaList(user, fp.getMaritacaList())) {
				// if operation OP is enable in FormAccess and current user
				// is member of list fp.getlist, so he/she has permission
				return true;
			}
		}
		return false;
	}

	private boolean userHasPermissionInFormPermissions(User user,
			FormPermissions formPerm, Operation op) {
		MaritacaList allUsers = userModel.getAllUsersList();
		// verify if the permission is for the AllUsers list
		if (allUsers.getKey().equals(formPerm.getMaritacaList().getKey())) {
			if (Operation.DELETE.equals(op)) {
				// allusers list permissions CANNOT be deleted
				return false;
			}
		}
		// since operation is no deleted or list is not AllUsers, verify form
		// permissions
		return userHasPermission(user, formPerm.getForm(), op);
	}

	@Override
	public Collection<Form> listFormsFromCurrentUser(boolean minimal){
		User user = getCurrentUser();
		verifyEntity(user);
		return entityManager.cQuery(Form.class, "user", user.getKey()
				.toString(), minimal);		
	}
	
	/**
	 * @param User
	 * @param Boolean
	 *            : true to get just minimal information, false to get all
	 *            information
	 * @return all Forms that the user has access but is not the owner (Forms
	 *         that are shared through lists)
	 */
	@Override
	public Collection<Form> listSharedFormsFromCurrentUser(boolean minimal) {
		User user = getCurrentUser();
		
		verifyEntity(user);
		User currentUser = getCurrentUser();

		Set<Form> forms = new HashSet<Form>();
		// get lists where user is member
		Collection<MaritacaListUser> lists = userModel.getMaritacaListByMember(user);
		for (MaritacaListUser gu : lists) {
			// get all formpermissions in each list
			Collection<FormPermissions> l1Forms = getFormPermissionsByList(gu
					.getMaritacaList());
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
	 * @param list
	 * @return Return the permissions list of a form in a list
	 */
	@Override
	public Collection<FormPermissions> getFormPermissionsByList(MaritacaList list) {
		verifyEntity(list);

		List<FormPermissions> result = entityManager.cQuery(
				FormPermissions.class, "maritacaList", list.getKey().toString());
		for (FormPermissions fp : result) {
			fp.setMaritacaList(list);
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
	
	/**
	 * Updates the form permissions to the owner, public and form lists.
	 * This methods requires UPDATE permission on target form which
	 * should be checked before calling it.
	 * @param form
	 * @param formLists
	 */
	private void saveFormPermissionsByPolicy(Form form, List<MaritacaList> formLists) {
		if (form == null || form.getUser() == null || formLists == null) {
			throw new IllegalArgumentException(
					"Incomplete parameters, form permission not saved");
		}		
		deleteOldFormPermissions(form);
		
		User          owner        = userModel.getUser(form.getUser().getKey());
		MaritacaList  ownerList    = owner.getMaritacaList();
		MaritacaList  allUsersList = userModel.getAllUsersList();		

		Policy p = form.getPolicy();
		
		FormPermissions ownerPermissions  = p.buildOwnerFormPermission(form, ownerList);
		FormPermissions publicPermissions = p.buildPublicFormPermission(form, allUsersList);
		
		saveFormPermission(ownerPermissions);
		saveFormPermission(publicPermissions);
		
		for(MaritacaList list : formLists){
			if(! list.equals(ownerList)){
				FormPermissions listPermissions = p.buildListFormPermission(form, list);			
				saveFormPermission(listPermissions);
			}
		}
	}
}
