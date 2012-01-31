package br.unifesp.maritaca.web.jsf.account;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.web.jsf.AbstractBean;

/**
 * Bean responsible for handling user creation.
 * @author tiagobarabasz
 */

@ManagedBean
@SessionScoped
public class AccountManagerBean extends AbstractBean implements Serializable{
	
	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUserBean;
	
	private static final long serialVersionUID = 1L;
	private User     user;
	private String   confirmPassword;
	
	public AccountManagerBean() {
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
		String email = getUser().getEmail();
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
		setUser(getCurrentUserBean().getUser().clone());		
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
}
