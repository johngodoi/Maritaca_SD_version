package br.unifesp.maritaca.model.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;

public class UserModelImplTest {
	private static final String uuid = "637dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid2 = "537dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid3 = "737dea60-146e-11e1-a7c0-d2b70b6d4d67";

	private UserModel userModel;
	private ManagerModel managerModel;
	private EntityManager em;

	@Before
	public void setUp() throws IOException {
		em = mock(EntityManager.class);
		userModel = new UserModelImpl();
		userModel.setEntityManager(em);
		managerModel = mock(ManagerModel.class);
		userModel.setManagerModel(managerModel);
	}

	@Test
	public void testSaveUser() {
		User user = new User();

		when(em.persist(any())).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				Object ob = invocation.getArguments()[0];
				if (ob instanceof User) {
					User u = (User) ob;
					u.setKey(uuid);
				} else {
					GroupUser gu = new GroupUser();
					gu.setKey(uuid3);
				}
				return true;
			}
		});

		when(em.cQuery(Group.class, "owner", uuid)).thenAnswer(
				new Answer<List<Group>>() {

					@Override
					public List<Group> answer(InvocationOnMock invocation)
							throws Throwable {
						Group g = new Group();
						g.setName(ManagerModel.ALL_USERS);
						g.setKey(uuid2);
						ArrayList<Group> l = new ArrayList<Group>();
						l.add(g);
						return l;
					}
				});
		
		when(managerModel.getRootUser()).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User user = new User();
				user.setKey(uuid);
				return user;
			}
		});

		assertNull(user.getKey());
		assertTrue(userModel.saveUser(user));
		assertTrue(user.getKey().toString().equals(uuid));
	}

	@Ignore
	@Test
	public void testAddUserToGroup() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetAllUsersGroup() {
		fail("Not yet implemented");
	}

}
