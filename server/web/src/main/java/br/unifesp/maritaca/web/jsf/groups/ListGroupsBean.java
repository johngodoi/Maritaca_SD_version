package br.unifesp.maritaca.web.jsf.groups;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.utils.Utils;

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
	
	private static final Log log = LogFactory.getLog(ListGroupsBean.class);
	
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
	
	public void removeGroup(Group group){
		if(super.userCtrl.removeGroup(group)){
			getMyGroups().remove(group);
		} else {
			throw new RuntimeException(
					Utils.getMessageFromResourceProperties("list_remove_error"));
		}		
	}

	@PostConstruct
	public void updateMyGroups() {
		User              currentUsr  = getCurrentUser().getUser();				
		Collection<Group> ownedGroups = super.userCtrl.getGroupsByOwner(currentUsr);
		
		removeAllUsersGroup(ownedGroups);
		removeUserGroup(ownedGroups);
		
		fillOwnerEmail(ownedGroups);
		
		setMyGroups(ownedGroups);
	}

	private void fillOwnerEmail(Collection<Group> ownedGroups) {
		for(Group grp : ownedGroups){
			User owner = super.userCtrl.getUser(grp.getOwner().getKey());
			grp.setOwner(owner);
		}		
	}

	private void removeUserGroup(Collection<Group> ownedGroups) {
		Group userGroup = getCurrentUser().getUser().getUserGroup();
		if(!ownedGroups.remove(userGroup)){
			log.error("User group for user "+getCurrentUser().getUser()+" not found");
		}
	}

	private void removeAllUsersGroup(Collection<Group> ownedGroups) {
		Group allUsrGrp = super.userCtrl.getAllUsersGroup();
		ownedGroups.remove(allUsrGrp);
	}		
}
