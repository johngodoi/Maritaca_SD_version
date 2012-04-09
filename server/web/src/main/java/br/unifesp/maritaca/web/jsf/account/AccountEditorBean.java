package br.unifesp.maritaca.web.jsf.account;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.MaritacaUserDTO;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Bean responsible for handling user creation.
 * @author tiagobarabasz
 */

@ManagedBean
@SessionScoped
public class AccountEditorBean extends AbstractBean implements Serializable{
	
	// TODO Use bean attributes instead of User attributes in xhtml.
	// TODO Verify the use of Pattern in email validation and the use of 
	//		ValidationMessages.properties
	@Pattern(regexp = Utils.EMAIL_REG_EXP, message="{email.invalid}")
	private String email;
	
	@Size(min = 3, max = 20)
	private String firstName;
	
	@Size(max = 20)
	private String lastName;
	
	private String encryptedPassword;
	
	/* Managed Properties */
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;	
	
	@ManagedProperty("#{manager}")
	private Manager moduleManager;
	
	private static final long serialVersionUID = 1L;
	
	private boolean creatingAccount;
			
	public AccountEditorBean() {
		super(false, true);
		clearUserInformation();
		setCreatingAccount(false);
	}
	
	@PostConstruct
	public void updateUserInformation(){
		if(getCurrentUserBean()!=null && getCurrentUserBean().getUser()!=null){
			//fillInformationFromUser(getCurrentUserBean().getUser());
		}
	}
			
	private void clearUserInformation(){
		setEmail("");
		setFirstName("");
		setLastName("");
	}
	
	public String saveButtonPressed(){
		if(getCurrentUserBean().getUser()!=null){
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
		User userToUpdate = createUserFromInformation();
		userToUpdate.setKey(getCurrentUserBean().getUser().getKey());
		
		return saveAccount(userToUpdate);
	}
	
	public User createUserFromInformation(){
		User user = new User();
		
		user.setEmail(getEmail());
		user.setFirstname(getFirstName());
		user.setLastname(getLastName());
		user.setPassword(getEncryptedPassword());
		
		return user;
	}
	
	public void fillInformationFromUser(User user){
		setEmail(user.getEmail());
		setFirstName(user.getFirstname());
		setLastName(user.getLastname());
		setEncryptedPassword(user.getPassword());
	}
	
	/**
	 * Create a new user based on information provided
	 * and saves it.
	 * @return null if not successful (refresh current view)
	 * or the navigation string to the home page if successful. 
	 */
	public String saveNewAccount(){
		User userToSave = createUserFromInformation();		
		return saveAccount(userToSave);
	}
	
	/**
	 * Saves the user passed as parameter. In case of success also set
	 * it as the current user.
	 * @param user
	 * @return true if successful, false otherwise.
	 */
	private String saveAccount(User userToSave){
		if(!validateAccount()){
			return null;
		}
		
		boolean saveSuccessful = super.userCtrl.saveUser(userToSave);
		User    savedUser      = super.userCtrl.getUser(getEmail());
		
		if(!saveSuccessful || savedUser == null){
			addMessage("account_create_error", FacesMessage.SEVERITY_ERROR);
			return null;
		} else {
			addMessage("account_create_success", FacesMessage.SEVERITY_INFO);
			getCurrentUserBean().setUser(new MaritacaUserDTO());//(savedUser);
			clearUserInformation();
			return "/faces/views/home";

		}
	}
	
	private boolean validateAccount(){
		if(registeredEmail()){
			addMessage("account_create_used_email", FacesMessage.SEVERITY_ERROR);
			return false;
		} else {
			return true;
		}		
	}
	
	/**
	 * Checks if the email from the user is already registered
	 * in the database. If the email belongs to the email from the logged
	 * user, this function also returns false.
	 * @return true if the email is already taken, false otherwise.
	 */
	public Boolean registeredEmail(){
		String email = getEmail();
		if(email==null || email.isEmpty()){
			return false;
		}
		
		if(getCurrentUserBean().getUser()!=null &&
				email.equals(getCurrentUserBean().getUser().getEmail())){
			return false;
		}
		
		if(super.userCtrl.findUserByEmail(email)==null){
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

	public CurrentUserBean getCurrentUserBean() {
		return currentUserBean;
	}

	public void setCurrentUserBean(CurrentUserBean currentUserBean) {
		this.currentUserBean = currentUserBean;
	}

	public Manager getModuleManager() {
		return moduleManager;
	}

	public void setModuleManager(Manager moduleManager) {
		this.moduleManager = moduleManager;
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
}
