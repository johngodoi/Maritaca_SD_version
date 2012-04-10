package br.unifesp.maritaca.business.account.edit;

import br.unifesp.maritaca.business.account.edit.dao.EditAccountDAO;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.persistence.dto.UserDTO;

public class EditAccountEJB extends AbstractEJB{

	private static final long serialVersionUID = 1L;
	private EditAccountDAO editAccountDAO;
	
	/**
	 * Checks if the email from the user is already registered
	 * in the database. If the email belongs to the email from the logged
	 * user, this function also returns false.
	 * @return true if the email is already taken, false otherwise.
	 */
	public boolean registeredEmail(String email){
		if(email==null || email.isEmpty()){
			return false;
		}
		
		if(super.userCtrl.findUserByEmail(email)==null){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Saves the user passed as parameter. In case of success also set
	 * it as the current user.
	 * @param user
	 */
	public void saveAccount(UserDTO userToSave){
		if(!validateAccount()){
			return null;
		}
		
		boolean saveSuccessful = super.userCtrl.saveUser(userToSave);
		User    savedUser      = super.userCtrl.getUser(getEmail());
		
		if(!saveSuccessful || savedUser == null){
			super.addMessageError(MaritacaConstants.ACCOUNT_CREATE_ERROR);
			return null;
		} else {
			super.addMessageInfo(MaritacaConstants.ACCOUNT_CREATE_SUCCESS);
			getCurrentUserBean().setUser(new UserDTO());//(savedUser);
			clearUserInformation();
			return "/faces/views/home";
		}
	}

	public EditAccountDAO getEditAccountDAO() {
		return editAccountDAO;
	}

	public void setEditAccountDAO(EditAccountDAO editAccountDAO) {
		this.editAccountDAO = editAccountDAO;
	}
}
