package br.unifesp.maritaca.business.form.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.exception.AuthorizationDenied;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.dao.FormEditorDAO;
import br.unifesp.maritaca.business.list.list.dao.ListMaritacaListDAO;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.business.util.ConstantsBusiness;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.permission.Document;
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
	private boolean createAnswers = true;
	
	@Inject private ListMaritacaListDAO listMaritacaListDAO;
	@Inject private UserDAO userDAO;
	@Inject private FormDAO formDAO;
	@Inject private FormEditorDAO formEditorDAO;
	
	
	/** 
	 * Save a new form, by default the policy is PRIVATE, it has not any list
	 * @param formDTO
	 */
	public void saveNewForm(FormDTO formDTO) {		
		Form form = UtilsBusiness.convertToClass(formDTO, Form.class);
		form.setUrl(this.getUniqueUrl());	

		UserDTO userDTO = new UserDTO(); 
		userDTO.setKey(formDTO.getUser());
		User user = (User) verifyReturnNullValuesInDB(userDTO);		
		form.setUser(user);
		
		formDAO.persistForm(form);
		formDTO.setKey(form.getKey());
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
		
		Permission permission = super.getPermission(form, userDTO.getKey(), Document.FORM);
		if(permission != null && permission.getRead()) {
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
			throw new AuthorizationDenied(Document.FORM, form.getKey(), userDTO.getKey(), Operation.READ);
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
		
		Permission permission = super.getPermission(originalForm, userDTO.getKey(), Document.FORM);
		if(permission != null && permission.getUpdate()) {
			Form form = new Form();
			form.setKey(formDTO.getKey());
			form.setUser(user);
			form.setTitle(formDTO.getTitle());
			form.setXml(formDTO.getXml());
			//form.setUrl(getUniqueUrl());
			form.setPolicy(originalForm.getPolicy());			
			formDAO.persistForm(form);
		}
		else {
			throw new AuthorizationDenied(Document.FORM, originalForm.getKey(), user.getKey(), Operation.UPDATE);
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
		
		Permission permission = super.getPermission(originalForm, userDTO.getKey(), Document.FORM);
		if(permission != null && permission.getDelete()) {
			formDAO.delete(originalForm);
			if(originalForm.getLists()!=null){
				formEditorDAO.deleteFormAccessible(originalForm, user);//
			}
		}
		else {
			throw new AuthorizationDenied(Document.FORM, originalForm.getKey(), user.getKey(), Operation.DELETE);
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
	public Boolean updateFormFromPolicyEditor(FormDTO formDTO, UserDTO userDTO, List<MaritacaListDTO> usedItems) {		
		//Form originalForm = (Form) verifyReturnNullValuesInDB(formDTO);//
		Form originalForm = formDAO.getFormByKey(formDTO.getKey(), false);
		User user = (User) verifyReturnNullValuesInDB(userDTO);
		List<UUID> originalLists = new ArrayList<UUID>();
		if(originalForm.getLists() != null) {
			originalLists.addAll(originalForm.getLists());
		}
		originalForm.setPolicy(formDTO.getPolicy());
		if(originalForm.isShared()) {
			originalForm.setLists(fetchKeysFromLists(usedItems));
		}
		Permission permission = super.getPermission(originalForm, userDTO.getKey(), Document.FORM);
		if(permission != null && permission.getUpdate()) {
			formDAO.persistForm(originalForm);
			if(originalForm.isShared()) {
				formEditorDAO.createOrUpdateFormAccessible(originalForm, user, checkDeletedLists(originalLists, originalForm.getLists()));
			}
			return true;
		}
		else {
			throw new AuthorizationDenied(Document.FORM, originalForm.getKey(), user.getKey(), Operation.UPDATE);
		}		
	}
	
	private List<UUID> checkDeletedLists(List<UUID> currentLists, List<UUID> newLists) {
		List<UUID> deletedLists = new ArrayList<UUID>();
		for(UUID current : currentLists) {
			if(!newLists.contains(current)) {
				deletedLists.add(current);
			}
		}
		return deletedLists;
	}
	
	//MaritacaList > autocomplete
	//////////////////////////////////////////////////////////////////
	public List<MaritacaListDTO> getOwnerOfMaritacaListByPrefix(String prefix) {
		List<MaritacaListDTO>    listsDto = new ArrayList<MaritacaListDTO>();
		Collection<MaritacaList> lists    = listMaritacaListDAO.maritacaListsStartingWith(prefix);
		for(MaritacaList list : lists) {
			listsDto.add(UtilsBusiness.convertToClass(list, MaritacaListDTO.class));
		}	
		return listsDto;
	}
	
	public MaritacaListDTO searchMaritacaListByName(String selectedItem) {
		MaritacaList list = formEditorDAO.searchMaritacaListByName(selectedItem);
		if(list != null) {
			return UtilsBusiness.convertToClass(list, MaritacaListDTO.class);
		}		
		return null;
	}
		
	public List<MaritacaListDTO> populateFormSharedList(FormDTO formDTO) {
		List<MaritacaListDTO> lstItems = new ArrayList<MaritacaListDTO>();
		Form form = formDAO.getFormByKey(formDTO.getKey(), false);
		//TODO: Lists should be only those where the owner is the current user
		//User owner = userDAO.findUserByKey(form.getUser().getKey()); 
		if(form.getLists() != null) {
			for(UUID id : form.getLists()) {
				MaritacaList mList = listMaritacaListDAO.getMaritacaListById(id);
				if(mList != null) {
					lstItems.add(UtilsBusiness.convertToClass(mList, MaritacaListDTO.class));
				}					
			}
		}
		return lstItems;
	}

	private List<UUID> fetchKeysFromLists(List<MaritacaListDTO> usedLists) {
		List<UUID> uuids = new ArrayList<UUID>();
		for(MaritacaListDTO list : usedLists) {			
			uuids.add(list.getKey());			
		}
		return uuids;
	}
	
	//
	public Collection<MaritacaList> getMaritacaListsByPrefix(String prefix) {
		return listMaritacaListDAO.maritacaListsStartingWith(prefix);
	}

	public ListMaritacaListDAO getListMaritacaListDAO() {
		return listMaritacaListDAO;
	}

	public void setListMaritacaListDAO(ListMaritacaListDAO listMaritacaListDAO) {
		this.listMaritacaListDAO = listMaritacaListDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public FormDAO getFormDAO() {
		return formDAO;
	}

	public FormDTO getFormDTOById(UUID uuid, UserDTO userDTO) {
		FormDTO formDTO = null;
		Form form = formDAO.getFormByKey(uuid, false);
		if(form != null) {			
			Permission permission = super.getPermission(form, form.getUser().getKey(), Document.FORM);
			if(permission != null && permission.getRead()) {
				formDTO = new FormDTO();
				formDTO.setKey(form.getKey());
				formDTO.setTitle(form.getTitle());
				formDTO.setUrl(form.getUrl());
				formDTO.setXml(form.getXml());
			}
			else {
				//TODO:
			}
		}
		return formDTO;
	}

	public FormDTO getFormDTOFromUrl(String url) {
		FormDTO formDTO = null;
		Form form = formDAO.findFormByUrl(url);
		if(form != null) {			
			Permission permission = super.getPermission(form, form.getUser().getKey(), Document.FORM);
			if(permission != null && permission.getRead()) {
				formDTO = new FormDTO();
				formDTO.setKey(form.getKey());
				formDTO.setTitle(form.getTitle());
				formDTO.setUrl(form.getUrl());
				formDTO.setXml(form.getXml());
			}
			else {
				//TODO:
			}
		}
		return formDTO;
	}
	
	public void setFormDAO(FormDAO formDAO) {
		this.formDAO = formDAO;
	}

	public FormEditorDAO getFormEditorDAO() {
		return formEditorDAO;
	}

	public void setFormEditorDAO(FormEditorDAO formEditorDAO) {
		this.formEditorDAO = formEditorDAO;
	}

	public boolean isCreateAnswers() {
		return createAnswers;
	}

	public void setCreateAnswers(boolean createAnswers) {
		this.createAnswers = createAnswers;
	}	
}

