package br.unifesp.maritaca.web.jsf.account;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.account.edit.AccountEditorEJB;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.jsf.login.LoginManagerBean;
import br.unifesp.maritaca.web.jsf.util.MaritacaConstants;

/**
 * Bean responsible for handling user creation.
 * @author tiagobarabasz
 */

@ManagedBean
@SessionScoped
public class AccountEditorBean extends MaritacaJSFBean{
	
	private static final long serialVersionUID = 1L;
	
	private UserDTO userDto;
	
	@Inject
	private AccountEditorEJB accountEditorEJB;
	
	@ManagedProperty("#{loginManagerBean}") 
	private LoginManagerBean loginManagerBean;
			
	private boolean creatingAccount;
			
	public AccountEditorBean() {
		clearUserInformation();
		setCreatingAccount(false);
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
	
	public void updateUserInformation(){
		if(getCurrentUser()!=null){
			setUserDto(getCurrentUser());
		}
	}
	
	private String updateAccount(){	
		getAccountEditorEJB().saveAccount(userDto);
		getModuleManager().setActiveModuleByString("Forms");
		getModuleManager().setActiveSubModuleInActiveMod("listForms");
		return MaritacaConstants.FACES_HOME;
	}
	
	/**
	 * Create a new user based on information provided and saves it.
	 * @return null if not successful (refresh current view)
	 * or the navigation string to the home page if successful. 
	 */
	public String saveNewAccount(){					
		getAccountEditorEJB().saveAccount(userDto);
		getLoginManagerBean().login(userDto);
		return MaritacaConstants.FACES_HOME;
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
		setUserDto((UserDTO)getCurrentUser().clone());
		return null;
	}

	public boolean registeredEmail(){
		if(userDto.getEmail()==null || emailFromCurrentUser())
			return false;
		return getAccountEditorEJB().registeredEmail(userDto.getEmail());
	}
	
	private boolean emailFromCurrentUser(){
		UserDTO currentUser = getCurrentUser();
		if(currentUser==null){
			return false;
		}		
		return userDto.getEmail().equals(currentUser.getEmail());
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
	
	private void clearUserInformation(){
		userDto = new UserDTO();
	}

	public boolean isCreatingAccount() {
		return creatingAccount;
	}

	public void setCreatingAccount(boolean creatingAccount) {
		this.creatingAccount = creatingAccount;
	}

	public AccountEditorEJB getAccountEditorEJB() {
		return accountEditorEJB;
	}

	public void setAccountEditorEJB(AccountEditorEJB accountEditorEJB) {
		this.accountEditorEJB = accountEditorEJB;
	}

	public UserDTO getUserDto() {
		return userDto;
	}

	public void setUserDto(UserDTO userDto) {
		this.userDto = userDto;
	}

	public LoginManagerBean getLoginManagerBean() {
		return loginManagerBean;
	}

	public void setLoginManagerBean(LoginManagerBean loginManagerBean) {
		this.loginManagerBean = loginManagerBean;
	}
}
