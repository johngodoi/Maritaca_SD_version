package br.unifesp.maritaca.business.form.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.dao.FormEditorDAO;
import br.unifesp.maritaca.business.form.edit.dao.FormPermissionsDAO;
import br.unifesp.maritaca.business.form.list.dao.FormListerDAO;
import br.unifesp.maritaca.business.list.dao.ManagerListDAO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.AuthorizationDenied;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.persistence.permission.Permission;
import br.unifesp.maritaca.util.UtilsCore;

/**
 * 
 * @author alvaro, jimvalsan
 *
 */
@Stateless
public class FormEditorEJB extends AbstractEJB {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(FormEditorEJB.class);
	
	@Inject private FormEditorDAO formEditorDAO;
	@Inject private FormListerDAO formListerDAO;
	@Inject private ManagerListDAO managerListDAO;
	@Inject private FormPermissionsDAO formPermissionsDAO;
	@Inject private UserDAO userDAO;
	
	
	//////////////////////////////////////////////////////////////////
	@Inject private FormDAO formDAO;
	
	/**
	 * 
	 * @param formDTO
	 */
	public void saveNewForm(FormDTO formDTO) {
		//TODO: verifyEntity(formDTO.getUserKey()); verifyNullProperties(formDTO); and keyUser
		Form form = new Form();
		form.setTitle(formDTO.getTitle());
		form.setXml(formDTO.getXml());
		form.setUrl(this.getUniqueUrl());
		
		UserDTO userDTO = new UserDTO(); 
		userDTO.setKey(formDTO.getUserKey());
		User user = (User) verifyReturnNullValuesInDB(userDTO);
		
		//TODO: Change for UUID
		form.setUser(user);
		formDAO.persistForm(form);		
	}
	
	/**
	 * Get an unique url for a form
	 * 
	 * @return String
	 */
	private String getUniqueUrl() {
		// TODO: check if this random string is enough
		// maybe it is better to generate uuid-based string
		String url = UtilsCore.randomString();
		if (!formDAO.verifyIfUrlExist(url))
			return url;
		else
			return getUniqueUrl();
	}
	
	/**
	 * 
	 * @param userDTO
	 * @param formDTO
	 * @return FormDTO
	 */
	public FormDTO getFormDTOByUserDTOAndFormDTO(UserDTO userDTO, FormDTO formDTO) {
		//TODO: Verify userDTO.getKey()				
		verifyReturnNullValuesInDB(userDTO);
		Form form = (Form) verifyReturnNullValuesInDB(formDTO);
		
		Permission permission = super.getPermission(form, userDTO.getKey());
		if(permission != null) {
			formDTO = new FormDTO();
			formDTO.setKey(form.getKey());
			formDTO.setTitle(form.getTitle());
			formDTO.setUrl(form.getUrl());
			formDTO.setPolicy(form.getPolicy());
			formDTO.setPermission(permission);
			return formDTO;
		}
		else {
			throw new AuthorizationDenied(Form.class, form.getKey(), userDTO.getKey(), Operation.READ);
		}
	}
	
	/**
	 * 
	 * @param formDTO
	 */
	public void updateForm(FormDTO formDTO, UserDTO userDTO) {
		//TODO: verifyEntity(formDTO.getUserKey()); verifyNullProperties(formDTO); and keyUser
		Form originalForm = (Form) verifyReturnNullValuesInDB(formDTO);		
		User user = (User) verifyReturnNullValuesInDB(userDTO);
		
		Permission permission = super.getPermission(originalForm, userDTO.getKey());
		if(permission != null && permission.getUpdate()) {
			Form form = new Form();
			form.setKey(formDTO.getKey());
			form.setTitle(formDTO.getTitle());
			form.setXml(formDTO.getXml());
			form.setUrl(getUniqueUrl());
			form.setPolicy(originalForm.getPolicy());			
			formDAO.persistForm(form);
		}
		else {
			throw new AuthorizationDenied(Form.class, originalForm.getKey(), user.getKey(), Operation.UPDATE);
		}
	}
	
	/**
	 * 
	 * @param formDTO
	 * @param userDTO
	 */
	public void deleteForm(FormDTO formDTO, UserDTO userDTO) {
		Form originalForm = (Form) verifyReturnNullValuesInDB(formDTO);		
		User user = (User) verifyReturnNullValuesInDB(userDTO);
				
		Permission permission = super.getPermission(originalForm, userDTO.getKey());
		if(permission != null && permission.getDelete()) {
			//TODO delete answers?
			formDAO.delete(originalForm);			
		}
		else {
			throw new AuthorizationDenied(Form.class, originalForm.getKey(), user.getKey(), Operation.DELETE);
		}
	}
	
