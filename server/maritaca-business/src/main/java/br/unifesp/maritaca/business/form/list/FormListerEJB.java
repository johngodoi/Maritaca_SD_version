package br.unifesp.maritaca.business.form.list;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.base.BaseDAO;
import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.base.PermissionDTO;
import br.unifesp.maritaca.business.base.UserDAO;
import br.unifesp.maritaca.business.form.list.dao.FormListerDAO;
import br.unifesp.maritaca.business.form.list.dto.FormListerDTO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
public class FormListerEJB extends BaseDAO {
	
	private static final Log log = LogFactory.getLog(FormListerEJB.class);
	
	@Inject FormListerDAO listFormsDAO;
	@Inject UserDAO userDAO;

	//TODO: Check why f.getUser.getEmail() is null, althought when the user is the onwer isn't neccessary show the column "owner"
	public Collection<FormListerDTO> getListOwnForms(UserDTO userDTO) {
		log.info("FormListerEJB - getListOwnForms");
		List<FormListerDTO> formsDTO = null;
		SimpleDateFormat newFormat = new SimpleDateFormat(MaritacaConstants.SHORT_DATE_FORMAT_ISO8601);
		List<Form> forms = listFormsDAO.getListOwnFormsByUserKey(userDTO.getKey().toString());
		if(!forms.isEmpty()) {
			formsDTO = new ArrayList<FormListerDTO>();
			//
			User user = userDAO.findUserByEmail(userDTO.getEmail());
			//
			for(Form f : forms) {
				PermissionDTO permission = new PermissionDTO(
						listFormsDAO.currentUserHasPermission(user, f, Operation.READ), 
						listFormsDAO.currentUserHasPermission(user, f, Operation.UPDATE), 
						listFormsDAO.currentUserHasPermission(user, f, Operation.SHARE), 
						listFormsDAO.currentUserHasPermission(user, f, Operation.DELETE));
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
	
	public Collection<FormListerDTO> getListSharedForms(UserDTO userDTO) {
		log.info("FormListerEJB - getListSharedForms");
		List<FormListerDTO> formsDTO = null;
		SimpleDateFormat newFormat = new SimpleDateFormat(MaritacaConstants.SHORT_DATE_FORMAT_ISO8601);
		User user = userDAO.findUserByEmail(userDTO.getEmail());
		List<Form> forms = listFormsDAO.getListSharedFormsByUserKey(user);
		if(!forms.isEmpty()) {
			formsDTO = new ArrayList<FormListerDTO>();			
			for(Form f : forms) {
				PermissionDTO permission = new PermissionDTO(
						listFormsDAO.currentUserHasPermission(user, f, Operation.READ), 
						listFormsDAO.currentUserHasPermission(user, f, Operation.UPDATE), 
						listFormsDAO.currentUserHasPermission(user, f, Operation.SHARE), 
						listFormsDAO.currentUserHasPermission(user, f, Operation.DELETE));
				FormListerDTO formDTO = new FormListerDTO(
						f.getKey(),
						f.getTitle(), 
						f.getUser().getEmail(), 
						newFormat.format(f.getCreationDate()), 
						f.getPolicy().toString(),
						permission);
				formsDTO.add(formDTO);
			}
		}		
		return formsDTO;
	}
}