package br.unifesp.maritaca.business.account.edit;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.dao.AccountEditorDAO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

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
	public void saveAccount(UserDTO userDto){
		if(userDto.getEmail()==null||registeredEmail(userDto.getEmail())){
			throw new IllegalArgumentException();
		}
		User user = UtilsBusiness.convertToClass(userDto, User.class);
		getAccountEditorDAO().saveUser(user);
		userDto.setKey(user.getKey());
		userDto.setMaritacaList(user.getMaritacaList());
	}

	public AccountEditorDAO getAccountEditorDAO() {
		return accountEditorDAO;
	}

	public void setAccountEditorDAO(AccountEditorDAO accountEditorDAO) {
		this.accountEditorDAO = accountEditorDAO;
	}
}
