package br.unifesp.maritaca.web.jsf.home;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
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
public class CreateAccountBean extends AbstractBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private User     user;
	private String   confirmPassword;
	
	public CreateAccountBean() {
		super(false, true);
		setUser(new User());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
			              
	public String create(){
		if(!super.userCtrl.saveUser(getUser())){
			return null;
		}		
		return "/faces/views/forms";
	}
	
	/**
	 * Checks if the email from the user is already registered
	 * in the database.
	 * @return true if the email is already taken, false otherwise.
	 */
	public Boolean registeredEmail(){
		String email = getUser().getEmail();
		if(email==null || email.isEmpty()){
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
}
