package br.unifesp.maritaca.business.account.edit.dao;

import static br.unifesp.maritaca.util.UtilsCore.verifyEntity;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.User;

public class EditAccountDAO extends BaseDAO{

	public void saveUser(User user) {
		if (user == null){
			return false;
		}
		
		if(!updateUserGroup(user)){
			return false;
		}
		
		if (!entityManager.persist(user)) {
			return false;
		}		
		if(user.getMaritacaList()==null && !createUserGroup(user)){
			return false;
		}
	}
	
	private boolean createUserGroup(User owner) {		
		MaritacaList list = new MaritacaList();
		list.setOwner(owner);
		list.setName(owner.getEmail());
		
		if(!saveMaritacaList(list)){
			return false;
		}				
		
		owner.setMaritacaList(list);
		
		return saveUser(owner);
	}
	
	public boolean saveMaritacaList(MaritacaList list) {
		if (list == null) {
			throw new IllegalArgumentException("Invalid group");
		}
		verifyEntity(list.getOwner());
		if (list.getName()==null || list.getName().length() == 0) {
			throw new IllegalArgumentException("Incomplete parameters");
		}

		if (list.getKey() == null) {
			// new group
			if (searchMaritacaListByName(list.getName()) != null) {
				return false;
			}
			if (entityManager.persist(list)) {
				// add owner to group
				MaritacaListUser listUser = new MaritacaListUser();
				listUser.setMaritacaList(list);
				listUser.setUser(list.getOwner());
				if (saveMaritacaListUser(listUser))
					return true;
				else {
					// not able to add user to group
					entityManager.delete(list);
					return false;
				}
			} else
				return false; // group not saved
		} else {
			return entityManager.persist(list);
		}
	}
	
	public boolean saveMaritacaListUser(MaritacaListUser groupUser) {
		if (entityManager == null || groupUser == null)
			return false;

		return entityManager.persist(groupUser);
	}

	/**
	 * Update the user group name if the user changed its email
	 */
	private boolean updateListName(User user) {
		if(user.getKey()==null){
			return true;
		}		
		User dbUser = getUser(user.getKey());
		if(dbUser.getEmail().equals(user.getEmail())){
			return true;
		} else {
			MaritacaList userGroup = searchMaritacaListByName(dbUser.getEmail());
			userGroup.setName(user.getEmail());
			
			return entityManager.persist(userGroup);
		}		
	}
}