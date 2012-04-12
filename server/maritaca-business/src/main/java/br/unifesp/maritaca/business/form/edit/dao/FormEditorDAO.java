package br.unifesp.maritaca.business.form.edit.dao;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.base.BaseDAO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;

public class FormEditorDAO extends BaseDAO {

	public boolean verifyIfUserExist(UUID userKey) {
		return entityManager.rowDataExists(User.class, userKey);
	}
	
	public boolean verifyIfUrlExist(String url) {
		// TODO: improve this
		// look for url in the Form columnFamily
		List<Form> fsList = entityManager.cQuery(Form.class, "url", url, true);
		return fsList.size() > 0;
	}

	public void insertForm(Form form) {
		entityManager.persist(form);
	}

}
