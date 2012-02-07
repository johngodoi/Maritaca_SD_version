package br.unifesp.maritaca.web.jsf.groups;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;

/**
 * Bean used to present groups to the user.
 * @author tiagobarabasz
 */
@ManagedBean
@RequestScoped
public class ListGroupsBean extends AbstractBean{
	
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean   currentUser;	
	private Collection<Group> myGroups;
	
	public ListGroupsBean() {
		super(false, true);
	}	

	public Collection<Group> getMyGroups() {
		return myGroups;
	}

	public void setMyGroups(Collection<Group> groups) {
		this.myGroups = groups;
	}

	public CurrentUserBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(CurrentUserBean currentUser) {				
		this.currentUser = currentUser;
	}
	
	public void leaveGroup(Group group){
		if(super.userCtrl.removeCurrentUserFromGroup(group)){
			getMyGroups().remove(group);
		}				
	}
	
	public void removeGroup(Group group){
		if(super.userCtrl.removeGroup(group)){
			getMyGroups().remove(group);
		}		
	}

	@PostConstruct
	public void updateMyGroups() {
		User              		newUser      = getCurrentUser().getUser();
		Collection<GroupUser>	myGroupsUser = super.userCtrl.getGroupsByMember(newUser);
		Collection<Group>       myGroups     = new ArrayList<Group>();
		
		Group myGroup;
		User  owner;
		for(GroupUser groupUser : myGroupsUser){
			if(!myGroups.contains(groupUser)){
				myGroup = super.userCtrl.getGroup(groupUser.getGroup().getKey());
				if(myGroup!=null){
					owner   = super.userCtrl.getUser(myGroup.getOwner().getKey());
					myGroup.setOwner(owner);
					myGroups.add(myGroup);	
				}else{
					/*TODO Add a log warning here saying that this group was deleted but
					 *     there is still information about him in the GroupUser table. */
				}
			}
		}		
		setMyGroups(myGroups);
	}
}
