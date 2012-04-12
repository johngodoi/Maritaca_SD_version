package br.unifesp.maritaca.business.account.edit.dao;

import java.util.List;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.business.list.edit.dao.ListEditorDAO;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

public class AccountEditorDAO extends BaseDAO {

	private ListEditorDAO listEditorDAO = new ListEditorDAO();
	
	/**
	 * Saves the user passed as parameter in the database.
	 * If the user has no MaritacaList set it also creates
	 * one.
	 * 
	 * @param user
	 */
	public void saveUser(UserDTO userDto) {
		User user = createUserFromDto(userDto);
		saveUser(user);
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
			throw new InvalidNumberOfEntries(email, User.class);
		}
	}
	
	private void saveUser(User user){
		getEntityManager().persist(user);
		if(user.getMaritacaList()==null){
			createUserList(user);			
		} else {			
			updateUserListName(user);
		}		
	}	
	
	/**
	 * Update the user list name if the user changed its email.
	 */
	private void updateUserListName(User user) {				
		MaritacaList userList;
		userList = getEntityManager().find(MaritacaList.class, user.getMaritacaList().getKey());			
		if (!userList.getName().equals(user.getEmail())) {						
			userList.setName(user.getEmail());
			listEditorDAO.saveMaritacaList(userList);
		}
	}

	private User createUserFromDto(UserDTO userToSave) {
		User user = new User();
		user.setEmail(userToSave.getEmail());
		user.setFirstname(userToSave.getFirstname());
		user.setLastname(userToSave.getLastname());
		user.setPassword(userToSave.getEncryptedPassword());
		return user;
	}
	
	private void createUserList(User owner) {
		MaritacaList list = new MaritacaList();
		list.setOwner(owner);
		list.setName(owner.getEmail());

		listEditorDAO.saveMaritacaList(list);
		getEntityManager().persist(owner);
	}
}