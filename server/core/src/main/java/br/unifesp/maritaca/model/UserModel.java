package br.unifesp.maritaca.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.OAuthClient;
import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;

/**
 * Interface responsible for specifying user administration operations used by the
 * web module to interact with the persistence layer. <br>
 * This interface contains methods related to both users and lists.
 * @author tiagobarabasz
 */
@Deprecated
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
	
	MaritacaList getMaritacaList(UUID uuid);
	
	boolean saveMaritacaList(MaritacaList maritacaList);
	
	/**
	 * Search for the list with the given name. Returns null
	 * if there is none that matches.
	 * @param list
	 * @return 
	 */
	MaritacaList searchMaritacaListByName(String maritacaListName);
	
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

	Collection<MaritacaList> getMaritacaListsByOwner(User owner);

	boolean addUserToMaritacaList(User user, MaritacaList list);

	MaritacaList getAllUsersList();

	void setManagerModel(ManagerModel managerModel);

	ManagerModel getManagerModel();

	boolean userIsMemberOfMaritacaList(User user, MaritacaList list);

	Collection<MaritacaListUser> getMaritacaListByMember(User user);

	/**
	 * Returns all users that are in the given list.
	 * @param maritacaList
	 * @return A collection containing the users found.
	 */
	Collection<User> searchUsersByMaritacaList(MaritacaList maritacaList);
	
	boolean saveMaritacaListUser(MaritacaListUser maritacaListUser);
	
	/**
	 * Returns a list<MaritacaList> with the list that match the string*
	 * @param startingString
	 * @return
	 */
	List<MaritacaList> maritacaListsStartingWith(String startingString);

	/**
	 * Gets the owner's list
	 * @param list
	 * @return
	 */
	User getOwnerOfMaritacaList(MaritacaList list);
	
	 /**  
	 * Removes the current user from the given list.
	 * @param maritacaList
	 * @return true if successful, false otherwise 
	 */
	boolean removeCurrentUserFromMaritacaList(MaritacaList maritacaList);
	
	/**
	 * Removes the given user from the given list.
	 * @param list
	 * @param user
	 * @return
	 */
	boolean removeUserFromMaritacaList(MaritacaList list, User user);

	/**
	 * Removes the current user from the given list in the database. 
	 * This method cascades the deletion to the MaritacaListUser table, removing
	 * any entries that belongs to the given list.
	 * @param list
	 * @return true if successful, false otherwise
	 */
	boolean removeMaritacaList(MaritacaList list);

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
	
	/**
	 * Save the temporal Authorization Code 
	 * @param authCode
	 * @return true if successful, false otherwise
	 */
	boolean saveAuthorizationCode(OAuthCode authCode);


	OAuthClient findOauthClient(String clientId);

	/**
	 * Search the authorization code in the database
	 * @param authCode
	 * @return OAuthCode or null if not found
	 */
	OAuthCode findOauthCode(String code);

	/**
	 * Returns the Access Token object
	 * @param accessToken 
	 * @return {@link OAuthToken}
	 */
	OAuthToken getOAuthTokenByAccessToken(String accessToken);

}
