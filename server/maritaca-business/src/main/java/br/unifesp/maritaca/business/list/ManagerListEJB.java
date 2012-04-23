package br.unifesp.maritaca.business.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.form.list.dao.FormListerDAO;
import br.unifesp.maritaca.business.list.dao.ManagerListDAO;
import br.unifesp.maritaca.business.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

@Stateless
public class ManagerListEJB {
	
	@Inject private ManagerListDAO managerListDAO;
	@Inject private FormListerDAO formListerDAO;
	@Inject private UserDAO userDAO;
	private static final Log log = LogFactory.getLog(ManagerListEJB.class);

	public Collection<MaritacaListDTO> getMaritacaListsByOwner(UserDTO userDTO) {
		List<MaritacaListDTO> listDTOs = null;
		User user = new User();
		user.setKey(userDTO.getKey());
		Collection<MaritacaList> ownedLists = managerListDAO.getMaritacaListsByOwner(user);		
		ownedLists = this.removeAllUsersLists(ownedLists);
		ownedLists = this.removeUserList(ownedLists, userDTO.getEmail());		
		ownedLists = this.fillOwnerEmail(ownedLists);
		if(!ownedLists.isEmpty()) {
			listDTOs = new ArrayList<MaritacaListDTO>();
			for(MaritacaList l : ownedLists) {
				MaritacaListDTO list = new MaritacaListDTO(l.getKey(), l.getName(), l.getDescription(), l.getOwner().getEmail());				
				listDTOs.add(list);
			}
		}		
		return listDTOs;		
	}
	
	private Collection<MaritacaList> fillOwnerEmail(Collection<MaritacaList> ownedLists) {
		for(MaritacaList grp : ownedLists){
			User owner = managerListDAO.getUser(grp.getOwner().getKey());
			grp.setOwner(owner);
		}
		return ownedLists;
	}

	private Collection<MaritacaList> removeUserList(Collection<MaritacaList> ownedLists, String email) {
		User user = userDAO.findUserByEmail(email);
		MaritacaList userList = user.getMaritacaList();
		if(!ownedLists.remove(userList)){
			log.error("User list for user "+user+" not found");
		}
		return ownedLists;
	}

	private Collection<MaritacaList> removeAllUsersLists(Collection<MaritacaList> ownedLists) {
		MaritacaList allUsrGrp = formListerDAO.getAllUsersList();
		ownedLists.remove(allUsrGrp);
		return ownedLists;
	}
	
	public boolean removeMaritacaList(MaritacaListDTO list) {
		MaritacaList maritacaList = managerListDAO.getMaritacaList(list.getKey());
		return managerListDAO.removeMaritacaList(maritacaList);
	}
	
	public MaritacaList searchMaritacaListByName(String listName) {
		return managerListDAO.searchMaritacaListByName(listName);
	}
	
	public Collection<User> searchUsersByMaritacaList(MaritacaList list) {
		return managerListDAO.searchUsersByMaritacaList(list);
	}
	
	public Collection<User> searchUsersByMaritacaList(MaritacaListDTO listDTO) {
		MaritacaList list = managerListDAO.getMaritacaList(listDTO.getKey());
		return managerListDAO.searchUsersByMaritacaList(list);
	}
	
	public User findUserByEmail(String email) {
		return managerListDAO.findUserByEmail(email);
	}
	
	public List<User> usersStartingWith(String startingString) {
		return managerListDAO.usersStartingWith(startingString);
	}
	
	public boolean saveMaritacaList(MaritacaList list) {
		return managerListDAO.saveMaritacaList(list);
	}
	
	public boolean saveListUsers(MaritacaList list, List<User> lstUsers) {
		return managerListDAO.saveListUsers(list, lstUsers);
	}
}