package br.unifesp.maritaca.model.impl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.UserLocator;

public class UserModelImpl implements UserModel {

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
		if (user == null)
			return false;
		if (entityManager.persist(user)) {
			return addUserToGroup(user, getAllUsersGroup());
		}
		return false;

	}

	@Override
	public User getUser(UUID uuid) {
		if (entityManager == null)
			return null;

		return entityManager.find(User.class, uuid);
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}

	@Override
	public Collection<User> listAllUsers() {
		if (entityManager == null)
			return null;

		return entityManager.listAll(User.class);
	}

	@Override
	public Collection<User> listAllUsersMinimal() {
		if (entityManager == null)
			return null;

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
		if (entityManager == null)
			return null;
		List<User> users = entityManager.cQuery(User.class, "email", email);
		if (users == null || users.size() == 0) {
			return null;
		} else {
			return users.get(0);
		}
	}

	@Override
	public Group getGroup(UUID uuid) {
		if (entityManager == null)
			return null;
		return entityManager.find(Group.class, uuid);
	}

	@Override
	public boolean saveGroup(Group group) {
		if (entityManager == null)
			return false;
		if (group == null || group.getOwner() == null
				|| group.getName() == null || group.getName().length() == 0) {
			throw new IllegalArgumentException("Incomplete parameters");
		}

		if (group.getKey() == null) {
			// new group
			for (Group g : getGroupsByOwner(group.getOwner())) {
				// verify in there is not another group with the same name for
				// owner
				if (group.getName().equals(g.getName())) {
					// group exists
					return true;
				}
			}
			return entityManager.persist(group);
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

	@Override
	public Collection<Group> getGroupsByOwner(User owner) {
		if (entityManager == null)
			return null;
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
		if (user == null || user.getKey() == null || group == null
				|| group.getKey() == null) {
			throw new IllegalArgumentException("parameters incompleted");
		}
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
		if (entityManager == null)
			return false;

		if (user == null || user.getKey() == null || group == null
				|| group.getKey() == null) {
			throw new IllegalArgumentException("parameters incompleted");
		}
		
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

}
