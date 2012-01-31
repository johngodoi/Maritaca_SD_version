package br.unifesp.maritaca.web.jsf.groups;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

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
@ViewScoped
public class ListGroupsBean extends AbstractBean{
	
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUser;	
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
		updateMyGroups();		
	}
	
	public void removeGroup(Group group){
		if(super.userCtrl.removeGroup(group)){
			getMyGroups().remove(group);
		}
	}

	private void updateMyGroups() {
		Collection<GroupUser>	myGroupsUser;
		Collection<Group>       myGroups = new ArrayList<Group>();
		User              		newUser  = getCurrentUser().getUser();
		
		myGroupsUser = super.userCtrl.getGroupsByMember(newUser);
		
		Group myGroup;
		User  owner;
		for(GroupUser groupUser : myGroupsUser){
			if(!myGroups.contains(groupUser)){
				myGroup = super.userCtrl.getGroup(groupUser.getGroup().getKey());
				owner   = super.userCtrl.getUser(myGroup.getOwner().getKey());
				myGroup.setOwner(owner);
				myGroups.add(myGroup);
			}
		}
		
		setMyGroups(myGroups);
	}
}