	private <T> Object verifyReturnNullValuesInDB(T obj) {
		if(obj instanceof FormDTO) {
			FormDTO formDTO = (FormDTO)obj;
			Form form = formDAO.getFormByKey(formDTO.getKey(), true);
			if(form == null) {
				throw new IllegalArgumentException("Form " +formDTO.getKey()+ " does not exist in database");
			}
			return form;
		}
		else if(obj instanceof UserDTO) {
			UserDTO userDTO = (UserDTO)obj;
			User user = userDAO.findUserByKey(userDTO.getKey());
			if(user == null) {
				throw new IllegalArgumentException("User does not exist in database");
			}
			return user;
		}
		return null;
	}
	
	
	//////////////////////////////////////////////////////////////////
	public FormDTO getFormWithPermissions(FormDTO formDTO, UserDTO userDTO) {
		Form form = new Form();
		form.setKey(formDTO.getKey());
		User user = userDAO.findUserByEmail(userDTO.getEmail());
		/*Permission permission = new Permission(
				formListerDAO.currentUserHasPermission(user, form, Operation.READ), 
				formListerDAO.currentUserHasPermission(user, form, Operation.UPDATE), 
				formListerDAO.currentUserHasPermission(user, form, Operation.SHARE), 
				formListerDAO.currentUserHasPermission(user, form, Operation.DELETE));
		formDTO.setPermission(permission);*/
		return formDTO;
	}
	
	public List<String> populateFormSharedList(FormDTO formDTO) {		
		List<String> lstItems = new ArrayList<String>();
		Form form = new Form();
		form.setKey(formDTO.getKey());
		for(FormPermissions fp : formListerDAO.getFormPermissions(form)) {
			MaritacaList list = formListerDAO.getMaritacaList(fp.getMaritacaList().getKey());
			if(!list.equals(formListerDAO.getAllUsersList())) {
				lstItems.add(list.getName());				
			}
		}
		return lstItems;
	}
	
	public String searchMaritacaListByName(String selectedItem) {
		MaritacaList list = formListerDAO.searchMaritacaListByName(selectedItem);
		if(list != null) {
			return list.getName();
		}		
		return null;
	}
	
	public Collection<MaritacaList> getMaritacaListsByPrefix(String prefix) {
		return managerListDAO.maritacaListsStartingWith(prefix);
	}
	
	public List<String> getOwnerOfMaritacaListByPrefix(String prefix) {
		List<String> listsNames = new ArrayList<String>();
		Collection<MaritacaList> lists = managerListDAO.maritacaListsStartingWith(prefix);
		for(MaritacaList gr : lists) {
			//set data of the owner
			gr.setOwner(formEditorDAO.getOwnerOfMaritacaList(gr));
			listsNames.add(gr.getName());
		}	
		return listsNames;
	}
	
	public boolean updateFormFromPolicyEditor(FormDTO formDTO, UserDTO userDTO, List<String> usedItems) {		
		Form form = new Form();
		form.setKey(formDTO.getKey());
		form.setTitle(formDTO.getTitle());
		form.setXml(formDTO.getXml());
		form.setUrl(getUniqueUrl());
		form.setPolicy(formDTO.getPolicy());
		// check permissions for updating
		Form originalForm = formEditorDAO.getForm(form.getKey(), true);
		if(!formPermissionsDAO.currentUserHasPermission(originalForm, Operation.UPDATE)){
			throw new AuthorizationDenied(Form.class, formDTO.getKey(), userDTO.getKey(), Operation.UPDATE);
		}
		if( formEditorDAO.verifyIfUserExist(userDTO.getKey()) ){
			form.setUser(userDAO.findUserByKey(userDTO.getKey()));
			formEditorDAO.persistForm(form);
			// save permissions of a form
			formPermissionsDAO.saveFormPermissionsByPolicy(form, getFormLists(usedItems));
			return true;
		} else {			
			throw new IllegalArgumentException("User does not exist in database");
		}
	}
	
	private List<MaritacaList> getFormLists(List<String> usedItems) {
		List<MaritacaList> lists = new ArrayList<MaritacaList>();
		for(String listName : usedItems) {
			MaritacaList list = formListerDAO.searchMaritacaListByName(listName);
			lists.add(list);
		}
		return lists;
	}
	
	//
	
		

			
}