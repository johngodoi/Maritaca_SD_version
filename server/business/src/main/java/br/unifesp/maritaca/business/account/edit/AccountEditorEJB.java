package br.unifesp.maritaca.business.account.edit;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.dao.AccountEditorDAO;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.exception.MaritacaException;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.persistence.entity.User;

@Stateless
public class AccountEditorEJB{

	@Inject
	private AccountEditorDAO accountEditorDAO;
	
	/**
	 * Checks if the email from the user is already registered
	 * in the database. If the email belongs to the email from the logged
	 * user, this function also returns false.
	 * @param currentUser 
	 * @return true if the email is already taken, false otherwise.
	 */
	public boolean registeredEmail(String email){
		return getAccountEditorDAO().findUserByEmail(email)!=null;
	}
	
	/**
	 * Saves the user passed as parameter.
	 * @param userDto
	 * @throws IllegalArgumentException
	 */

	public void saveNewAccount(UserDTO userDto){
		if(userDto.getEmail()==null||registeredEmail(userDto.getEmail())){
			throw new IllegalArgumentException();
		}
		saveAccount(userDto);
	}
	
	private void saveAccount(UserDTO userDto) {
		User user = UtilsBusiness.convertToClass(userDto, User.class);
		getAccountEditorDAO().saveUser(user);
		userDto.setKey(user.getKey());
		userDto.setMaritacaList(user.getMaritacaList());
	}

	public void updateAccount(UserDTO userDto, String currentUserEmail){
		if(userDto.getEmail()==null){
			throw new IllegalArgumentException();
		}
		if(!userDto.getEmail().equals(currentUserEmail)
				&&registeredEmail(userDto.getEmail())){
			MaritacaException me = new MaritacaException();
			me.setUserMessage("account_create_used_email");
			throw me;
		}		
		saveAccount(userDto);
	}

	public AccountEditorDAO getAccountEditorDAO() {
		return accountEditorDAO;
	}

	public void setAccountEditorDAO(AccountEditorDAO accountEditorDAO) {
		this.accountEditorDAO = accountEditorDAO;
	}
}
