package br.unifesp.maritaca.web.jsf.account;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import br.unifesp.maritaca.core.User;
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
	
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	
	private static final long serialVersionUID = 1L;
	private User     user;
	
	private String   confirmPassword;
	
	public AccountEditorBean() {
		super(false, true);
		clearUser();
	}
			
	private void clearUser(){
		setUser(new User());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
			              
	public String saveAccount(){
		if(!passwordsMatch() || registeredEmail()){
			return null;
		}
		getUser().setEmail(getEmail());
		if(!super.userCtrl.saveUser(getUser())){
			return null;
		}
		getCurrentUserBean().setUser(getUser());
		clearUser();
		return "/faces/views/home";
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
		clearUser();
		return "/faces/views/login";
	}
	
	/**
	 * Returns the view that is presented to the user when the user edition
	 * is canceled.
	 * @return The String containing the URL of the view.
	 */
	public String cancelUserEdition(){
		clearUser();
		return "/faces/views/home";
	}
	
	/**
	 * Set the user been managed by this bean as the current user logged
	 * and redirects to the account edition/creation page.
	 */
	public String useCurrentUser(){
		setUser(getCurrentUserBean().getUser());		
		return "/faces/views/editAccount";
	}
	
	public Boolean passwordsMatch(){
		if(getUser()==null || getConfirmPassword()==null || getUser().getPassword()==null){
			return false;
		} else {
			if(getUser().getPassword().equals(getConfirmPassword())){
				return true;
			} else {
				return false;
			}
		}
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public CurrentUserBean getCurrentUserBean() {
		return currentUserBean;
	}

	public void setCurrentUserBean(CurrentUserBean currentUserBean) {
		this.currentUserBean = currentUserBean;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
