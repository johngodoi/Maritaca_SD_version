package br.unifesp.maritaca.web.jsf.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.list.ManagerListEJB;
import br.unifesp.maritaca.business.list.dto.ListDTO;
import br.unifesp.maritaca.business.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.jsf.util.MaritacaConstants;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Bean responsible for managing user groups.
 * 
 * @author tiagobarabasz
 */
@ManagedBean
@ViewScoped
public class MaritacaListEditorBean extends MaritacaJSFBean {

	private static final Log log = LogFactory.getLog(MaritacaListEditorBean.class);
	
	@Inject ManagerListEJB managerListEJB;
	
	private ListDTO listDTO;
	
	public MaritacaListEditorBean() {
	}
	
	/* Error messages resources */
	private static final String LIST_ADD_ERROR_USER_NOT_FOUND = "list_add_error_user_not_found";
	private static final String LIST_ADD_ERROR_USER_ADDED     = "list_add_error_user_added";	
	private static final String LIST_REMOVE_OWNER_ERROR       = "list_remove_owner_error";
	private static final String LIST_ADD_EMPTY_EMAIL          = "item_list_add_empty_field";
	private static final String LIST_ADD_SUCESS               = "list_add_sucess";
	private static final String LIST_ADD_FAILURE              = "list_add_fail";
	
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	@ManagedProperty("#{manager}")
	private Manager manager;

	@Size(min = 4, max = 20)
	private String listName;
	private String listDescription;

	private String ignoreListName;

	private UUID listId;

	private Boolean allowUserJoin;
	private List<String> autoCompleteEmails;
	private List<User> addedUsers;

	//The email field can be empty
	@Pattern(regexp="("+MaritacaConstants.EMAIL_REG_EXP+")|^$")
	private String selectedEmail;
	private String addEmailError;
	
	/**
	 * Method used to clear the group information.<br>
	 * It must be called when the createGroup sub module is invoked.
	 */
	@PostConstruct
	public void clearList() {
		setAutoCompleteEmails(new ArrayList<String>());
		setAllowUserJoin(true);
		setAddedUsers(new ArrayList<User>());
		setIgnoreListName(null);
		setListName(null);
		setListDescription(null);
		setSelectedEmail(null);
		setAddEmailError(null);
		if(getCurrentUserBean()!=null) {
			User user = new User();
			user.setKey(getCurrentUser().getKey());
			user.setEmail(getCurrentUser().getEmail());
			getAddedUsers().add(user);
		}
	}

	/**
	 * Checks if there is one group with the same name as the one being created
	 * that is owned to the logged user.<br>
	 * If the group is being edited, its allowed to keep its name and this
	 * function returns false in case the same name is used.
	 * 
	 * @return true if there is, false otherwise
	 */
	//TODO: Improve this method with the event(onchange)
	public boolean registeredListName() {
		MaritacaList maritacaList = managerListEJB.searchMaritacaListByName(getListName());
		if (!listNameUsed(maritacaList) || !listOwnerIsCurrentUser(maritacaList)
				|| listNameIgnored(maritacaList)) {
			return false;
		} else {
			return true;
		}
	}

	private boolean listNameIgnored(MaritacaList list) {
		if (getIgnoreListName() != null
				&& list.getName().equals(getIgnoreListName())) {
			return true;
		} else {
			return false;
		}
	}

	public String editList(MaritacaListDTO list) {
		fillListInfo(list);
		setIgnoreListName(list.getName());
		getManager().activeModAndSub("lists", "listEditor");
		return null;
	}

	private void fillListInfo(MaritacaListDTO list) {
		setListName(list.getName());
		setListDescription(list.getDescription());
		setListId(list.getKey());

		List<User> users = new ArrayList<User>(managerListEJB.searchUsersByMaritacaList(list));
		setAddedUsers(users);
	}

	private boolean listNameUsed(MaritacaList list) {
		if (list == null) {
			return false;
		} else {
			return true;
		}
	}

	private boolean listOwnerIsCurrentUser(MaritacaList list) {
		UUID ownerKey = list.getOwner().getKey();
		/*UUID userKey = getCurrentUserBean().getUser().getKey();
		if (ownerKey.equals(userKey)) {
			return true;
		} else {
			return false;
		}*/return false;
	}

	public void addEmail() {
		if (getSelectedEmail() == null || getSelectedEmail().equals("")) {
			setAddEmailError(Utils
					.getMessageFromResourceProperties(LIST_ADD_EMPTY_EMAIL));
			return;
		}

		if (emailAdded(getSelectedEmail())) {
			// Email already added
			setAddEmailError(Utils
					.getMessageFromResourceProperties(LIST_ADD_ERROR_USER_ADDED));
			return;
		}

		User selectedUser = findUserByEmail(getSelectedEmail());
		if (selectedUser != null) {
			// Sucess!
			clearAddEmailError();
			getAddedUsers().add(selectedUser);
		} else {
			setAddEmailError(Utils
					.getMessageFromResourceProperties(LIST_ADD_ERROR_USER_NOT_FOUND));
		}
	}

