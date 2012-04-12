package br.unifesp.maritaca.business.form.edit;

import static br.unifesp.maritaca.util.UtilsCore.verifyEntity;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.form.edit.dao.FormEditorDAO;
import br.unifesp.maritaca.business.form.edit.dao.FormPermissionsDAO;
import br.unifesp.maritaca.business.form.edit.dao.NewFormDTO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.util.UtilsCore;

@Stateless
public class FormEditorEJB {
	
	private static final Log log = LogFactory.getLog(FormEditorEJB.class);
	
	@Inject private FormEditorDAO formEditorDAO;
	@Inject private FormPermissionsDAO formPermissionsDAO;

	public void saveForm(NewFormDTO newFormDTO) {
		log.info("in saveForm");
		verifyEntity(newFormDTO.getUserKey());
//		verifyNullProperties(formDTO);
		
		Form form = new Form();
		form.setTitle(newFormDTO.getTitle());
		form.setXml(newFormDTO.getXml());
		form.setUrl(getUniqueUrl());
		if( formEditorDAO.verifyIfUserExist(newFormDTO.getUserKey()) ){
			formEditorDAO.insertForm(form);
			formPermissionsDAO.saveFormPermissionsByPolicy(form,new ArrayList<MaritacaList>());
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
	
}
