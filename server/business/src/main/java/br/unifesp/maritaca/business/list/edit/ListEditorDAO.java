package br.unifesp.maritaca.business.list.edit;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.User;

public class ListEditorDAO extends BaseDAO {
	
	public void saveMaritacaList(MaritacaList list) {
		entityManager.persist(list);
	}

	public MaritacaList searchMaritacaListByName(String listName) {
		if (listName == null) {
			throw new IllegalArgumentException();
		}

		List<MaritacaList> foundGroups;
		foundGroups = getEntityManager().cQuery(MaritacaList.class, "name",
				listName);

		if (foundGroups.size() == 0) {
			return null;
		} else if (foundGroups.size() == 1) {
			return foundGroups.get(0);
		} else {
			throw new InvalidNumberOfEntries(MaritacaList.class, "name", 
					listName, foundGroups.size());
		}
	}

	public MaritacaList searchMaritacaListByName(String listName, UUID owner) {
		List<MaritacaList> foundLists;
		foundLists = entityManager.cQuery(MaritacaList.class, "owner",
				owner.toString());
		for (MaritacaList list : foundLists) {
			if (list.getName() != null && list.getName().equals(listName)) {
				return list;
			}
		}
		return null;
	}
		
	public List<User> usersStartingWith(String startingString) {
		return objectsStartingWith(User.class, startingString, "getEmail");
	}
	
	public MaritacaList getMaritacaList(UUID uuid) {
		return entityManager.find(MaritacaList.class, uuid);
	}
}
