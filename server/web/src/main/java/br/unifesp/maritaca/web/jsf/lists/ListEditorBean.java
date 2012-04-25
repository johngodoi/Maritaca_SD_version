package br.unifesp.maritaca.web.jsf.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.list.edit.ListEditorEJB;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

/**
 * Bean responsible for managing user groups.
 * 
 * @author tiagobarabasz
 */
@ManagedBean
@ViewScoped
public class ListEditorBean extends MaritacaJSFBean {
			
	@Inject
	private ListEditorEJB         listEditorEJB;
	private ItemListUsersBean     usersList;
	private MaritacaListDTO       listDto;

//	private static final Log log = LogFactory.getLog(MaritacaListEditorBean.class);
	/* Error messages resources */
	/*
	private static final String LIST_ADD_ERROR_USER_NOT_FOUND = "list_add_error_user_not_found";
	private static final String LIST_ADD_ERROR_USER_ADDED     = "list_add_error_user_added";	
	private static final String LIST_REMOVE_OWNER_ERROR       = "list_remove_owner_error";
	private static final String LIST_ADD_EMPTY_EMAIL          = "item_list_add_empty_field";	
	private static final String LIST_ADD_FAILURE              = "list_add_fail";
	*/
	private static final String LIST_ADD_SUCESS               = "list_add_sucess";	
	private static final long   serialVersionUID              = 1L;
	
	public ListEditorBean() {
	}
	
	/**
	 * Method used to clear the group information.<br>
	 * It must be called when the createGroup sub module is invoked.
	 */
	@PostConstruct
	public void clearList() {
		setUsersList(new ItemListUsersBean());
		getUsersList().setCurrentUserKey(getCurrentUser().getKey());
		getUsersList().setListEditorEJB(listEditorEJB);
		setListDto(new MaritacaListDTO());
	}

	/**
	 * Checks if there is one group with the same name as the one being created
	 * that is owned by the logged user.<br>
	 * If the group is being edited, its allowed to keep its name and this
	 * function returns false in case the same name is used.
	 * 
	 * @return true if there is, false otherwise
	 */
	//TODO: Improve this method with the event(onchange)
	public boolean registeredListName() {
		return false;
	}

	public String editList(MaritacaListDTO listDto) {
		getModuleManager().activeModAndSub("lists", "listEditor");
		setListDto(listDto);
		
		List<UserDTO> users;
		users = listEditorEJB.searchUsersByMaritacaList(listDto.getKey());
		getUsersList().setUsedItens(users);
		
		return null;
	}

	/**
	 * Save the group being created. Reloads the page if an error occurs or go
	 * to the home page otherwise.
	 * 
	 * @return Navigation string
	 */
	public String save() {
		String       returnString = null;
		
		List<UUID> lists = new ArrayList<UUID>();
		for(UserDTO listDto : usersList.getUsedItens()){
			lists.add(listDto.getKey());
		}		
		listDto.setUsers(lists);		
		listDto.setOwner(getCurrentUser().getKey());
		listEditorEJB.saveMaritacaList(listDto);
		addMessageInfo(LIST_ADD_SUCESS);
		getModuleManager().setActiveModuleByString("Lists");
		getModuleManager().setActiveSubModuleInActiveMod("myLists");

		clearList();
		return returnString;
	}

	public String cancel() {
		return MaritacaConstants.FACES_HOME;
	}
	
	public MaritacaListDTO getListDto() {
		return listDto;
	}

	public void setListDto(MaritacaListDTO listDto) {
		this.listDto = listDto;
	}

	public ItemListUsersBean getUsersList() {
		return usersList;
	}

	public void setUsersList(ItemListUsersBean usersList) {
		this.usersList = usersList;
	}
}