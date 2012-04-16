package br.unifesp.maritaca.business.form.edit.dao;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaList;
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

	public void persistForm(Form form) {
		entityManager.persist(form);
	}

	public Form getForm(UUID key, boolean minimal) {
		if (key == null ) {
			throw new IllegalArgumentException("Incomplete parameters");
		}
		Form form = entityManager.find(Form.class, key, minimal);
//		if(userHasPermission(getCurrentUser(), form, Operation.READ)){
//			return form;	
//		} else {
//			throw new AuthorizationDenied(Form.class, form.getKey(), getCurrentUser().getKey(), Operation.READ);
//		}	
		return form;
	}

	public void delete(Form form) {
		entityManager.delete(form);
	}
	
	public User getOwnerOfMaritacaList(MaritacaList gr) {
		if (gr.getOwner() != null) {
			return getUser(gr.getOwner().getKey());
		} else {
			MaritacaList group = entityManager.find(MaritacaList.class, gr.getKey(), true);
			if (group != null) {
				return getOwnerOfMaritacaList(group);
			} else {
				return null;
			}
		}
	}
	
	public User getUser(UUID uuid) {
		return entityManager.find(User.class, uuid);
	}
}
