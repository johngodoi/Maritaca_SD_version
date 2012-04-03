package br.unifesp.maritaca.model.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;

public class UserModelImplTest {
	private static final String uuid  = "637dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid2 = "537dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid3 = "737dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid4 = "737dea60-146e-11e1-a7c0-d2b70b444444";

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
		user.setEmail("user@email.com");
		
		MaritacaList userList = new MaritacaList();
		user.setMaritacaList(userList);				

		when(em.persist(any())).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				Object ob = invocation.getArguments()[0];
				if (ob instanceof User) {
					User u = (User) ob;
					u.setKey(uuid);
				} else {
					MaritacaListUser gu = new MaritacaListUser();
					gu.setKey(uuid3);
				}
				return true;
			}
		});

		when(em.cQuery(MaritacaList.class, "owner", uuid)).thenAnswer(
				new Answer<List<MaritacaList>>() {

					@Override
					public List<MaritacaList> answer(InvocationOnMock invocation)
							throws Throwable {
						MaritacaList g = new MaritacaList();
						g.setName(ManagerModel.ALL_USERS);
						g.setKey(uuid2);
						ArrayList<MaritacaList> l = new ArrayList<MaritacaList>();
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

	@Test(expected=IllegalArgumentException.class)
	public void testSaveEmptyGroup(){
		MaritacaList emptyGroup = new MaritacaList();
		userModel.saveMaritacaList(emptyGroup);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSaveGroupWithoutName(){
		MaritacaList noNameGrp = new MaritacaList();
		User  owner     = new User();
		owner.setKey(uuid);
		noNameGrp.setOwner(owner);
		userModel.saveMaritacaList(noNameGrp);
	}
	
	@Test
	public void testSaveValidGroup(){
		MaritacaList validGrp = new MaritacaList();
		validGrp.setName("grpTest");
		
		User  owner     = new User();
		owner.setKey(uuid);
		validGrp.setOwner(owner);
		
		when(em.persist(any())).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				Object ob = invocation.getArguments()[0];
				if (ob instanceof MaritacaList) {
					MaritacaList grp = new MaritacaList();
					grp.setKey(uuid3);
				}
				return true;
			}
		});
		
		assertTrue(userModel.saveMaritacaList(validGrp));
	}
	
	
	@Test
	public void testOwnerIsInList() {		
		final MaritacaList list1 = new MaritacaList();
		list1.setName("list1");
		list1.setKey(uuid);
		
		final User  ownerList1 = new User();
		ownerList1.setKey(uuid2);
		
		list1.setOwner(ownerList1);
		
		when(em.cQuery(MaritacaListUser.class, "maritacaList", uuid)).thenAnswer(new Answer<List<MaritacaListUser>>(){
			@Override
			public List<MaritacaListUser> answer(InvocationOnMock invocation)
					throws Throwable {
				
				List<MaritacaListUser> lists = new ArrayList<MaritacaListUser>();
				MaritacaListUser list = new MaritacaListUser(list1, ownerList1);
				lists.add(list);
				
				return lists;
			}			
		});
		
		when(managerModel.getRootUser()).thenAnswer(new Answer<User>(){
			@Override
			public User answer(InvocationOnMock invocation)
					throws Throwable {
				User rootUsr = new User();
				rootUsr.setKey(uuid3);
				
				return rootUsr;
			}						
		});
		
		when(em.find(User.class, UUID.fromString(uuid2))).thenAnswer(new Answer<User>(){
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				return ownerList1;
			}			
		});
		
		when(em.cQuery(MaritacaList.class, "owner", uuid)).thenAnswer(new Answer<List<MaritacaList>>(){
			@Override
			public List<MaritacaList> answer(InvocationOnMock invocation)
					throws Throwable {
				List<MaritacaList> rootGrps = new ArrayList<MaritacaList>();
				MaritacaList       rootGrp  = new MaritacaList();
				rootGrp.setKey(uuid4);
				rootGrp.setName(ManagerModel.ALL_USERS);
				rootGrps.add(rootGrp);
				return rootGrps;
			}			
		});

		
		Collection<User> usersFromGrp = userModel.searchUsersByMaritacaList(list1);		
		assertTrue(usersFromGrp.contains(ownerList1));
		assertEquals(usersFromGrp.size(),1);	
	}	
}
