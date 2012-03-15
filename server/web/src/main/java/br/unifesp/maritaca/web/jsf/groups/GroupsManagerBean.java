package br.unifesp.maritaca.web.jsf.groups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Bean responsible for managing user groups.
 * 
 * @author tiagobarabasz
 */
@ManagedBean
@ViewScoped
public class GroupsManagerBean extends AbstractBean implements Serializable {

	private static final Log log = LogFactory.getLog(GroupsManagerBean.class);
	
	/* Error messages resources */
	private static final String GROUP_ADD_ERROR_USER_NOT_FOUND = "group_add_error_user_not_found";
	private static final String GROUP_ADD_ERROR_USER_ADDED     = "group_add_error_user_added";
	private static final String GROUP_ADD_EMPTY_EMAIL          = "item_list_add_empty_field";
	private static final String GROUP_ADD_SUCESS               = "group_add_sucess";
	private static final String GROUP_ADD_FAILURE              = "group_add_fail";
	
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	@ManagedProperty("#{manager}")
	private Manager manager;

	@Size(min = 4, max = 20)
	private String groupName;
	private String groupDescription;

	private String ignoreGroupName;

	private UUID groupId;

	private Boolean allowUserJoin;
	private List<String> autoCompleteEmails;
	private List<User> addedUsers;

	//The email field can be empty
	@Pattern(regexp = "("+Utils.EMAIL_REG_EXP+")|^$", message="{email.invalid}")
	private String selectedEmail;
	private String addEmailError;

	public GroupsManagerBean() {
		super(false, true);
		clearGroup();
	}

	/**
	 * Method used to clear the group information.<br>
	 * It must be called when the createGroup sub module is invoked.
	 */
	public void clearGroup() {
		setAutoCompleteEmails(new ArrayList<String>());
		setAllowUserJoin(true);
		setAddedUsers(new ArrayList<User>());
		setIgnoreGroupName(null);
		setGroupName(null);
		setGroupDescription(null);
		setSelectedEmail(null);
		setAddEmailError(null);
	}

	/**
	 * Checks if there is one group with the same name as the one being created
	 * that is owned to the logged user.<br>
	 * If the group is being edited, its allowed to keep its name and this
	 * function returns false in case the same name is used.
	 * 
	 * @return true if there is, false otherwise
	 */
	public boolean registeredGroupName() {
		UserModel userModel = super.userCtrl;
		Group group = userModel.searchGroupByName(getGroupName());

		if (!groupNameUsed(group) || !groupOwnerIsCurrentUser(group)
				|| groupNameIgnored(group)) {
			return false;
		} else {
			return true;
		}
	}

	private boolean groupNameIgnored(Group group) {
		if (getIgnoreGroupName() != null
				&& group.getName().equals(getIgnoreGroupName())) {
			return true;
		} else {
			return false;
		}
	}

	public String editGroup(Group group) {
		fillGroupInfo(group);
		setIgnoreGroupName(group.getName());
		getManager().activeModAndSub("groups", "groupEditor");
		return null;
	}

	private void fillGroupInfo(Group group) {
		setGroupName(group.getName());
		setGroupDescription(group.getDescription());
		setAllowUserJoin(group.getAllowUsersToJoin());
		setGroupId(group.getKey());

		List<User> users = new ArrayList<User>(
				super.userCtrl.searchUsersByGroup(group));
		setAddedUsers(users);
	}

	private boolean groupNameUsed(Group group) {
		if (group == null) {
			return false;
		} else {
			return true;
		}
	}

	private boolean groupOwnerIsCurrentUser(Group group) {
		UUID ownerKey = group.getOwner().getKey();
		UUID userKey = getCurrentUserBean().getUser().getKey();
		if (ownerKey.equals(userKey)) {
			return true;
		} else {
			return false;
		}
	}

	public void addEmail() {
		if (getSelectedEmail() == null || getSelectedEmail().equals("")) {
			setAddEmailError(Utils
					.getMessageFromResourceProperties(GROUP_ADD_EMPTY_EMAIL));
			return;
		}

		if (emailAdded(getSelectedEmail())) {
			// Email already added
			setAddEmailError(Utils
					.getMessageFromResourceProperties(GROUP_ADD_ERROR_USER_ADDED));
			return;
		}

		User selectedUser = findUserByEmail(getSelectedEmail());
		if (selectedUser != null) {
			// Sucess!
			clearAddEmailError();
			getAddedUsers().add(selectedUser);
		} else {
			setAddEmailError(Utils
					.getMessageFromResourceProperties(GROUP_ADD_ERROR_USER_NOT_FOUND));
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
		return super.userCtrl.findUserByEmail(selectedEmail);
	}

	public void removeEmail(String email) {
		clearAddEmailError();
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
		UserModel userModel = super.userCtrl;
		List<String> userEmails = new ArrayList<String>();

		for (User u : userModel.usersStartingWith(startingString)) {
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
		Boolean   success      = true;
		UserModel userModel    = super.userCtrl;

		Group group = createGroupObj();
		if (userModel.saveGroup(group)) {
			if(!newGroup(group)){
				success = updateGroupUsers(group);
			}

			if (success) {
				addMessage(GROUP_ADD_SUCESS, FacesMessage.SEVERITY_INFO);
				returnString = "/faces/views/home";
			} else {
				addMessage(GROUP_ADD_FAILURE, FacesMessage.SEVERITY_ERROR);
				log.error("Error saving group: " + group.toString());
			}
		} else {
			addMessage(GROUP_ADD_FAILURE, FacesMessage.SEVERITY_ERROR);
			log.error("Error saving group: " + group.toString());			
		}

		clearGroup();
		return returnString;
	}

	private boolean newGroup(Group group) {
		return getGroupId()==null;
	}

	private boolean updateGroupUsers(Group group) {
		List<User> users = new ArrayList<User>(
				super.userCtrl.searchUsersByGroup(group));
		
		for(User usr : getAddedUsers()){
			if(!users.contains(usr)){
				super.userCtrl.saveGroupUser(newGroupUser(usr,group));
			}
		}

		for(User usr : users){
			if(!getAddedUsers().contains(usr)){
				super.userCtrl.removeUserFromGroup(group,usr);
			}
		}
		
		return true;
	}
	
	private GroupUser newGroupUser(User usr, Group grp){
		GroupUser groupUser = new GroupUser();
		groupUser.setUser(usr);
		groupUser.setGroup(grp);
		
		return groupUser;
	}

	private Group createGroupObj() {
		Group newGroup = new Group();
		if(getGroupId()!=null){
			newGroup.setKey(getGroupId());
		}
		newGroup.setName(getGroupName());
		newGroup.setDescription(getGroupDescription());
		newGroup.setOwner(getCurrentUserBean().getUser());

		return newGroup;
	}

	public String cancel() {
		return "/faces/views/home";
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public String getIgnoreGroupName() {
		return ignoreGroupName;
	}

	public void setIgnoreGroupName(String ignoreGroupName) {
		this.ignoreGroupName = ignoreGroupName;
	}

	public UUID getGroupId() {
		return groupId;
	}

	public void setGroupId(UUID groupId) {
		this.groupId = groupId;
	}
}
