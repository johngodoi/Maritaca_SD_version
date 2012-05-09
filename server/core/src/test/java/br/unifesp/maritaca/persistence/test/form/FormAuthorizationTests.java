package br.unifesp.maritaca.persistence.test.form;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
@Deprecated
public class FormAuthorizationTests extends BaseEmbededServerSetupTest {

	private AuthorizationTestsSetUp env;

	@Resource
	private EntityManagerHectorImpl emHectorImpl;	

	
	@Before
	public void setUp(){
		env = new AuthorizationTestsSetUp(emHectorImpl);
	}
	
	private void setCurrentUser(User usr){
		env.formAnswModel.setCurrentUser(usr);
		env.formAnswModel.getUserModel().setCurrentUser(usr);
	}		
	
	@Test	
	public void testPublicFormUserNotInGroup(){
		setCurrentUser(env.userNoGrp);		
				
		/* Can read the form */
		Form formRetrieved;
		try{
			formRetrieved = env.formAnswModel.getForm(env.publicFormUser1.getKey(), false);
		} catch(Exception e){
			fail();
			return;
		}
		
		/* Can't update the form */
		formRetrieved.setUser(env.userNoGrp);
		try{
			env.formAnswModel.saveForm(formRetrieved);
			fail();
		} catch(Exception e){
		}
		
		/* Can't delete the form */
		try{
			env.formAnswModel.deleteForm(env.publicFormUser1);
			fail();
		} catch(Exception e){
		}
	}

