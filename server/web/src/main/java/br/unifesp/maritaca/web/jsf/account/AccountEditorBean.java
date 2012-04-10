package br.unifesp.maritaca.web.jsf.account;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.unifesp.maritaca.business.account.edit.EditAccountEJB;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.jsf.util.MaritacaConstants;

/**
 * Bean responsible for handling user creation.
 * @author tiagobarabasz
 */

@ManagedBean
@SessionScoped
public class AccountEditorBean extends MaritacaJSFBean{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EditAccountEJB editAccountEJB;
	
	@Pattern(regexp = MaritacaConstants.EMAIL_REG_EXP)
	private String email;
	
	@Size(min = 3, max = 20)
	private String firstName;
	
	@Size(max = 20)
	private String lastName;
	
	private String encryptedPassword;	
		
	private boolean creatingAccount;
			
	public AccountEditorBean() {
		clearUserInformation();
		setCreatingAccount(false);
	}
				
	private void clearUserInformation(){
		setEmail("");
		setFirstName("");
		setLastName("");
	}
	
	public String saveButtonPressed(){
		if(super.getCurrentUser()!=null){
			return updateAccount();
		} else{
			return saveNewAccount();
		}
	}
	
	public String createAccount(){
		setCreatingAccount(true);
		return null;
	}
	
	private String updateAccount(){
		UserDTO userToUpdate = createUserFromInformation();		
		editAccountEJB.saveAccount(userToUpdate);
		
		return MaritacaConstants.FACES_HOME;
	}
	
	public UserDTO createUserFromInformation(){
		UserDTO user = new UserDTO();
		
		user.setEmail(getEmail());
		user.setFirstname(getFirstName());
		user.setLastname(getLastName());
		setEncryptedPassword(getEncryptedPassword());
		
		return user;
	}
	
	public void fillInformationFromUser(UserDTO user){
		setEmail(user.getEmail());
		setFirstName(user.getFirstname());
		setLastName(user.getLastname());
		setEncryptedPassword(user.getEncryptedPassword());
	}
	
	/**
	 * Create a new user based on information provided and saves it.
	 * @return null if not successful (refresh current view)
	 * or the navigation string to the home page if successful. 
	 */
	public String saveNewAccount(){
		UserDTO userToSave = createUserFromInformation();				
		editAccountEJB.saveAccount(userToSave);		
		return MaritacaConstants.FACES_HOME;
	}
	
	private boolean validateAccount(){
		if(editAccountEJB.registeredEmail(getEmail())){
			super.addMessageError(MaritacaConstants.ACCOUNT_CREATE_USED_EMAIL);
			return false;
		} else {
			return true;
		}		
	}
	
	/**
	 * Returns the view that is presented to the user when the user creation
	 * is canceled.
	 * @return The String containing the URL of the view.
	 */
	public String cancelUserCreation(){
		clearUserInformation();
		setCreatingAccount(false);
		return null;
	}
	
	public String editAccount(){
		getModuleManager().setActiveModuleByString("Settings");
		getModuleManager().setActiveSubModuleInActiveMod("accountEditor");
		return null;
	}
	
	/**
	 * Returns the view that is presented to the user when the user edition
	 * is canceled.
	 * @return The String containing the URL of the view.
	 */
	public String cancelUserEdition(){
		clearUserInformation();
		getModuleManager().setActiveModuleByString("Forms");
		getModuleManager().setActiveSubModuleInActiveMod("listForms");
		return null;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public boolean isCreatingAccount() {
		return creatingAccount;
	}

	public void setCreatingAccount(boolean creatingAccount) {
		this.creatingAccount = creatingAccount;
	}

	public EditAccountEJB getEditAccountEJB() {
		return editAccountEJB;
	}

	public void setEditAccountEJB(EditAccountEJB editAccountEJB) {
		this.editAccountEJB = editAccountEJB;
	}
}
