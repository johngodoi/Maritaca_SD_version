package br.unifesp.maritaca.model.impl;

import static br.unifesp.maritaca.util.UtilsCore.verifyEntity;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.OAuthClient;
import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.InvalidNumberOfEntries;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.UseEntityManager;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.util.UserLocator;

//TODO Ticket: 113 - Use exceptions instead of returning false
public class UserModelImpl implements UserModel, Serializable, UseEntityManager {
	private static final Log log = LogFactory.getLog(UserModelImpl.class);
	private static final long serialVersionUID = 1L;

	private EntityManager entityManager;
	private ManagerModel managerModel;
	private User currentUser;

	public UserModelImpl() {
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public boolean saveUser(User user) {
		if (user == null){
			return false;
		}
		
		if(!updateUserGroup(user)){
			return false;
		}
		
		if (!entityManager.persist(user)) {
			return false;
		}		
		if(user.getMaritacaList()==null && !createUserGroup(user)){
			return false;
		}		
		return true;
	}

	/*
	 * Update the user group name if the user changed its email
	 */
	private boolean updateUserGroup(User user) {
		if(user.getKey()==null){
			return true;
		}		
		User dbUser = getUser(user.getKey());
		if(dbUser.getEmail().equals(user.getEmail())){
			return true;
		} else {
			MaritacaList userGroup = searchMaritacaListByName(dbUser.getEmail());
			userGroup.setName(user.getEmail());
			
			return entityManager.persist(userGroup);
		}		
	}


	private boolean createUserGroup(User owner) {		
		MaritacaList list = new MaritacaList();
		list.setOwner(owner);
		list.setName(owner.getEmail());
		
		if(!saveMaritacaList(list)){
			return false;
		}				
		
		owner.setMaritacaList(list);
		
		return saveUser(owner);
	}

	@Override
	public User getUser(UUID uuid) {
		return entityManager.find(User.class, uuid);
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}

	@Override
	public Collection<User> listAllUsers() {
		return entityManager.listAll(User.class);
	}

	@Override
	public Collection<User> listAllUsersMinimal() {
		return entityManager.listAll(User.class, true);
	}

	public User getUser(String email) {
		List<User> users = entityManager.cQuery(User.class, "email", email);
		if (users == null || users.size() == 0) {
			return null;
		} else if ( users.size() == 1 ){
			return users.get(0);
		} else {
			throw new InvalidNumberOfEntries(email, User.class);
		}
	}

	@Override
	public MaritacaList getMaritacaList(UUID uuid) {
		return entityManager.find(MaritacaList.class, uuid);
	}

	@Override
	public boolean saveMaritacaList(MaritacaList list) {
		if (list == null) {
			throw new IllegalArgumentException("Invalid group");
		}
		verifyEntity(list.getOwner());
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

	/**
	 * @return List of group which the user is owner
	 */
	@Override
	public Collection<MaritacaList> getMaritacaListsByOwner(User owner) {
		verifyEntity(owner);
		return entityManager.cQuery(MaritacaList.class, "owner", owner.toString());
	}

	/**
	 * Adds an user to a particular group
	 * 
	 * @param user
	 * @param group
	 * @return true if was successful
	 */
	@Override
	public boolean addUserToMaritacaList(User user, MaritacaList group) {
		verifyEntity(user);
		verifyEntity(group);

		MaritacaListUser grUser = new MaritacaListUser();
		grUser.setMaritacaList(group);
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
	public boolean userIsMemberOfMaritacaList(User user, MaritacaList group) {
		verifyEntity(user);
		verifyEntity(group);

		if (group.getOwner().equals(user)) {
			// owner is default member of group
			return true;
		}
		
		if( group.equals(getAllUsersList()) ){
			// all users are in the public group
			return true;
		}

		// is not a owner, get members
		List<MaritacaListUser> list = entityManager.cQuery(MaritacaListUser.class, "maritacaList",
				group.getKey().toString(), true);
		for (MaritacaListUser gu : list) {
			if (gu.getUser().equals(user))
				return true;// user is member
		}

		return false;// not a member
	}

	/**
	 * Get default users group (ALL_USERS)
	 */
	@Override
	public MaritacaList getAllUsersList() {
		User root = managerModel.getRootUser();
		for (MaritacaList g : getMaritacaListsByOwner(root)) {
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
	public Collection<MaritacaListUser> getMaritacaListByMember(User user) {
		verifyEntity(user);

		MaritacaListUser allGroupUser = new MaritacaListUser();
		allGroupUser.setUser(user);
		allGroupUser.setMaritacaList(getAllUsersList());
		
		Collection<MaritacaListUser> groupsByMember;
		groupsByMember =  entityManager.cQuery(MaritacaListUser.class, "user", user.getKey()
												.toString(), true);
		groupsByMember.add(allGroupUser);
		
		return groupsByMember;
	}

	public MaritacaList searchMaritacaListByName(String groupName) {
		if (entityManager == null || groupName == null)
			return null;

		List<MaritacaList> foundGroups = entityManager.cQuery(MaritacaList.class, "name",
				groupName);

		if (foundGroups.size() == 0) {
			return null;
		} else if (foundGroups.size() == 1) {
			return foundGroups.get(0);
		} else {
			throw new InvalidNumberOfEntries(groupName, MaritacaList.class);
		}
	}

	@Override
	public List<User> usersStartingWith(String startingString) {
		return objectsStartingWith(User.class, startingString, "getEmail");
	}

	@Override
	public List<MaritacaList> maritacaListsStartingWith(String startingString) {
		return objectsStartingWith(MaritacaList.class, startingString, "getName");
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
	public boolean saveMaritacaListUser(MaritacaListUser groupUser) {
		if (entityManager == null || groupUser == null)
			return false;

		return entityManager.persist(groupUser);
	}

	@Override
	public boolean removeCurrentUserFromMaritacaList(MaritacaList group) {
		return removeUserFromMaritacaList(group, getCurrentUser());
	}

	@Override
	public boolean removeMaritacaList(MaritacaList group) {
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
	private boolean removeGroupUserFromGroup(MaritacaList group) {
		verifyEntity(group);
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

	@Override
	public Collection<User> searchUsersByMaritacaList(MaritacaList group) {
		if(group.getOwner()==null){
			throw new IllegalArgumentException("Invalid group");
		}
		
		String           groupKey        = group.getKey().toString();
		List<MaritacaListUser>  grpsUsrFromUser = entityManager.cQuery(MaritacaListUser.class, "maritacaList", groupKey);
		Collection<User> foundUsers      = new ArrayList<User>();

		if(group.equals(getAllUsersList())){
			return listAllUsers();
		}
		
		for (MaritacaListUser groupUser : grpsUsrFromUser) {
			User user = entityManager.find(User.class, groupUser.getUser().getKey());
			foundUsers.add(user);
		}
				
		if(!foundUsers.contains(group.getOwner())){
			log.error("Group: " + group + " does not contain its owner");
			throw new RuntimeException("Group owner not in group");			
		}
		
		return foundUsers;
	}

	@Override
	public boolean removeUserFromMaritacaList(MaritacaList group, User user) {
		verifyEntity(group);

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
	public User getOwnerOfMaritacaList(MaritacaList gr) {
		if (gr.getOwner() != null) {
			return getUser(gr.getOwner().getKey());
		} else {
			MaritacaList group = entityManager.find(MaritacaList.class, gr.getKey(), true);
			if (group != null) {
				return getOwnerOfMaritacaList(group);
			} else {
				return null;
			}
		}
	}

	@Override
	public boolean saveAuthorizationCode(OAuthCode oAuthCode) {
		if (oAuthCode == null)
			return false;

		//TODO: verify that this code must be unique
		return entityManager.persist(oAuthCode);
	}

	@Override
	public User getUserByToken(String token) {
		List<OAuthToken> oauth = entityManager.cQuery(OAuthToken.class,
				"accessToken", token);
		for (OAuthToken oauthtoken : oauth) {
			return entityManager
					.find(User.class, oauthtoken.getUser().getKey());
		}
		return null;
	}

	@Override
	public boolean saveAuthToken(OAuthToken token) {
		return entityManager.persist(token);
	}

	@Override
	public OAuthClient findOauthClient(String clientId) {
		List<OAuthClient> list = entityManager.cQuery(OAuthClient.class,
				"clientId", clientId);
		for (OAuthClient client : list) {
			return client;// always returns the first
		}
		return null;
	}

	@Override
	public OAuthCode findOauthCode(String authCode) {
		if (authCode == null) {
			return null;
		}

		List<OAuthCode> foundCode = entityManager.cQuery(OAuthCode.class,
				"code", authCode);

		for(OAuthCode code: foundCode){
			return code; //return first
		}
		
		return null;
	}

	@Override
	public OAuthToken getOAuthTokenByAccessToken(String accessToken) {
		List<OAuthToken> list = entityManager.cQuery(OAuthToken.class,
				"accessToken", accessToken);
		for (OAuthToken token : list) {
			return token;// always returns the first
		}
		return null;
	}
}