	@Test	
	public void testPublicFormUserIsOwner(){
		setCurrentUser(env.user1grp1);		
						
		/* Can read the form */
		Form formRetrieved;
		try{
			formRetrieved = env.formAnswModel.getForm(env.publicFormUser1.getKey(), false);
		} catch(Exception e){
			fail();
			return;
		}
		
		/* Can update the form */
		formRetrieved.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(formRetrieved));
		} catch(Exception e){
			fail();
		}
				
		/* Can delete the form */
		try{
			env.formAnswModel.deleteForm(env.publicFormUser1);
		} catch(Exception e){
			fail();
		}		
	}
	
	@Test
	public void testPrivateFormUserIsOwner(){
		setCurrentUser(env.user1grp1);		
		
		/* Can read the form */		
		try{
			env.formAnswModel.getForm(env.privateFormUser1.getKey(), false);			
		} catch(Exception e){
			fail();
		}
		
		/* Can update the form */
		env.privateFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.privateFormUser1));			
		} catch(Exception e){
			fail();
		}
				
		/* Can delete the form */
		try{
			env.formAnswModel.deleteForm(env.privateFormUser1);			
		} catch(Exception e){
			fail();
		}				
	}
	
	@Test
	public void testPrivateFormUserInGroup(){
		setCurrentUser(env.user2grp1);		
		
		/* Can't read the form */		
		try{
			env.formAnswModel.getForm(env.privateFormUser1.getKey(), false);
			fail();
		} catch(Exception e){
		}
		
		/* Can't update the form */
		env.privateFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.privateFormUser1));
			fail();
		} catch(Exception e){
		}
				
		/* Can't delete the form */
		try{
			env.formAnswModel.deleteForm(env.privateFormUser1);
			fail();
		} catch(Exception e){
		}				
	}
	
	@Test
	public void testSharedHierPermissionsUserFromList(){
		setCurrentUser(env.user2grp1);		
		
		/* Can read the form */		
		try{
			env.formAnswModel.getForm(env.sharedHierFormUser1.getKey(), false);
		} catch(Exception e){
			fail();
		}
		
		/* Can't update the form */
		env.sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.sharedHierFormUser1));
			fail();
		} catch(Exception e){
		}
				
		/* Can't delete the form */
		try{
			env.formAnswModel.deleteForm(env.sharedHierFormUser1);
			fail();
		} catch(Exception e){
		}			
	}
	
	@Test
	public void testSharedHierPermissionsUserNotFromList(){
		setCurrentUser(env.userNoGrp);		
		
		/* Can't read the form */		
		try{
			env.formAnswModel.getForm(env.sharedHierFormUser1.getKey(), false);
			fail();
		} catch(Exception e){
		}
		
		/* Can't update the form */
		env.sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.sharedHierFormUser1));
			fail();
		} catch(Exception e){
		}
				
		/* Can't delete the form */
		try{
			env.formAnswModel.deleteForm(env.sharedHierFormUser1);
			fail();
		} catch(Exception e){
		}			
	}
	
	@Test
	public void testSharedHierPermissionsUserIsOwner(){
		setCurrentUser(env.user1grp1);		
		
		/* Can read the form */		
		try{
			env.formAnswModel.getForm(env.sharedHierFormUser1.getKey(), false);
		} catch(Exception e){
			fail();
		}
		
		/* Can update the form */
		env.sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.sharedHierFormUser1));
		} catch(Exception e){
			fail();
		}
				
		/* Can delete the form */
		try{
			env.formAnswModel.deleteForm(env.sharedHierFormUser1);
		} catch(Exception e){
			fail();
		}			
	}
	
	@Test
	
	public void testSharedSocialPermissionsUserIsOwner(){
		setCurrentUser(env.user1grp1);		
		
		/* Can read the form */		
		try{
			env.formAnswModel.getForm(env.sharedSocialFormUser1.getKey(), false);
		} catch(Exception e){
			fail();
		}
		
		/* Can update the form */
		env.sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.sharedSocialFormUser1));
		} catch(Exception e){
			fail();
		}
				
		/* Can delete the form */
		try{
			env.formAnswModel.deleteForm(env.sharedSocialFormUser1);
		} catch(Exception e){
			fail();
		}	
	}
	
	@Test
	public void testSharedSocialPermissionsUserNotFromList(){
		setCurrentUser(env.userNoGrp);		
		
		/* Can't read the form */		
		try{
			env.formAnswModel.getForm(env.sharedSocialFormUser1.getKey(), false);
			fail();
		} catch(Exception e){
		}
		
		/* Can't update the form */
		env.sharedSocialFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.sharedSocialFormUser1));
			fail();
		} catch(Exception e){
		}
				
		/* Can't delete the form */
		try{
			env.formAnswModel.deleteForm(env.sharedSocialFormUser1);
			fail();
		} catch(Exception e){
		}			
	}

	@Test
	public void testSocialHierPermissionsUserFromList(){
		setCurrentUser(env.user2grp1);		
		
		/* Can read the form */		
		try{
			env.formAnswModel.getForm(env.sharedSocialFormUser1.getKey(), false);
		} catch(Exception e){
			fail();
		}
		
		/* Can't update the form */
		env.sharedHierFormUser1.setTitle("nice form");
		try{
			assertTrue(env.formAnswModel.saveForm(env.sharedSocialFormUser1));
			fail();
		} catch(Exception e){
		}
				
		/* Can't delete the form */
		try{
			env.formAnswModel.deleteForm(env.sharedSocialFormUser1);
			fail();
		} catch(Exception e){
		}			
	}
	
	@Test
	public void testListFormsFromOwner(){
		setCurrentUser(env.user1grp1);	
		
		Collection<Form> user1Forms = new ArrayList<Form>();
		user1Forms.add(env.privateFormUser1);
		user1Forms.add(env.publicFormUser1);
		user1Forms.add(env.sharedHierFormUser1);
		user1Forms.add(env.sharedSocialFormUser1);
		
		Collection<Form> returnedForms = env.formAnswModel.listFormsFromCurrentUser(true);		
		assertTrue(returnedForms.containsAll(user1Forms));
		assertTrue(returnedForms.size()==4);
		
		returnedForms = env.formAnswModel.listSharedFormsFromCurrentUser(true);
		assertTrue(returnedForms.isEmpty());
	}	
	
	@Test
	public void testListFormsUserInGroup(){		
		setCurrentUser(env.user2grp1);
		
		Collection<Form> user2SharedForms = new ArrayList<Form>();
		user2SharedForms.add(env.publicFormUser1);
		user2SharedForms.add(env.sharedHierFormUser1);
		user2SharedForms.add(env.sharedSocialFormUser1);
		
		Collection<Form>returnedForms = env.formAnswModel.listFormsFromCurrentUser(true); 
		assertTrue(returnedForms.isEmpty());
		
		returnedForms = env.formAnswModel.listSharedFormsFromCurrentUser(true);
		assertTrue(returnedForms.containsAll(user2SharedForms));
		assertTrue(returnedForms.size()==3);
	}
		
	@Test
	public void testListFormsUserNotInGroup(){				
		setCurrentUser(env.userNoGrp);		
		Collection<Form>returnedForms = env.formAnswModel.listFormsFromCurrentUser(true); 
		assertTrue(returnedForms.isEmpty());
		
		Collection<Form> userNoGrpSharedForms = new ArrayList<Form>();
		userNoGrpSharedForms.add(env.publicFormUser1);
		
		returnedForms = env.formAnswModel.listSharedFormsFromCurrentUser(true);
		assertTrue(returnedForms.containsAll(userNoGrpSharedForms));
		assertTrue(returnedForms.size()==1);		
	}
}
