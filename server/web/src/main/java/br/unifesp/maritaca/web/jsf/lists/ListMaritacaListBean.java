package br.unifesp.maritaca.web.jsf.lists;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Bean used to present lists to the user.
 * @author tiagobarabasz
 */
@ManagedBean
@RequestScoped
public class ListMaritacaListBean extends AbstractBean{
	
	private static final long serialVersionUID = 1L;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean   currentUser;	
	private Collection<MaritacaList> myLists;
	
	private static final Log log = LogFactory.getLog(ListMaritacaListBean.class);
	
	public ListMaritacaListBean() {
		super(false, true);
	}	

	public Collection<MaritacaList> getMyLists() {
		return myLists;
	}

	public void setMyLists(Collection<MaritacaList> lists) {
		this.myLists = lists;
	}

	public CurrentUserBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(CurrentUserBean currentUser) {				
		this.currentUser = currentUser;
	}
	
	public void removeList(MaritacaList list){
		if(super.userCtrl.removeMaritacaList(list)){
			getMyLists().remove(list);
		} else {
			throw new RuntimeException(
					Utils.getMessageFromResourceProperties("list_remove_error"));
		}		
	}

	@PostConstruct
	public void updateMyLists() {
		User              currentUsr  = getCurrentUser().getUser();				
		Collection<MaritacaList> ownedLists = super.userCtrl.getMaritacaListsByOwner(currentUsr);
		
		removeAllUsersLists(ownedLists);
		removeUserList(ownedLists);
		
		fillOwnerEmail(ownedLists);
		
		setMyLists(ownedLists);
	}

	private void fillOwnerEmail(Collection<MaritacaList> ownedLists) {
		for(MaritacaList grp : ownedLists){
			User owner = super.userCtrl.getUser(grp.getOwner().getKey());
			grp.setOwner(owner);
		}		
	}

	private void removeUserList(Collection<MaritacaList> ownedLists) {
		MaritacaList userList = getCurrentUser().getUser().getMaritacaList();
		if(!ownedLists.remove(userList)){
			log.error("User list for user "+getCurrentUser().getUser()+" not found");
		}
	}

	private void removeAllUsersLists(Collection<MaritacaList> ownedLists) {
		MaritacaList allUsrGrp = super.userCtrl.getAllUsersList();
		ownedLists.remove(allUsrGrp);
	}		
}
