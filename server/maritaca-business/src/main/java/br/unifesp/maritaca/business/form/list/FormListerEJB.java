package br.unifesp.maritaca.business.form.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.permission.Document;
import br.unifesp.maritaca.persistence.permission.Permission;

@Stateless
public class FormListerEJB extends AbstractEJB {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(FormListerEJB.class);
		
	@Inject private UserDAO userDAO;

	/**
	 * 
	 * @param userDTO
	 * @return Collection<FormDTO>
	 */
	public Collection<FormDTO> getListOwnForms(UserDTO userDTO) {
		List<FormDTO> formsDTO = null;
		List<Form> forms = formDAO.getListOwnFormsByUserKey(userDTO.getKey().toString());
		if(forms != null && !forms.isEmpty()) {
			formsDTO = new ArrayList<FormDTO>();
			//TODO: Need I this query? this user has all the permissions for his/her forms!
			User user = userDAO.findUserByKey(userDTO.getKey());
			if(user != null) {
                for(Form form : forms) {//Ask
                	Permission permission = new Permission(true, true, true, true);//super.getPermission(form, form.getUser().getKey(), Document.FORM);
                	if(permission != null) {
                		if(permission.getShare()) { permission.setShare(form.changePolicy()); }
	                	FormDTO formDTO = new FormDTO(
	                            form.getKey(),
	                            form.getTitle(), 
	                            userDTO.getEmail(), 
	                            form.getUrl(), 
	                            form.getCreationDate().toString(), 
	                            form.getPolicy(),
	                            permission);
	                	formsDTO.add(formDTO);
                	}
                }            
			}
            else {
                log.error(userDTO.getEmail() + " User does not exist");
            }
		}
	    return formsDTO;	  
	}	
	
	/**
	 * 
	 * @param userDTO
	 * @return Collection<FormDTO>
	 */
	public Collection<FormDTO> getListSharedForms(UserDTO userDTO) {
		List<FormDTO> formsDTO = null;
		List<Form> forms = formDAO.getAllSharedFormsByUserKey(userDTO.getKey().toString());
		if(forms!= null && !forms.isEmpty()) {
			formsDTO = new ArrayList<FormDTO>();
			//TODO: Need I this query?
			User user = userDAO.findUserByKey(userDTO.getKey());
			if(user != null) {
                for(Form form : forms) {
                	if(form != null && !userDTO.getKey().toString().equals(form.getUser().getKey().toString())) {
                		Permission permission = super.getPermission(form, userDTO.getKey(), Document.FORM);
	                	if(permission != null) {
	                		if(permission.getShare()) { permission.setShare(form.changePolicy()); }
	                		User owner = userDAO.findUserByKey(form.getUser().getKey());
	                		FormDTO formDTO = new FormDTO(
		                            form.getKey(),
		                            form.getTitle(), 
		                            owner!=null?owner.getEmail():"", 
		                            form.getUrl(), 
		                            form.getCreationDate().toString(), 
		                            form.getPolicy(),
		                            permission);
		                	formsDTO.add(formDTO);
	                	}
                	}
                }            
			}
            else {
                log.error(userDTO.getEmail() + " User does not exist");
            }
		}
	    return formsDTO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}