	private boolean emailAdded(String selectedEmail) {
		for (User u : getAddedUsers()) {
			if (u.getEmail().equals(selectedEmail)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of strings containing the emails from the users being
	 * added to the group.
	 * 
	 * @return
	 */
	public List<String> getAddedEmailsList() {
		List<String> addedEmails = new ArrayList<String>();
		for (User u : getAddedUsers()) {
			addedEmails.add(u.getEmail());
		}
		return addedEmails;
	}

	private User findUserByEmail(String selectedEmail) {
		return managerListEJB.findUserByEmail(selectedEmail);
	}

	public void removeEmail(String email) {
		clearAddEmailError();
		
		String ownerUsrEmail = getCurrentUser().getEmail();				
		if(email.equals(ownerUsrEmail)){
			setAddEmailError(Utils
					.getMessageFromResourceProperties(LIST_REMOVE_OWNER_ERROR));
			return;
		}
		if (emailAdded(email)) {
			for (User u : getAddedUsers()) {
				if (u.getEmail().equals(email)) {
					getAddedUsers().remove(u);
					return;
				}
			}
		}
	}

	private void clearAddEmailError() {
		setAddEmailError("");
	}

	public List<String> usersStartingWith(String startingString) {
		List<String> userEmails = new ArrayList<String>();
		for (User u : managerListEJB.usersStartingWith(startingString)) {
			userEmails.add(u.getEmail());
		}
		setAutoCompleteEmails(userEmails);
		return userEmails;
	}

	/**
	 * Save the group being created. Reloads the page if an error occurs or go
	 * to the home page otherwise.
	 * 
	 * @return Navigation string
	 */
	public String save() {
		String    returnString = null;
		MaritacaList list = createListObj();
		if (managerListEJB.saveMaritacaList(list) && managerListEJB.saveListUsers(list, getAddedUsers())) {
			addMessageInfo(LIST_ADD_SUCESS);
			getManager().setActiveModuleByString("Lists");
			getManager().setActiveSubModuleInActiveMod("myLists");
		} else {
			addMessageError(LIST_ADD_FAILURE);
			log.error("Error saving list: " + list.toString());			
		}

		clearList();
		return returnString;
	}

	private MaritacaList createListObj() {
		MaritacaList newList = new MaritacaList();
		if(getListId()!=null){
			newList.setKey(getListId());
		}
		newList.setName(getListName());
		newList.setDescription(getListDescription());
		newList.setOwner(managerListEJB.findUserByEmail(getCurrentUser().getEmail()));

		return newList;
	}

	public String cancel() {
		return MaritacaConstants.FACES_HOME;
	}

	public CurrentUserBean getCurrentUserBean() {
		return currentUserBean;
	}

	public void setCurrentUserBean(CurrentUserBean currentUserBean) {
		this.currentUserBean = currentUserBean;
	}

	public Boolean getAllowUserJoin() {
		return allowUserJoin;
	}

	public void setAllowUserJoin(Boolean allowUserJoin) {
		this.allowUserJoin = allowUserJoin;
	}

	public List<String> getAutoCompleteEmails() {
		return autoCompleteEmails;
	}

	public void setAutoCompleteEmails(List<String> autoCompleteEmails) {
		this.autoCompleteEmails = autoCompleteEmails;
	}

	public String getSelectedEmail() {
		return selectedEmail;
	}

	public void setSelectedEmail(String selectedEmail) {
		this.selectedEmail = selectedEmail;
	}

	public List<User> getAddedUsers() {
		return addedUsers;
	}

	public void setAddedUsers(List<User> addedUsers) {
		this.addedUsers = addedUsers;
	}

	public String getAddEmailError() {
		return addEmailError;
	}

	public void setAddEmailError(String addEmailError) {
		this.addEmailError = addEmailError;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String groupName) {
		this.listName = groupName;
	}

	public String getListDescription() {
		return listDescription;
	}

	public void setListDescription(String groupDescription) {
		this.listDescription = groupDescription;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public String getIgnoreListName() {
		return ignoreListName;
	}

	public void setIgnoreListName(String ignoreGroupName) {
		this.ignoreListName = ignoreGroupName;
	}

	public UUID getListId() {
		return listId;
	}

	public void setListId(UUID listId) {
		this.listId = listId;
	}
}
