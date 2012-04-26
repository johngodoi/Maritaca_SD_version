package br.unifesp.maritaca.business.list.edit.dao;

import java.util.List;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;


public class ListEditorDAO extends BaseDAO{
	
	public void saveMaritacaList(MaritacaList list) {
		if (newList(list)) {
			if (searchMaritacaListByName(list.getName()) != null) {
				throw new InvalidNumberOfEntries(MaritacaList.class, "name", list.getName());
			}
			getEntityManager().persist(list);
			
			addListOwnerToList(list);
		} else {
			getEntityManager().persist(list);
		}
	}				

	public MaritacaList searchMaritacaListByName(String groupName) {
		if (groupName == null){
			throw new IllegalArgumentException();
		}			
		
		List<MaritacaList> foundGroups;
		foundGroups = getEntityManager().cQuery(MaritacaList.class, "name",groupName);
		
		if (foundGroups.size() == 0) {
			return null;
		} else if (foundGroups.size() == 1) {
			return foundGroups.get(0);
		} else {
			throw new InvalidNumberOfEntries(MaritacaList.class, "name", groupName);
		}
	}
	
	private void addListOwnerToList(MaritacaList list) {
		MaritacaListUser listUser = new MaritacaListUser();
		listUser.setMaritacaList(list);
		listUser.setUser(list.getOwner());
		getEntityManager().persist(listUser);		
	}
	
	private boolean newList(MaritacaList list) {
		return list.getKey() == null;
	}
}
