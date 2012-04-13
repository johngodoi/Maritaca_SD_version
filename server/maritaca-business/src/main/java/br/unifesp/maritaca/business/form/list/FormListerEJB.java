package br.unifesp.maritaca.business.form.list;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.base.PermissionDTO;
import br.unifesp.maritaca.business.base.UserDAO;
import br.unifesp.maritaca.business.form.list.dao.FormListerDAO;
import br.unifesp.maritaca.business.form.list.dto.FormListerDTO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
public class FormListerEJB {
	
	private static final Log log = LogFactory.getLog(FormListerEJB.class);
	
	@Inject private FormListerDAO formListerDAO;
	@Inject private UserDAO userDAO;

	public Collection<FormListerDTO> getListOwnForms(UserDTO userDTO) {
		List<FormListerDTO> formsDTO = null;
		SimpleDateFormat newFormat = new SimpleDateFormat(MaritacaConstants.SHORT_DATE_FORMAT_ISO8601);
		List<Form> forms = formListerDAO.getListOwnFormsByUserKey(userDTO.getKey().toString());
		if(!forms.isEmpty()) {
			formsDTO = new ArrayList<FormListerDTO>();
			//
			User user = userDAO.findUserByEmail(userDTO.getEmail());
			for(Form f : forms) {
				PermissionDTO permission = new PermissionDTO(
						formListerDAO.currentUserHasPermission(user, f, Operation.READ), 
						formListerDAO.currentUserHasPermission(user, f, Operation.UPDATE), 
						formListerDAO.currentUserHasPermission(user, f, Operation.SHARE), 
						formListerDAO.currentUserHasPermission(user, f, Operation.DELETE));
				FormListerDTO formDTO = new FormListerDTO(
						f.getKey(),
						f.getTitle(), 
						userDTO.getEmail(), 
						newFormat.format(f.getCreationDate()), 
						f.getPolicy().toString(),
						permission);
				formsDTO.add(formDTO);
			}
		}		
		return formsDTO;
	}
	
	public User getUserByKey(UUID uuid) {
		return userDAO.findUserByKey(uuid);
	}
	
	private String getUserEmailByKey(UUID uuid) {
		User user = userDAO.findUserByKey(uuid);
		if(user != null)
			return user.getEmail();
		else {
			log.error(uuid + " User doesn't exist");
			return "";
		}
	}
	
	public Collection<FormListerDTO> getListSharedForms(UserDTO userDTO) {
		List<FormListerDTO> formsDTO = null;
		SimpleDateFormat newFormat = new SimpleDateFormat(MaritacaConstants.SHORT_DATE_FORMAT_ISO8601);
		User user = userDAO.findUserByEmail(userDTO.getEmail());
		List<Form> forms = formListerDAO.getListSharedFormsByUserKey(user);
		if(!forms.isEmpty()) {
			formsDTO = new ArrayList<FormListerDTO>();			
			for(Form f : forms) {
				PermissionDTO permission = new PermissionDTO(
						formListerDAO.currentUserHasPermission(user, f, Operation.READ), 
						formListerDAO.currentUserHasPermission(user, f, Operation.UPDATE), 
						formListerDAO.currentUserHasPermission(user, f, Operation.SHARE), 
						formListerDAO.currentUserHasPermission(user, f, Operation.DELETE));
				FormListerDTO formDTO = new FormListerDTO(
						f.getKey(),
						f.getTitle(), 
						getUserEmailByKey(f.getUser().getKey()),  
						newFormat.format(f.getCreationDate()), 
						f.getPolicy().toString(),
						permission);
				formsDTO.add(formDTO);
			}
		}		
		return formsDTO;
	}
}
