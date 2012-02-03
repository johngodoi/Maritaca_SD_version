package br.unifesp.maritaca.model.impl;

import static br.unifesp.maritaca.util.Utils.verifyEM;
import static br.unifesp.maritaca.util.Utils.verifyEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.UserLocator;

public class UserModelImpl implements UserModel, Serializable {

	private static final long serialVersionUID = 1L;
	
	private EntityManager entityManager;
	private ManagerModel managerModel;
	private User currentUser;

	public UserModelImpl() {
		setCurrentUser(UserLocator.getCurrentUser());
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public boolean saveUser(User user) {
		verifyEM(entityManager);
		if (user == null)
			return false;
		if (entityManager.persist(user)) {
			return addUserToGroup(user, getAllUsersGroup());
		}
		return false;
	}

	@Override
	public User getUser(UUID uuid) {
		verifyEM(entityManager);

		return entityManager.find(User.class, uuid);
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}

	@Override
	public Collection<User> listAllUsers() {
		verifyEM(entityManager);

		return entityManager.listAll(User.class);
	}

	@Override
	public Collection<User> listAllUsersMinimal() {
		verifyEM(entityManager);

		return entityManager.listAll(User.class, true);
	}

	/**
	 * Returns the first user with the given email address. Email addresses
	 * should be unique in the database.
	 * 
	 * @param email
	 *            Address of the searched user.
	 * @return The user found.
	 */
	@Override
	public User getUser(String email) {
		verifyEM(entityManager);
		List<User> users = entityManager.cQuery(User.class, "email", email);
		if (users == null || users.size() == 0) {
			return null;
		} else {
			return users.get(0);
		}
	}

	@Override
	public Group getGroup(UUID uuid) {
		verifyEM(entityManager);
		return entityManager.find(Group.class, uuid);
	}

	@Override
	public boolean saveGroup(Group group) {
		verifyEM(entityManager);
		if (group == null || group.getOwner() == null
				|| group.getName() == null || group.getName().length() == 0) {
			throw new IllegalArgumentException("Incomplete parameters");
		}

		if (group.getKey() == null) {
			// new group
			if(searchGroupByName(group.getName())!=null){
				return false;
			}
			if(entityManager.persist(group)){
			//add current user to group
				GroupUser grUser = new GroupUser();
				grUser.setGroup(group);
				grUser.setUser(group.getOwner());
				if(saveGroupUser(grUser))
					return true;
				else{
					//not able to add user to group
					entityManager.delete(group);
					return false;
				}
			}else
				return false; //group not saved
			
		} else {
			// look for group
			Group g = getGroup(group.getKey());
			if (g == null) {
				// new group
				group.setKey("");
				return saveGroup(group);
			} else
				// update name
				return entityManager.persist(group);
		}
	}	
	

	/**
	 * @return List of group which the user is owner
	 */
	@Override
	public Collection<Group> getGroupsByOwner(User owner) {
		verifyEM(entityManager);
		verifyEntity(owner);
		return entityManager.cQuery(Group.class, "owner", owner.toString());
	}

	/**
	 * Adds an user to a particular group
	 * 
	 * @param user
	 * @param group
	 * @return true if was successful
	 */
	@Override
	public boolean addUserToGroup(User user, Group group) {
		verifyEM(entityManager);
		verifyEntity(user);
		verifyEntity(group);
		
		GroupUser grUser = new GroupUser();
		grUser.setGroup(group);
		grUser.setUser(user);
		return entityManager.persist(grUser);
	}

	/**
	 * Checks if a given user belongs to a group
	 * By default, a group-owner belongs to a group, but
	 * owner is not save in columnfamily GroupUser
	 * @param user
	 * @param group
	 * @return
	 */
	@Override
	public boolean userIsMemberOfGroup(User user, Group group) {
		verifyEM(entityManager);
		verifyEntity(user);
		verifyEntity(group);
		
		if(group.getOwner().equals(user)){
			//owner is default member of group
			return true;
		}

		//is not a owner, get members
		List<GroupUser> list = entityManager.cQuery(GroupUser.class, "group",
				group.getKey().toString(), true);
		for (GroupUser gu : list) {
			if (gu.getUser().equals(user))
				return true;//user is member
		}

		return false;//not a member
	}

	/**
	 * Get default users group (ALL_USERS)
	 */
	@Override
	public Group getAllUsersGroup() {
		User root = managerModel.getRootUser();
		for (Group g : getGroupsByOwner(root)) {
			if (g.getName().equals(ManagerModel.ALL_USERS)) {
				return g;
			}
		}
		return null;
	}

	@Override
	public ManagerModel getManagerModel() {
		return managerModel;
	}

	@Override
	public void setManagerModel(ManagerModel managerModel) {
		this.managerModel = managerModel;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public Collection<GroupUser> getGroupsByMember(User user) {
		verifyEM(entityManager);
		verifyEntity(user);
		
		return entityManager.cQuery(GroupUser.class, "user",
				user.getKey().toString(), true);
	}

	public Group searchGroupByName(String groupName) {
		if (entityManager == null || groupName == null)
			return null;
		
		List<Group> foundGroups = entityManager.cQuery(Group.class, "name", groupName);
		
		if(foundGroups.size()==0){
			return null;
		} else if (foundGroups.size()==1){
			return foundGroups.get(0);
		} else {
			throw new RuntimeException("Invalid number of groups with name:"+groupName);
		}
	}

	@Override
	public List<User> usersStartingWith(String startingString) {
		Collection<User> listUser       = listAllUsers();		
		List<User>       matchingEmails = new ArrayList<User>();
		
		for(User u : listUser){
			if(u.getEmail().matches("^"+startingString+".*")){
				matchingEmails.add(u);
			}
		}		
		return matchingEmails;
	}

	@Override
	public User findUserByEmail(String email) {		
		if (entityManager == null || email == null)
			return null;
		
		List<User> foundUsers = entityManager.cQuery(User.class, "email", email);
		
		if(foundUsers.size()==0){
			return null;
		} else if (foundUsers.size()==1){
			return foundUsers.get(0);
		} else {
			throw new RuntimeException("Invalid number of users for the email:"+email);
		}
	}

	@Override
	public boolean saveGroupUser(GroupUser groupUser) {
		if (entityManager == null || groupUser == null)
			return false;
		
		return entityManager.persist(groupUser);
	}
	
	@Override
	public void close() {
		entityManager = null;
		currentUser = null;
		managerModel = null;
	}
}
