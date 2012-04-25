package br.unifesp.maritaca.business.form.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.dao.FormEditorDAO;
import br.unifesp.maritaca.business.form.list.dao.FormListerDAO;
import br.unifesp.maritaca.business.list.dao.ManagerListDAO;
import br.unifesp.maritaca.core.Form;
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
		
	@Inject private FormListerDAO formListerDAO;
	@Inject private ManagerListDAO managerListDAO;
	
	//////////////////////////////////////////////////////////////////
	@Inject private FormDAO formDAO;
	@Inject private FormEditorDAO formEditorDAO;
	@Inject private UserDAO userDAO;
	
	/**
	 * Save a new form, by default the policy is PRIVATE
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
		
		form.setUser(user);
		form.setLists(getOwnMaritacaListByUser(user));
		formDAO.persistForm(form);	
	}
	
	/**
	 * Get the MaritacaList for the current user
	 * @param user
	 * @return List<UUID>
	 */
	private List<UUID> getOwnMaritacaListByUser(User user) {
		List<UUID> uuids = new ArrayList<UUID>();
		List<MaritacaList> mLists = (List<MaritacaList>) formDAO.getMaritacaListsByOwner(user.getKey());
		if(mLists != null && !mLists.isEmpty()) {
			for(MaritacaList mList : mLists) {
				if(mList.getName().equals(user.getEmail()))
					uuids.add(mList.getKey());
			}
		}		
		return uuids;
	}
	
	/**
	 * Get an unique URL for a form 
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
	 * Get a FormDTO
	 * @param userDTO
	 * @param formDTO
	 * @return FormDTO
	 */
	public FormDTO getFormDTOByUserDTOAndFormDTO(FormDTO formDTO, UserDTO userDTO) {
		//TODO: Verify userDTO.getKey()				
		verifyReturnNullValuesInDB(userDTO);
		//Form form = (Form) verifyReturnNullValuesInDB(formDTO);
		Form form = formDAO.getFormByKey(formDTO.getKey(), false);
		
		Permission permission = super.getPermission(form, userDTO.getKey());
		if(permission != null) {
			formDTO = new FormDTO();
			formDTO.setKey(form.getKey());
			formDTO.setTitle(form.getTitle());
			formDTO.setUrl(form.getUrl());
			formDTO.setXml(form.getXml());
			formDTO.setPolicy(form.getPolicy());
			formDTO.setPermission(permission);
			return formDTO;
		}
		else {
			throw new AuthorizationDenied(Form.class, form.getKey(), userDTO.getKey(), Operation.READ);
		}
	}
	
	/**
	 * Update a Form
	 * @param formDTO
	 * @param userDTO
	 */
	public void updateForm(FormDTO formDTO, UserDTO userDTO) {
		//TODO: verifyEntity(formDTO.getUserKey()); verifyNullProperties(formDTO); and keyUser
		Form originalForm = (Form) verifyReturnNullValuesInDB(formDTO);
		User user = (User) verifyReturnNullValuesInDB(userDTO);
		
		Permission permission = super.getPermission(originalForm, userDTO.getKey());
		if(permission != null && permission.getUpdate()) {
			Form form = new Form();
			form.setKey(formDTO.getKey());
			form.setUser(user);
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
	 * Delete a Form
	 * @param formDTO
	 * @param userDTO
	 */
	//TODO delete answers?
	public void deleteForm(FormDTO formDTO, UserDTO userDTO) {
		//Form originalForm = (Form) verifyReturnNullValuesInDB(formDTO);
		User user = (User) verifyReturnNullValuesInDB(userDTO);
		Form originalForm = formDAO.getFormByKey(formDTO.getKey(), false);
		
		Permission permission = super.getPermission(originalForm, userDTO.getKey());
		if(permission != null && permission.getDelete()) {
			formDAO.delete(originalForm);
			formEditorDAO.deleteFormAccessible(originalForm, user);//
		}
		else {
			throw new AuthorizationDenied(Form.class, originalForm.getKey(), user.getKey(), Operation.DELETE);
		}
	}
	
	/**
	 * 
	 * @param obj
	 * @return Form or User
	 */
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
	
	/**
	 * Update the Form with the new Policy
	 * @param formDTO
	 * @param userDTO
	 * @param usedItems
	 * @return
	 */
	public Boolean updateFormFromPolicyEditor(FormDTO formDTO, UserDTO userDTO, List<String> usedItems) {		
		//Form originalForm = (Form) verifyReturnNullValuesInDB(formDTO);//
		Form originalForm = formDAO.getFormByKey(formDTO.getKey(), false);
		User user = (User) verifyReturnNullValuesInDB(userDTO);
		Form form = new Form();//TODO: new ?
		form.setKey(formDTO.getKey());
		form.setUser(user);
		form.setTitle(formDTO.getTitle());
		form.setXml(formDTO.getXml());
		form.setUrl(getUniqueUrl());
		form.setPolicy(formDTO.getPolicy());
		form.setLists(getListsForForm(usedItems));
		
		Permission permission = super.getPermission(originalForm, userDTO.getKey());
		if(permission != null && permission.getUpdate()) {
			formDAO.persistForm(form);
			formEditorDAO.createOrUpdateFormAccessible(form, user);//
			return true;
		}
		else {
			throw new AuthorizationDenied(Form.class, originalForm.getKey(), user.getKey(), Operation.UPDATE);
		}
	}
	
	/**
	 * Get the lists for a form
	 * @param usedItems
	 * @return
	 */
	private List<UUID> getListsForForm(List<String> usedItems) {
		List<UUID> uuids = new ArrayList<UUID>();
		for(String s : usedItems) {
			List<MaritacaList> mLists = new ArrayList<MaritacaList>(formDAO.getMaritacaListsByName(s));
			if(mLists != null && !mLists.isEmpty()) {
				uuids.add(mLists.get(0).getKey());//TODO: Fix
			}
		}
		return uuids;
	}
	
	//MaritacaList > autocomplete
	//////////////////////////////////////////////////////////////////
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
	
	public String searchMaritacaListByName(String selectedItem) {
		MaritacaList list = formListerDAO.searchMaritacaListByName(selectedItem);
		if(list != null) {
			return list.getName();
		}		
		return null;
	}
	
	public List<String> populateFormSharedList(FormDTO formDTO) {
		List<String> lstItems = new ArrayList<String>();
		Form form = formDAO.getFormByKey(formDTO.getKey(), false);		
		if(form != null && form.getLists() != null && !form.getLists().isEmpty()) {
			for(UUID uuid : form.getLists()) {
				MaritacaList mList = managerListDAO.getMaritacaList(uuid);
				if(mList != null && !mList.getName().equals(MaritacaConstants.ALL_USERS)) {
					lstItems.add(mList.getName());
				}
			}
		}
		return lstItems;
	}

	/////TODO: managerListDAO, formListerDAO
	public Collection<MaritacaList> getMaritacaListsByPrefix(String prefix) {
		return managerListDAO.maritacaListsStartingWith(prefix);
	}	
	
	private List<MaritacaList> getFormLists(List<String> usedItems) {
		List<MaritacaList> lists = new ArrayList<MaritacaList>();
		for(String listName : usedItems) {
			MaritacaList list = formListerDAO.searchMaritacaListByName(listName);
			lists.add(list);
		}
		return lists;
	}	
}