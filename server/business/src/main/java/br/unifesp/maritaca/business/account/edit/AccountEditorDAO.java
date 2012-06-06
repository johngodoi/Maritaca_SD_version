package br.unifesp.maritaca.business.account.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.business.list.edit.ListEditorDAO;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.User;

public class AccountEditorDAO extends BaseDAO {

	@Inject
	private ListEditorDAO listEditorDAO;
	
	/**
	 * Saves the user passed as parameter in the database.
	 * If the user has no MaritacaList set it also creates
	 * one.
	 * 
	 * @param user
	 */
	public void saveUser(User user) {
		getEntityManager().persist(user);
		
		if(user.getMaritacaList()==null){
			createUserList(user);			
		} else {
			updateUserListName(user);
		}		
	}
	
	public User findUserByKey(UUID userKey){
		return getEntityManager().find(User.class, userKey);
	}
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public User findUserByEmail(String email) {		
		List<User> users = getEntityManager()
				.cQuery(User.class, "email", email);
		if (users == null || users.size() == 0) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new InvalidNumberOfEntries(User.class, "email", 
					email, users.size());
		}
	}
	
	private MaritacaList findUserList(UUID maritacaListKey){
		return getEntityManager().find(MaritacaList.class,maritacaListKey);
	}
	
	/**
	 * Update the user list name if the user changed its email.
	 */
	private void updateUserListName(User user) {
		MaritacaList userList = findUserList(user.getMaritacaList());
		if (!userList.getName().equals(user.getEmail())) {						
			userList.setName(user.getEmail());
			getListEditorDAO().saveMaritacaList(userList);
		}
	}
	
	private void createUserList(User owner) {
		MaritacaList list = new MaritacaList();
		list.setOwner(owner);
		list.setName(owner.getEmail());
		List<UUID> listUsers = new ArrayList<UUID>();
		listUsers.add(owner.getKey());
		list.setUsers(listUsers);
		getListEditorDAO().saveMaritacaList(list);
		
		owner.setMaritacaList(list.getKey());
		getEntityManager().persist(owner);				
	}

	public ListEditorDAO getListEditorDAO() {
		return listEditorDAO;
	}

	public void setListEditorDAO(ListEditorDAO listEditorDAO) {
		this.listEditorDAO = listEditorDAO;
	}
}