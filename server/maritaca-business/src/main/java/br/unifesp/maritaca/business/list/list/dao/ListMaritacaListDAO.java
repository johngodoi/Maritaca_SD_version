package br.unifesp.maritaca.business.list.list.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;

public class ListMaritacaListDAO extends BaseDAO {
	
	private static final Log log = LogFactory.getLog(ListMaritacaListDAO.class);
	
	@Inject
	private UserDAO userDAO;
	
	public List<MaritacaList> getMaritacaListsByOwner(UUID owner) {
		return entityManager.cQuery(MaritacaList.class, "owner", owner.toString());
	}
	
	public User getUser(UUID uuid) {
		return entityManager.find(User.class, uuid);
	}
	
	public void removeMaritacaList(MaritacaList list) {
		if (!getEntityManager().delete(list)) {
			log.warn("Could not remove list: " + list.toString());
		}
	}
	
	public Collection<User> listAllUsers() {
		return entityManager.listAll(User.class);
	}
	
	public User findUserByEmail(String email) {
		return userDAO.findUserByEmail(email);
	}
	
	public List<MaritacaList> maritacaListsStartingWith(String startingString) {
		return objectsStartingWith(MaritacaList.class, startingString, "getName");
	}

	public MaritacaList getMaritacaList(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}	
}