package br.unifesp.maritaca.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;

public interface UserModel {
	boolean saveUser(User user);

	User getUser(UUID uuid);
	
	User getUser(String email);

	void setEntityManager(EntityManager em);

	Collection<User> listAllUsers();

	Collection<User> listAllUsersMinimal();
	
	Group getGroup(UUID uuid);
	
	boolean saveGroup(Group group);
	
	/**
	 * Search for the group with the given name. Returns null
	 * if there is none that matches.
	 * @param group
	 * @return 
	 */
	Group searchGroupByName(String groupName);
	
	/**
	 * Returns a list of users whose emails starts with
	 * the given string.
	 * @param startingString
	 * @return
	 */
	List<User> usersStartingWith(String startingString);
	
	/**
	 * Find user by its email. If no user is found with the given
	 * email returns null. <br>
	 * Note: For one given email there must be only one user.
	 * @param email
	 * @return The user or null if no user is found.
	 */
	User findUserByEmail(String email);	

	Collection<Group> getGroupsByOwner(User owner);

	boolean addUserToGroup(User user, Group group);

	Group getAllUsersGroup();

	void setManagerModel(ManagerModel managerModel);

	ManagerModel getManagerModel();

	boolean userIsMemberOfGroup(User user, Group group);

	Collection<GroupUser> getGroupsByMember(User user);

	boolean saveGroupUser(GroupUser groupUser);

	void close();
}
