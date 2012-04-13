package br.unifesp.maritaca.business.form.edit.dao;

import static br.unifesp.maritaca.util.UtilsCore.verifyEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.access.Policy;
import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.AuthorizationDenied;
import br.unifesp.maritaca.model.ManagerModel;

public class FormPermissionsDAO extends BaseDAO {
	
	String CFG_ROOT = "root";
	
	/**
	 * Updates the form permissions to the owner, public and form lists.
	 * This methods requires UPDATE permission on target form which
	 * should be checked before calling it.
	 * @param form
	 * @param formLists
	 */
	public void saveFormPermissionsByPolicy(Form form, List<MaritacaList> formLists) {
		if (form == null || form.getUser() == null || formLists == null) {
			throw new IllegalArgumentException(
					"Incomplete parameters, form permission not saved");
		}		
		deleteOldFormPermissions(form);
		
		User          owner        = this.getUser(form.getUser().getKey());
		MaritacaList  ownerList    = owner.getMaritacaList();
		MaritacaList  allUsersList = this.getAllUsersList();		

		Policy policy = form.getPolicy();
		
		FormPermissions ownerPermissions  = policy.buildOwnerFormPermission(form, ownerList);
		FormPermissions publicPermissions = policy.buildPublicFormPermission(form, allUsersList);
		
		saveFormPermission(ownerPermissions);
		saveFormPermission(publicPermissions);
		
		for(MaritacaList list : formLists){
			if(! list.equals(ownerList)){
				FormPermissions listPermissions = policy.buildListFormPermission(form, list);			
				saveFormPermission(listPermissions);
			}
		}
	}
	
	private void deleteOldFormPermissions(Form form){
		for(FormPermissions fp : getFormPermissions(form)){
			this.deleteFormPermission(fp);
		}		
	}
	
	public List<FormPermissions> getFormPermissions(Form form) {
		verifyEntity(form);

		List<FormPermissions> result = entityManager.cQuery(
				FormPermissions.class, "form", form.getKey().toString());
		for (FormPermissions fp : result) {
			MaritacaList group = this.getMaritacaList(fp.getMaritacaList().getKey());
			if(group == null){
				throw new RuntimeException(
						"Can't find MaritacaList listed in FormPermissions: " + fp.getKey());
			}
			fp.setMaritacaList(group);
		}
		return result;
	}
	
	public MaritacaList getMaritacaList(UUID uuid) {
		return entityManager.find(MaritacaList.class, uuid);
	}
	
	public void deleteFormPermission(FormPermissions formPerm) {
		verifyEntity(formPerm);
		entityManager.delete(formPerm);
	}
	
	public User getUser(UUID uuid) {
		return entityManager.find(User.class, uuid);
	}
	
	public MaritacaList getAllUsersList() {
		User root = this.getRootUser();
		for (MaritacaList g : getMaritacaListsByOwner(root)) {
			if (g.getName().equals(ManagerModel.ALL_USERS)) {
				return g;
			}
		}
		return null;
	}
	
	public User getRootUser() {
		User rootUser = null;
		for (Configuration cfUser : entityManager.cQuery(Configuration.class,
				"name", CFG_ROOT)) {
			rootUser = entityManager.find(User.class,
					UUID.fromString(cfUser.getValue()));
			break;
		}
		return rootUser;
	}
	
	public Collection<MaritacaList> getMaritacaListsByOwner(User owner) {
		verifyEntity(owner);
		return entityManager.cQuery(MaritacaList.class, "owner", owner.toString());
	}
	
	private void saveFormPermission(FormPermissions fp) {
		// verify parameters
		if (fp == null || fp.getForm() == null || fp.getMaritacaList() == null) {
			throw new IllegalArgumentException(
					"Incomplete parameters, form permission not saved");
		}

		if(fp.getKey()!=null && !currentUserHasPermission(fp.getForm(),Operation.UPDATE)){
		// Form exists and user does not have permission to update
			throw new AuthorizationDenied(Form.class, fp.getForm().getKey(), null, Operation.UPDATE);
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
	
	public <T> boolean currentUserHasPermission(T entity, Operation op) {
//		User user = getCurrentUser();
		return true; // userHasPermission(user, entity, op);
	}

	public void delete(FormPermissions fp) {
		entityManager.delete(fp);
	}
}
