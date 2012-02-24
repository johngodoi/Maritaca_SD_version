package br.unifesp.maritaca.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;

/**
 * Interface responsible for specifying user administration operations used by the
 * web module to interact with the persistence layer. <br>
 * This interface contains methods related to both users and groups.
 * @author tiagobarabasz
 */
public interface UserModel extends GenericModel{
	boolean saveUser(User user);

	User getUser(UUID uuid);
	
	/**
	 * Returns the first user with the given email address. Email addresses
	 * should be unique in the database.
	 * 
	 * @param email
	 *            Address of the searched user.
	 * @return The user found.
	 */
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

	/**
	 * Returns all users that are in the given group.
	 * @param group
	 * @return A collection containing the users found.
	 */
	Collection<User> searchUsersByGroup(Group group);
	
	boolean saveGroupUser(GroupUser groupUser);
	
	/**
	 * Returns a list<Group> with the group that match the string*
	 * @param startingString
	 * @return
	 */
	List<Group> groupsStartingWith(String startingString);

	/**
	 * Gets the owner's group
	 * @param group
	 * @return
	 */
	User getOwnerOfGroup(Group gr);
	
	 /**  
	 * Removes the current user from the given group.
	 * @param group
	 * @return true if successful, false otherwise 
	 */
	boolean removeCurrentUserFromGroup(Group group);
	
	/**
	 * Removes the given user from the given group.
	 * @param group
	 * @param user
	 * @return
	 */
	boolean removeUserFromGroup(Group group, User user);

	/**
	 * Removes the current user from the given group in the database. 
	 * This method cascades the deletion to the GroupUser table, removing
	 * any entries that belongs to the given group.
	 * @param group
	 * @return true if successful, false otherwise
	 */
	boolean removeGroup(Group group);

	void close();

	/**
	 * Returns an user with a valid access token
	 * @param token
	 * @return User if token is valid, or null if
	 * token not exist or token have expired
	 */
	User getUserByToken(String token);
	
	/**
	 * Save a access, refresh token for an user
	 * with expiration date = current time + 3600
	 * @param token
	 * @return
	 */
	boolean saveAuthToken(OAuthToken token);
	
}
