package br.unifesp.maritaca.model.impl;

import static br.unifesp.maritaca.util.Utils.verifyEM;
import static br.unifesp.maritaca.util.Utils.verifyEntity;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.UserLocator;

public class UserModelImpl implements UserModel, Serializable {
	private static final Log log = LogFactory.getLog(UserModelImpl.class);
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
		if (group == null) {
			throw new IllegalArgumentException("Invalid group");
		}
		verifyEntity(group.getOwner());
		if (group.getName().length() == 0) {
			throw new IllegalArgumentException("Incomplete parameters");
		}

		if (group.getKey() == null) {
			// new group
			if (searchGroupByName(group.getName()) != null) {
				return false;
			}
			if (entityManager.persist(group)) {
				// add current user to group
				GroupUser grUser = new GroupUser();
				grUser.setGroup(group);
				grUser.setUser(group.getOwner());
				if (saveGroupUser(grUser))
					return true;
				else {
					// not able to add user to group
					entityManager.delete(group);
					return false;
				}
			} else
				return false; // group not saved
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
	 * Checks if a given user belongs to a group By default, a group-owner
	 * belongs to a group, but owner is not save in columnfamily GroupUser
	 * 
	 * @param user
	 * @param group
	 * @return
	 */
	@Override
	public boolean userIsMemberOfGroup(User user, Group group) {
		verifyEM(entityManager);
		verifyEntity(user);
		verifyEntity(group);

		if (group.getOwner().equals(user)) {
			// owner is default member of group
			return true;
		}

		// is not a owner, get members
		List<GroupUser> list = entityManager.cQuery(GroupUser.class, "group",
				group.getKey().toString(), true);
		for (GroupUser gu : list) {
			if (gu.getUser().equals(user))
				return true;// user is member
		}

		return false;// not a member
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
		if (currentUser == null) {
			setCurrentUser(UserLocator.getCurrentUser());
		}
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public Collection<GroupUser> getGroupsByMember(User user) {
		verifyEM(entityManager);
		verifyEntity(user);

		return entityManager.cQuery(GroupUser.class, "user", user.getKey()
				.toString(), true);
	}

	public Group searchGroupByName(String groupName) {
		if (entityManager == null || groupName == null)
			return null;

		List<Group> foundGroups = entityManager.cQuery(Group.class, "name",
				groupName);

		if (foundGroups.size() == 0) {
			return null;
		} else if (foundGroups.size() == 1) {
			return foundGroups.get(0);
		} else {
			throw new InvalidNumberOfEntries(groupName, Group.class);
		}
	}

	@Override
	public List<User> usersStartingWith(String startingString) {
		return objectsStartingWith(User.class, startingString, "getEmail");
	}

	@Override
	public List<Group> groupsStartingWith(String startingString) {
		return objectsStartingWith(Group.class, startingString, "getName");
	}

	/**
	 * Returns a list<T> with the objects that start with startingString.
	 * Returned Objects are in MINIMAL representation.
	 * 
	 * @param cl
	 * @param startingStr
	 * @param methodName
	 * @return
	 */
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

	@Override
	public User findUserByEmail(String email) {
		if (entityManager == null || email == null)
			return null;

		List<User> foundUsers = entityManager
				.cQuery(User.class, "email", email);

		if (foundUsers.size() == 0) {
			return null;
		} else if (foundUsers.size() == 1) {
			return foundUsers.get(0);
		} else {
			throw new RuntimeException("Invalid number of users for the email:"
					+ email);
		}
	}

	@Override
	public boolean saveGroupUser(GroupUser groupUser) {
		if (entityManager == null || groupUser == null)
			return false;

		return entityManager.persist(groupUser);
	}

	@Override
	public boolean removeCurrentUserFromGroup(Group group) {
		return removeUserFromGroup(group, getCurrentUser());
	}

	@Override
	public boolean removeGroup(Group group) {
		verifyEM(entityManager);
		verifyEntity(group);

		if (!removeGroupUserFromGroup(group)) {
			log.warn("Could not remove group: " + group.toString());
			return false;
		} else {
			return entityManager.delete(group);
		}
	}

	/**
	 * Removes every entry in GroupsUser from the given group.
	 * 
	 * @param group
	 * @return true if successful, false otherwise
	 */
	private boolean removeGroupUserFromGroup(Group group) {
		verifyEM(entityManager);
		verifyEntity(group);
		List<GroupUser> groupsUserFromUser;
		groupsUserFromUser = entityManager.cQuery(GroupUser.class, "group",
				group.getKey().toString());

		for (GroupUser groupUser : groupsUserFromUser) {
			if (groupUser.getGroup().equals(group.getKey())) {
				if (!entityManager.delete(groupUser)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public Collection<User> searchUsersByGroup(Group group) {
		String groupKey = group.getKey().toString();
		List<GroupUser> groupsUserFromUser = entityManager.cQuery(
				GroupUser.class, "group", groupKey);
		Collection<User> foundUsers = new ArrayList<User>();

		for (GroupUser groupUser : groupsUserFromUser) {
			User user = entityManager.find(User.class, groupUser.getUser()
					.getKey());
			foundUsers.add(user);
		}
		return foundUsers;
	}

	@Override
	public boolean removeUserFromGroup(Group group, User user) {
		verifyEM(entityManager);
		verifyEntity(group);

		List<GroupUser> groupsUserFromUser = new ArrayList<GroupUser>();
		groupsUserFromUser = entityManager.cQuery(GroupUser.class, "user", user
				.getKey().toString());

		for (GroupUser groupUser : groupsUserFromUser) {
			if (groupUser.getGroup().getKey().equals(group.getKey())) {
				if (!entityManager.delete(groupUser)) {
					log.warn("Couldn't delete groupUser: "	+ groupUser.toString());
					return false;
				} else {
					return true;
				}
			}
		}
		log.warn("user " + user + " cannot be deleted from group " + group);
		return false; // User is not in the given group
	}

	@Override
	public void close() {
		entityManager = null;
		currentUser = null;
		managerModel = null;
	}

	/**
	 * Returns the full data of a group's owner
	 */
	@Override
	public User getOwnerOfGroup(Group gr) {
		if (gr.getOwner() != null) {
			return getUser(gr.getOwner().getKey());
		} else {
			Group group = entityManager.find(Group.class, gr.getKey(), true);
			if (group != null) {
				return getOwnerOfGroup(group);
			} else {
				return null;
			}
		}
	}
}
