package br.unifesp.maritaca.model;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import br.unifesp.maritaca.core.Group;
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

	Collection<Group> getGroupsByOwner(User owner);

	boolean addUserToGroup(User user, Group group);

	Group getAllUsersGroup();

	void setManagerModel(ManagerModel managerModel);

	ManagerModel getManagerModel();

	boolean userIsMemberOfGroup(User user, Group group);
}
