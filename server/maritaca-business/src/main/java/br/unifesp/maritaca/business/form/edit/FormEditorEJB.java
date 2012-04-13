package br.unifesp.maritaca.business.form.edit;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.business.base.PermissionDTO;
import br.unifesp.maritaca.business.base.UserDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.dao.FormEditorDAO;
import br.unifesp.maritaca.business.form.edit.dao.FormPermissionsDAO;
import br.unifesp.maritaca.business.form.list.dao.FormListerDAO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.AuthorizationDenied;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.util.UtilsCore;

@Stateless
public class FormEditorEJB {
	
	private static final Log log = LogFactory.getLog(FormEditorEJB.class);
	
	@Inject private FormEditorDAO formEditorDAO;
	@Inject private FormListerDAO formListerDAO;
	@Inject private FormPermissionsDAO formPermissionsDAO;
	@Inject private UserDAO userDAO;

	public void saveNewForm(FormDTO formDTO) {
		log.info("in saveNewForm");
//		verifyEntity(formDTO.getUserKey());
//		verifyNullProperties(formDTO);
		
		Form form = new Form();
		form.setTitle(formDTO.getTitle());
		form.setXml(formDTO.getXml());
		form.setUrl(getUniqueUrl());
		if( formEditorDAO.verifyIfUserExist(formDTO.getUserKey()) ){
			form.setUser(userDAO.findUserByKey(formDTO.getUserKey()));
			formEditorDAO.persistForm(form);
			formPermissionsDAO.saveFormPermissionsByPolicy(form,new ArrayList<MaritacaList>());
		} else {
			throw new IllegalArgumentException("User does not exist in database");
		}
		
	}
	
	public void updateForm(FormDTO formDTO, UserDTO currentUserDTO){
		log.info("in updateForm");
//		verifyEntity(formDTO.getUserKey());
//		verifyNullProperties(formDTO);
		
		Form form = new Form();
		form.setKey(formDTO.getKey());
		form.setTitle(formDTO.getTitle());
		form.setXml(formDTO.getXml());
		form.setUrl(getUniqueUrl());
		// check permissions for updating
		Form originalForm = formEditorDAO.getForm(form.getKey(), true);
		if(!formPermissionsDAO.currentUserHasPermission(originalForm, Operation.UPDATE)){
			throw new AuthorizationDenied(Form.class, formDTO.getKey(), currentUserDTO.getKey(), Operation.UPDATE);
		}
		if( formEditorDAO.verifyIfUserExist(formDTO.getUserKey()) ){
			form.setUser(userDAO.findUserByKey(formDTO.getUserKey()));
			formEditorDAO.persistForm(form);
			// save permissions of a form
			formPermissionsDAO.saveFormPermissionsByPolicy(form, new ArrayList<MaritacaList>());
		} else {
			throw new IllegalArgumentException("User does not exist in database");
		}
	}
	
	/**
	 * Get an unique url for a form
	 * 
	 * @return
	 */
	private String getUniqueUrl() {
		// TODO: check if this random string is enough
		// maybe it is better to generate uuid-based string
		String url = UtilsCore.randomString();
		if (!formEditorDAO.verifyIfUrlExist(url))
			return url;
		else
			return getUniqueUrl();
	}

	public FormDTO getFormWithPermissions(FormDTO formDTO, UserDTO userDTO) {
		Form form = new Form();
		form.setKey(formDTO.getKey());
		User user = userDAO.findUserByEmail(userDTO.getEmail());
		PermissionDTO permission = new PermissionDTO(
				formListerDAO.currentUserHasPermission(user, form, Operation.READ), 
				formListerDAO.currentUserHasPermission(user, form, Operation.UPDATE), 
				formListerDAO.currentUserHasPermission(user, form, Operation.SHARE), 
				formListerDAO.currentUserHasPermission(user, form, Operation.DELETE));
		formDTO.setPermissionDTO(permission);
		return formDTO;
	}

	public void deleteForm(FormDTO formDTO, UserDTO userDTO) {
		// verify if current user has permissions
		if (formPermissionsDAO.currentUserHasPermission(formDTO, Operation.DELETE)) {
			Form form = new Form();
			form.setKey(formDTO.getKey());
			// first delete permissions
			List<FormPermissions> permissionsList = 
					formPermissionsDAO.getFormPermissions(form);
			for (FormPermissions fp : permissionsList) {
				formPermissionsDAO.delete(fp);
			}
			formEditorDAO.delete(form);
			// TODO delete answers?
		} else {
			throw new AuthorizationDenied(Form.class, formDTO.getKey(), userDTO.getKey(), Operation.DELETE);
		}	
	}
	
}
