package br.unifesp.maritaca.business.base.dao;

import java.util.UUID;

import br.unifesp.maritaca.persistence.entity.FormAccessibleByList;

public class FormAccessibleByListDAO extends BaseDAO {

	public FormAccessibleByList findFormAccesibleByKey(UUID listKey) {
		return entityManager.find(FormAccessibleByList.class, listKey);
		/*List<FormAccessibleByUser> sharedForms = entityManager.cQuery(FormAccessibleByUser.class, "user", userKey.toString(), true);
		if(!sharedForms.isEmpty()) {
			return sharedForms.get(0);
		}
		return null;*/
	}
	
	public FormAccessibleByList persist(FormAccessibleByList formsByList) {
		try {
			entityManager.persist(formsByList);
			return formsByList;
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	public Boolean delete(FormAccessibleByList formsByList) {
		try {
			entityManager.delete(formsByList);
			return true;
		}
		catch(Exception ex) {
			return false;
		}
	}
}