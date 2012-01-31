package br.unifesp.maritaca.web.jsf.groups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Bean responsible for managing user groups.
 * 
 * @author tiagobarabasz
 */
@ManagedBean
@ViewScoped
public class GroupsManagerBean implements Serializable {

	/* Error messages resources */
	private static final String GROUP_ADD_ERROR_USER_NOT_FOUND = "group_add_error_user_not_found";
	private static final String GROUP_ADD_ERROR_USER_ADDED     = "group_add_error_user_added";
	private static final String GROUP_ADD_EMPTY_EMAIL          = "group_add_empty_email";

	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	
	@Size(min=4,max=20)
	private String groupName;
	private String groupDescription;
	
	private boolean allowUserJoin;
	private List<String> autoCompleteEmails;
	private List<User> addedUsers;
	
	@Pattern(regexp=Utils.EMAIL_REG_EXP)
	private String selectedEmail;	
	private String addEmailError;

	public GroupsManagerBean() {
		emptyGroup();
	}

	private void emptyGroup() {
		setAutoCompleteEmails(new ArrayList<String>());
		setAddedUsers(new ArrayList<User>());
	}

	/**
	 * Checks if there is one group with the same name as the one being created
	 * that belongs to the logged user.
	 * 
	 * @return true if there is, false otherwise
	 */
	public boolean registeredGroupName() {
		UserModel userModel = ModelFactory.getInstance().createUserModel();
		if(userModel.searchGroupByName(getGroupName())==null){
			return false;
		} else {
			return true;
		}
	}

	public void addEmail() {
		if(getSelectedEmail()==null || getSelectedEmail().equals("")){
			setAddEmailError(Utils.getMessageFromResourceProperties(GROUP_ADD_EMPTY_EMAIL));
			return;
		}
		
		if(emailAdded(getSelectedEmail())){
		// Email already added
			setAddEmailError(Utils.getMessageFromResourceProperties(GROUP_ADD_ERROR_USER_ADDED));
			return;
		}
		
		User selectedUser = findUserByEmail(getSelectedEmail());
		if(selectedUser!=null){
		// Sucess!
			clearAddEmailError();
			getAddedUsers().add(selectedUser);			
		} else{
			setAddEmailError(Utils.getMessageFromResourceProperties(GROUP_ADD_ERROR_USER_NOT_FOUND));
		}
	}
	
	private boolean emailAdded(String selectedEmail) {
		for(User u : getAddedUsers()){
			if(u.getEmail().equals(selectedEmail)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a list of strings containing the emails
	 * from the users being added to the group.
	 * @return
	 */
	public List<String> getAddedEmailsList(){
		List<String> addedEmails = new ArrayList<String>();
		for(User u : getAddedUsers()){
			addedEmails.add(u.getEmail());
		}
		return addedEmails;
	}

	private User findUserByEmail(String selectedEmail) {
		UserModel userModel = ModelFactory.getInstance().createUserModel();
		
		return userModel.findUserByEmail(selectedEmail);
	}

	public void removeEmail(String email){
		clearAddEmailError();
		if(emailAdded(email)){
			for(User u : getAddedUsers()){
				if(u.getEmail().equals(email)){
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
		UserModel userModel = ModelFactory.getInstance().createUserModel();
		List<String> userEmails = new ArrayList<String>();

		for (User u : userModel.usersStartingWith(startingString)) {
			userEmails.add(u.getEmail());
		}
		
		setAutoCompleteEmails(userEmails);

		return userEmails;
	}

	/**
	 * Save the group being created. Reloads the page if an error
	 * occurs or go to the home page otherwise.
	 * @return Navigation string
	 */
	public String save() {
		String  returnString = null;
		Boolean error        = false;
		
		ModelFactory modelFactory = ModelFactory.getInstance();
		UserModel userModel = modelFactory.createUserModel();
								
		Group group = createGroupObj();
		if (userModel.saveGroup(group)) {
			for(User usr : getAddedUsers()){
				GroupUser groupUser = new GroupUser();
				groupUser.setUser(usr);
				groupUser.setGroup(group);				
				if(!userModel.saveGroupUser(groupUser)){
					error = true;
					break;
				}
			}
			if(!error){
				returnString = "/faces/views/home";
			}
		}
		
		emptyGroup();		
		return returnString;
	}

	private Group createGroupObj() {
		Group newGroup = new Group();
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

	public boolean isAllowUserJoin() {
		return allowUserJoin;
	}

	public void setAllowUserJoin(boolean allowUserJoin) {
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
}
