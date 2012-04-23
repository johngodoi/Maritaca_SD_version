package br.unifesp.maritaca.business.base.dao;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.core.Form;

/**
 * 
 * @author jimvalsan
 *
 */
public class FormDAO extends BaseDAO {
	
	public void persistForm(Form form) {
		entityManager.persist(form);		
		//createRandownAnswer(form);
	}
	
	public void delete(Form form) {
		System.out.println("delete");
		entityManager.delete(form);
	}
	
	/**
	 * 
	 * @param key
	 * @param minimal
	 * @return Form
	 */
	public Form getFormByKey(UUID formKey, boolean minimal) {
		if (formKey == null ) {
			throw new IllegalArgumentException("Incomplete parameters");
		}
		return entityManager.find(Form.class, formKey, minimal);
	}
	
	public boolean verifyIfUrlExist(String url) {
		// TODO: improve this
		// look for url in the Form columnFamily
		List<Form> fsList = entityManager.cQuery(Form.class, "url", url, true);
		return fsList.size() > 0;
	}
	
	/**
	 * 
	 * @param userKey
	 * @return List<Form>
	 */
	public List<Form> getListOwnFormsByUserKey(String userKey) {
		return entityManager.cQuery(Form.class, "user", userKey, false);		
	}
	
	public List<Form> getListSharedFormsByUserKey(String userKey) {
		return entityManager.cQuery(Form.class, "user", userKey, false);		
	}
}