package br.unifesp.maritaca.web.jsf.groups;

import java.util.Collection;

import javax.faces.bean.ManagedBean;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
public class GroupsBean extends AbstractBean{

	private static final long serialVersionUID = 1L;
	
	public GroupsBean() {
		super(false, true);
	}
	
	public Collection<Group> groupsStartingWithPrefix(String prefix){
		Collection<Group> groups = userCtrl.groupsStartingWith(prefix);
		for(Group gr : groups){
			//set data of the owner
			gr.setOwner(userCtrl.getOwnerOfGroup(gr));
		}
		return groups;
	}

}
