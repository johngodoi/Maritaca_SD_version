package br.unifesp.maritaca.business.account.edit;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.dao.AccountEditorDAO;
import br.unifesp.maritaca.business.annotations.VerifyObject;
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
	public boolean registeredEmail(String email, UserDTO currentUser){
		if(email==null){
			return false;
		}
		if(currentUser!=null && email.equals(currentUser.getEmail())){
			return false;
		}
		return accountEditorDAO.findUserByEmail(email)!=null;
	}
	
	/**
	 * Saves the user passed as parameter.
	 * @param userDto
	 * @throws IllegalArgumentException
	 */
	public void saveAccount(@VerifyObject(UserDTO.class) UserDTO userDto){		
		if(!validateAccount(userDto)){
			throw new IllegalArgumentException();
		}				
		accountEditorDAO.saveUser(userDto);						
	}

	private boolean validateAccount(UserDTO user){
		String email = user.getEmail();
		if(registeredEmail(email,user)){
			return false;
		} else {
			return true;
		}		
	}
}
