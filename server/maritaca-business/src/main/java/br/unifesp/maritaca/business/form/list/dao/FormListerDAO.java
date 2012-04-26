package br.unifesp.maritaca.business.form.list.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.unifesp.maritaca.access.AccessLevel;
import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.AuthorizationDenied;
import br.unifesp.maritaca.model.ManagerModel;

public class FormListerDAO extends BaseDAO {
	
	public List<Form> getListOwnFormsByUserKey(String key) {
		return entityManager.cQuery(Form.class, "user", key, false);		
	}
	
	public List<Form> getListSharedFormsByUserKey(User user) {
		return null;
	}
		
	public Collection<FormPermissions> getFormPermissionsByList(MaritacaList list) {
		List<FormPermissions> result = entityManager.cQuery(FormPermissions.class, "maritacaList", list.getKey().toString());
		for (FormPermissions fp : result) {
			fp.setMaritacaList(list);
		}
		return result;
	}
		
	public Form getForm(UUID uid, boolean minimal, User user) {
		if (uid == null ) {
			throw new IllegalArgumentException("Incomplete parameters");
		}
		Form form = entityManager.find(Form.class, uid, minimal);
		if(userHasPermission(user, form, Operation.READ)){
			return form;	
		} else {
			throw new AuthorizationDenied(Form.class, form.getKey(), user.getKey(), Operation.READ);
		}		
	}
	
	public <T> boolean userHasPermission(User user, T entity, Operation op) {
		if (user == null || op == null)
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
		//Get permissions of form
		List<FormPermissions> listFP = getFormPermissions(form);
		for (FormPermissions fp : listFP) {
			if (fp.getFormAccess().isOperationEnabled(op)
					&& userIsMemberOfMaritacaList(user, fp.getMaritacaList())) {
				// if operation OP is enable in FormAccess and current user
				// is member of list fp.getlist, so he/she has permission
				return true;
			}
		}
		return false;
	}
	
	public List<FormPermissions> getFormPermissions(Form form) {
		List<FormPermissions> result = entityManager.cQuery(FormPermissions.class, "form", form.getKey().toString());
		for (FormPermissions fp : result) {
			MaritacaList grp = getMaritacaList(fp.getMaritacaList().getKey());
			if(grp == null){
				throw new RuntimeException(
						"Can't find MaritacaList listed in FormPermissions: " + fp.getKey());
			}
			fp.setMaritacaList(grp);
		}
		return result;
	}
	
	public MaritacaList getMaritacaList(UUID uuid) {
		return entityManager.find(MaritacaList.class, uuid);
	}
	
	public boolean userIsMemberOfMaritacaList(User user, MaritacaList group) {
		return false;// not a member
	}
	
	private boolean userHasPermissionInFormPermissions(User user,
			FormPermissions formPerm, Operation op) {
		MaritacaList allUsers = getAllUsersList();
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
	
	public MaritacaList getAllUsersList() {
		User root = getRootUser();
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
				"name", "root")) {
			rootUser = entityManager.find(User.class, UUID.fromString(cfUser.getValue()));
			break;
		}
		return rootUser;
	}
	
	public Collection<MaritacaList> getMaritacaListsByOwner(User owner) {
		return entityManager.cQuery(MaritacaList.class, "owner", owner.toString());
	}

	public <T> boolean currentUserHasPermission(User user, T entity, Operation op) {
		return userHasPermission(user, entity, op);
	}
	
	public MaritacaList searchMaritacaListByName(String groupName) {
		if (entityManager == null || groupName == null)
			return null;

		List<MaritacaList> foundGroups = entityManager.cQuery(MaritacaList.class, "name",
				groupName);

		if (foundGroups.size() == 0) {
			return null;
		} else if (foundGroups.size() == 1) {
			return foundGroups.get(0);
		} else {
//			throw new InvalidNumberOfEntries(groupName, MaritacaList.class);
			return null;
		}
	}
}