package br.unifesp.maritaca.business.list.dao;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.business.form.list.dao.FormListerDAO;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.User;

public class ManagerListDAO extends BaseDAO {
	
	private static final Log log = LogFactory.getLog(ManagerListDAO.class);
	
	@Inject FormListerDAO fomListerDAO;
	@Inject UserDAO userDAO;
	
	public Collection<MaritacaList> getMaritacaListsByOwner(User owner) {
		//verifyEntity(owner);
		return entityManager.cQuery(MaritacaList.class, "owner", owner.toString());
	}
	
	public User getUser(UUID uuid) {
		return entityManager.find(User.class, uuid);
	}
	
	public User getUserByKey(String email) {
		User user = userDAO.findUserByEmail(email);
		System.out.println("opa: " + user.getEmail());
		return user;
	}
	
	public MaritacaList getMaritacaList(UUID uuid) {
		return entityManager.find(MaritacaList.class, uuid);
	}
	
	public boolean removeMaritacaList(MaritacaList group) {
		//verifyEntity(group);

		if (!removeGroupUserFromGroup(group)) {
			log.warn("Could not remove group: " + group.toString());
			return false;
		} else {
			return entityManager.delete(group);
		}
	}
	
	private boolean removeGroupUserFromGroup(MaritacaList group) {
		//verifyEntity(group);
		List<MaritacaListUser> groupsUserFromUser;
		groupsUserFromUser = entityManager.cQuery(MaritacaListUser.class, "maritacaList",
				group.getKey().toString());

		for (MaritacaListUser groupUser : groupsUserFromUser) {
			if (groupUser.getMaritacaList().equals(group.getKey())) {
				if (!entityManager.delete(groupUser)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public MaritacaList searchMaritacaListByName(String listName) {
		if (entityManager == null || listName == null)
			return null;

		List<MaritacaList> foundGroups = entityManager.cQuery(MaritacaList.class, "name",
				listName);

		if (foundGroups.size() == 0) {
			return null;
		} else if (foundGroups.size() == 1) {
			return foundGroups.get(0);
		} else {
			throw new InvalidNumberOfEntries(listName, MaritacaList.class);
		}
	}
			
	public Collection<User> searchUsersByMaritacaList(MaritacaList group) {
		//TODO: 
		/* if(group.getOwner()==null){
			throw new IllegalArgumentException("Invalid group 1");
		}*/
		
		String           groupKey        = group.getKey().toString();
		List<MaritacaListUser>  grpsUsrFromUser = entityManager.cQuery(MaritacaListUser.class, "maritacaList", groupKey);
		Collection<User> foundUsers      = new ArrayList<User>();

		if(group.equals(fomListerDAO.getAllUsersList())){
			return listAllUsers();
		}
		
		for (MaritacaListUser groupUser : grpsUsrFromUser) {
			User user = entityManager.find(User.class, groupUser.getUser().getKey());
			foundUsers.add(user);
		}
				
		/*if(!foundUsers.contains(group.getOwner())){
			log.error("Group: " + group + " does not contain its owner");
			throw new RuntimeException("Group owner not in group");			
		}*/
		
		return foundUsers;
	}
	
	public Collection<User> listAllUsers() {
		return entityManager.listAll(User.class);
	}
	
	public User findUserByEmail(String email) {
		return userDAO.findUserByEmail(email);
	}
	
	public List<User> usersStartingWith(String startingString) {
		return objectsStartingWith(User.class, startingString, "getEmail");
	}
	
	public List<MaritacaList> maritacaListsStartingWith(String startingString) {
		return objectsStartingWith(MaritacaList.class, startingString, "getName");
	}
	
	private <T> List<T> objectsStartingWith(Class<T> cl, String startingStr,
			String methodName) {
		ArrayList<T> result = new ArrayList<T>(0);
		try {
			// TODO: improve this. Retrieving all elements in a column family
			// is expensive with big collections.
			Method method = cl.getMethod(methodName);
			List<T> resultEM = entityManager.listAll(cl, true);
			for (T obj : resultEM) {

				String value = (String) method.invoke(obj);
				if (value != null && value.matches("^" + startingStr + ".*")) {
					result.add(obj);
				}
			}
		} catch (Exception e) {
			log.error("Exception executing the method " + methodName
					+ " in the class " + cl.getSimpleName());
		}

		return result;
	}
	
	public boolean saveMaritacaList(MaritacaList list) {
		if (list == null) {
			throw new IllegalArgumentException("Invalid group 2");
		}
		//verifyEntity(list.getOwner());
		if (list.getName()==null || list.getName().length() == 0) {
			throw new IllegalArgumentException("Incomplete parameters");
		}

		if (list.getKey() == null) {
			// new group
			if (searchMaritacaListByName(list.getName()) != null) {
				return false;
			}
			if (entityManager.persist(list)) {
				// add owner to group
				MaritacaListUser listUser = new MaritacaListUser();
				listUser.setMaritacaList(list);
				listUser.setUser(list.getOwner());
				if (saveMaritacaListUser(listUser))
					return true;
				else {
					// not able to add user to group
					entityManager.delete(list);
					return false;
				}
			} else
				return false; // group not saved
		} else {
			return entityManager.persist(list);
		}
	}
	
	public boolean saveMaritacaListUser(MaritacaListUser groupUser) {
		if (entityManager == null || groupUser == null)
			return false;
		return entityManager.persist(groupUser);
	}
	
	public boolean saveListUsers(MaritacaList list, List<User> lstUsers) {
		List<User> users = new ArrayList<User>(this.searchUsersByMaritacaList(list));		
		for(User usr : lstUsers){
			if(!users.contains(usr)){
				this.saveMaritacaListUser(newListUser(usr,list));
			}
		}
		for(User usr : users){
			if(!lstUsers.contains(usr)){
				this.removeUserFromMaritacaList(list,usr);
			}
		}		
		return true;
	}
	
	public boolean removeUserFromMaritacaList(MaritacaList group, User user) {
		//verifyEntity(group);
		List<MaritacaListUser> groupsUserFromUser = new ArrayList<MaritacaListUser>();
		groupsUserFromUser = entityManager.cQuery(MaritacaListUser.class, "user", user
				.getKey().toString());

		for (MaritacaListUser groupUser : groupsUserFromUser) {
			if (groupUser.getMaritacaList().getKey().equals(group.getKey())) {
				if (!entityManager.delete(groupUser)) {
					log.warn("Couldn't delete groupUser: "
							+ groupUser.toString());
					return false;
				} else {
					return true;
				}
			}
		}
		log.warn("user " + user + " cannot be deleted from group " + group);
		return false; // User is not in the given group
	}
	
	private MaritacaListUser newListUser(User usr, MaritacaList list){
		MaritacaListUser listUser = new MaritacaListUser();
		listUser.setUser(usr);
		listUser.setMaritacaList(list);
		
		return listUser;
	}
}