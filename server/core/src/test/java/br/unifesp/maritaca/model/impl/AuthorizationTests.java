package br.unifesp.maritaca.model.impl;

import static org.junit.Assert.*;


import javax.annotation.Resource;

import me.prettyprint.cassandra.BaseEmbededServerSetupTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.access.Policy;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.exception.AuthorizationDenied;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class AuthorizationTests extends BaseEmbededServerSetupTest {

	@Resource
	private EntityManagerHectorImpl emHectorImpl;	
	
	private FormAnswerModel formAnswModel;
	
	private User  user1grp1    = new User();
	private User  user2grp1    = new User();
	private User  userNoGrp    = new User();
	private User  root         = new User();
	
	private Group grp1         = new Group();		
	private Group grpUser1     = new Group();				
	private Group grpUser2     = new Group();
	private Group grpUserNoGrp = new Group();
	private Group grpAllUsers  = new Group();
	
	private Form  publicFormUser1       = new Form();
	private Form  privateFormUser1      = new Form();
	private Form  sharedHierFormUser1   = new Form();
	private Form  sharedSocialFormUser1 = new Form();
	
	@Before
	public void setUp(){				
		EntityManager em    = emHectorImpl;
		
		user1grp1.setEmail("usr1");
		user2grp1.setEmail("usr2");
		userNoGrp.setEmail("usrNoGrp");
		root.setEmail(ManagerModel.CFG_ROOT);
		
		em.persist(user1grp1);
		em.persist(user2grp1);
		em.persist(userNoGrp);
		em.persist(root);
		
		grp1.setOwner(user1grp1);
		grpUser1.setOwner(user1grp1);
		grpUser2.setOwner(user2grp1);
		grpUserNoGrp.setOwner(userNoGrp);
		grpAllUsers.setOwner(root);
		grpAllUsers.setName(ManagerModel.ALL_USERS);
		
		em.persist(grp1);
		em.persist(grpUser1);
		em.persist(grpUser2);
		em.persist(grpUserNoGrp);
		em.persist(grpAllUsers);
		
		user1grp1.setUserGroup(grpUser1);
		user2grp1.setUserGroup(grpUser2);
		userNoGrp.setUserGroup(grpUserNoGrp);
		em.persist(user1grp1);
		em.persist(user2grp1);
		em.persist(userNoGrp);
		
		GroupUser gp1 = new GroupUser(grp1,user1grp1);
		GroupUser gp2 = new GroupUser(grp1,user2grp1);
		GroupUser gp3 = new GroupUser(grpAllUsers,user1grp1);
		GroupUser gp4 = new GroupUser(grpAllUsers,user2grp1);
		GroupUser gp5 = new GroupUser(grpAllUsers,userNoGrp);
		
		em.persist(gp1);
		em.persist(gp2);
		em.persist(gp3);
		em.persist(gp4);
		em.persist(gp5);
		
		publicFormUser1.setUser(user1grp1);
		
		Policy publicPolicy = Policy.PUBLIC;		
		publicFormUser1.setPolicy(publicPolicy);
		em.persist(publicFormUser1);
			
		for(FormPermissions fp : publicPolicy.buildPermissions(grpUser1, grpAllUsers, grp1)){
			fp.setForm(publicFormUser1);
			em.persist(fp);
		}
		
		
		privateFormUser1.setUser(user1grp1);
		
		Policy privatePolicy = Policy.PRIVATE;		
		privateFormUser1.setPolicy(privatePolicy);
		em.persist(privateFormUser1);
			
		for(FormPermissions fp : privatePolicy.buildPermissions(grpUser1, grpAllUsers, grp1)){
			fp.setForm(privateFormUser1);
			em.persist(fp);
		}
		
		
		sharedHierFormUser1.setUser(user1grp1);
		
		Policy sharedHierPolicy = Policy.SHARED_HIERARCHICAL;		
		sharedHierFormUser1.setPolicy(sharedHierPolicy);
		sharedHierFormUser1.setSharedlist(grp1);
		em.persist(sharedHierFormUser1);
			
		for(FormPermissions fp : sharedHierPolicy.buildPermissions(grpUser1, grpAllUsers, grp1)){
			fp.setForm(sharedHierFormUser1);
			em.persist(fp);
		}
		
		
		sharedSocialFormUser1.setUser(user1grp1);
		
		Policy sharedSocialPolicy = Policy.SHARED_SOCIAL;		
		sharedSocialFormUser1.setPolicy(sharedSocialPolicy);
		sharedSocialFormUser1.setSharedlist(grp1);
		em.persist(sharedSocialFormUser1);
			
		for(FormPermissions fp : sharedSocialPolicy.buildPermissions(grpUser1, grpAllUsers, grp1)){
			fp.setForm(sharedSocialFormUser1);
			em.persist(fp);
		}
		
		
		formAnswModel = ModelFactory.getInstance().createFormResponseModel();
		formAnswModel.setEntityManager(em);

		ManagerModel mm = mock(ManagerModelImpl.class);
		when(mm.getRootUser()).thenAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				return root;
			}			
		});
		
		UserModel um =  ModelFactory.getInstance().createUserModel();
		um.setEntityManager(em);
		um.setManagerModel(mm);
		formAnswModel.setUserModel(um);
	}
	
	private void setCurrentUser(User usr){
		formAnswModel.setCurrentUser(usr);
		formAnswModel.getUserModel().setCurrentUser(usr);
	}
	
	@Test
	public void testPublicFormUserNotInGroup(){
		setCurrentUser(userNoGrp);		
				
		/* Can read the form */
		Form formRetrieved;
		try{
			formRetrieved = formAnswModel.getForm(publicFormUser1.getKey(), false);
		} catch(AuthorizationDenied e){
			fail();
			return;
		}
		
		/* Can't update the form */
		formRetrieved.setUser(userNoGrp);
		try{
			formAnswModel.saveForm(formRetrieved);
			fail();
		} catch(AuthorizationDenied e){
		}
		
		/* Can't delete the form */
		try{
			formAnswModel.deleteForm(publicFormUser1);
			fail();
		} catch(AuthorizationDenied e){
		}
	}

	@Test
	public void testPublicFormUserIsOwner(){
		setCurrentUser(user1grp1);		
						
		/* Can read the form */
		Form formRetrieved;
		try{
			formRetrieved = formAnswModel.getForm(publicFormUser1.getKey(), false);
		} catch(AuthorizationDenied e){
			fail();
			return;
		}
		
		/* Can update the form */
		formRetrieved.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(formRetrieved));
		} catch(AuthorizationDenied e){
			fail();
		}
				
		/* Can delete the form */
		try{
			formAnswModel.deleteForm(publicFormUser1);
		} catch(AuthorizationDenied e){
			fail();
		}		
	}
	
	@Test
	public void testPrivateFormUserIsOwner(){
		setCurrentUser(user1grp1);		
		
		/* Can read the form */		
		try{
			formAnswModel.getForm(privateFormUser1.getKey(), false);			
		} catch(AuthorizationDenied e){
			fail();
		}
		
		/* Can update the form */
		privateFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(privateFormUser1));			
		} catch(AuthorizationDenied e){
			fail();
		}
				
		/* Can delete the form */
		try{
			formAnswModel.deleteForm(privateFormUser1);			
		} catch(AuthorizationDenied e){
			fail();
		}				
	}
	
	@Test
	public void testPrivateFormUserInGroup(){
		setCurrentUser(user2grp1);		
		
		/* Can't read the form */		
		try{
			formAnswModel.getForm(privateFormUser1.getKey(), false);
			fail();
		} catch(AuthorizationDenied e){
		}
		
		/* Can't update the form */
		privateFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(privateFormUser1));
			fail();
		} catch(AuthorizationDenied e){
		}
				
		/* Can't delete the form */
		try{
			formAnswModel.deleteForm(privateFormUser1);
			fail();
		} catch(AuthorizationDenied e){
		}				
	}
	
	@Test
	public void testSharedHierPermissionsUserFromList(){
		setCurrentUser(user2grp1);		
		
		/* Can read the form */		
		try{
			formAnswModel.getForm(sharedHierFormUser1.getKey(), false);
		} catch(AuthorizationDenied e){
			fail();
		}
		
		/* Can't update the form */
		sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(sharedHierFormUser1));
			fail();
		} catch(AuthorizationDenied e){
		}
				
		/* Can't delete the form */
		try{
			formAnswModel.deleteForm(sharedHierFormUser1);
			fail();
		} catch(AuthorizationDenied e){
		}			
	}
	
	@Test
	public void testSharedHierPermissionsUserNotFromList(){
		setCurrentUser(userNoGrp);		
		
		/* Can't read the form */		
		try{
			formAnswModel.getForm(sharedHierFormUser1.getKey(), false);
			fail();
		} catch(AuthorizationDenied e){
		}
		
		/* Can't update the form */
		sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(sharedHierFormUser1));
			fail();
		} catch(AuthorizationDenied e){
		}
				
		/* Can't delete the form */
		try{
			formAnswModel.deleteForm(sharedHierFormUser1);
			fail();
		} catch(AuthorizationDenied e){
		}			
	}
	
	@Test
	public void testSharedHierPermissionsUserIsOwner(){
		setCurrentUser(user1grp1);		
		
		/* Can read the form */		
		try{
			formAnswModel.getForm(sharedHierFormUser1.getKey(), false);
		} catch(AuthorizationDenied e){
			fail();
		}
		
		/* Can update the form */
		sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(sharedHierFormUser1));
		} catch(AuthorizationDenied e){
			fail();
		}
				
		/* Can delete the form */
		try{
			formAnswModel.deleteForm(sharedHierFormUser1);
		} catch(AuthorizationDenied e){
			fail();
		}			
	}
	
	@Test
	public void testSharedSocialPermissionsUserIsOwner(){
		setCurrentUser(user1grp1);		
		
		/* Can read the form */		
		try{
			formAnswModel.getForm(sharedSocialFormUser1.getKey(), false);
		} catch(AuthorizationDenied e){
			fail();
		}
		
		/* Can update the form */
		sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(sharedSocialFormUser1));
		} catch(AuthorizationDenied e){
			fail();
		}
				
		/* Can delete the form */
		try{
			formAnswModel.deleteForm(sharedSocialFormUser1);
		} catch(AuthorizationDenied e){
			fail();
		}	
	}
	
	@Test
	public void testSharedSocialPermissionsUserNotFromList(){
		setCurrentUser(userNoGrp);		
		
		/* Can't read the form */		
		try{
			formAnswModel.getForm(sharedSocialFormUser1.getKey(), false);
			fail();
		} catch(AuthorizationDenied e){
		}
		
		/* Can't update the form */
		sharedSocialFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(sharedSocialFormUser1));
			fail();
		} catch(AuthorizationDenied e){
		}
				
		/* Can't delete the form */
		try{
			formAnswModel.deleteForm(sharedSocialFormUser1);
			fail();
		} catch(AuthorizationDenied e){
		}			
	}

	@Test
	public void testSocialHierPermissionsUserFromList(){
		setCurrentUser(user2grp1);		
		
		/* Can read the form */		
		try{
			formAnswModel.getForm(sharedSocialFormUser1.getKey(), false);
		} catch(AuthorizationDenied e){
			fail();
		}
		
		/* Can't update the form */
		sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(formAnswModel.saveForm(sharedSocialFormUser1));
			fail();
		} catch(AuthorizationDenied e){
		}
				
		/* Can't delete the form */
		try{
			formAnswModel.deleteForm(sharedSocialFormUser1);
			fail();
		} catch(AuthorizationDenied e){
		}			
	}

	
	public EntityManagerHectorImpl getEmHectorImpl() {
		return emHectorImpl;
	}

	public void setEmHectorImpl(EntityManagerHectorImpl emHectorImpl) {
		this.emHectorImpl = emHectorImpl;
	}
}
