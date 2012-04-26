package br.unifesp.maritaca.business.base.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormAccessibleByList;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.persistence.permission.Policy;

/**
 * 
 * @author jimvalsan
 *
 */
public class FormDAO extends BaseDAO {
	
	public Collection<MaritacaList> getAllMaritacaLists() {
		return entityManager.listAll(MaritacaList.class, false);
	}
	
	public Collection<MaritacaList> getMaritacaListsByOwner(UUID key) {
		return entityManager.cQuery(MaritacaList.class, "owner", key.toString());
	}
	
	public Collection<MaritacaList> getMaritacaListsByName(String name) {
		return entityManager.cQuery(MaritacaList.class, "name", name);
	}
	
	public MaritacaList getMaritacaListByKey(UUID mListKey, boolean minimal) {		
		return entityManager.find(MaritacaList.class, mListKey, minimal);
	}
	
	//////
	
	public void persistForm(Form form) {
		entityManager.persist(form);		
		//createRandownAnswer(form);
	}
	
	public void delete(Form form) {
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
		return entityManager.cQuery(Form.class, "user", userKey, false);//true		
	}
		
	public List<Form> getAllSharedFormsByUserKey(String userKey) {
		List<Form> forms = new ArrayList<Form>();
		List<Form> publicForms = getAllPublicLists();
		if(publicForms != null && !publicForms.isEmpty())
			forms.addAll(publicForms);
		forms.addAll(getListSharedFormsByUserKey(userKey));
		return forms;		
	}
	/**
	 * 
	 * @param userKey
	 * @return List<Form>
	 */
	private List<Form> getListSharedFormsByUserKey(String userKey) {	
		List<Form> forms = new ArrayList<Form>();
		
		List<FormAccessibleByList> sharedForms = getFormAccessibleByUser(userKey);
		for(FormAccessibleByList formByList : sharedForms) {
			for(UUID f : formByList.getForms()) {
				Form form = entityManager.find(Form.class, f);
				if(!forms.contains(form)) {
					forms.add(form);
				}
			}
		}
		return forms;
	}
	
	private List<Form> getAllPublicLists() {
		return entityManager.cQuery(Form.class, "policy", Policy.PUBLIC.toString(), false);
	}
	
	private List<FormAccessibleByList> getFormAccessibleByUser(String strUserKey) {
		UUID userKey = UUID.fromString(strUserKey);
		List<FormAccessibleByList> forms = new ArrayList<FormAccessibleByList>();
		
		List<MaritacaList> mLists = new ArrayList<MaritacaList>(getAllMaritacaLists());			
		for(MaritacaList mlist : mLists) {
			if(mlist.getUsers() != null && !mlist.getName().equals(MaritacaConstants.ALL_USERS) && mlist.getUsers().contains(userKey)) {
				FormAccessibleByList form = this.findFormAccesibleByKey(mlist.getKey());
				if(form !=null && !forms.contains(form))
					forms.add(form);
			}		
		}
		return forms;
	}
	
	private FormAccessibleByList findFormAccesibleByKey(UUID listKey) {
		return entityManager.find(FormAccessibleByList.class, listKey);
	}
}