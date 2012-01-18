package br.unifesp.maritaca.web.jsf.home;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
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
			
	public void clearUser(){
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
		
		EntityManagerFactory emFactory;
		EntityManager        em;
		
		emFactory  = EntityManagerFactory.getInstance();		
		em         = emFactory.createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
		
		List<User> users = em.cQuery(User.class, "email", email);
		
		if(users.size()!=0){
			return true;
		}
		return false;
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
