package br.unifesp.maritaca.persistence.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import javax.annotation.Resource;

import me.prettyprint.cassandra.BaseEmbededServerSetupTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;
import br.unifesp.maritaca.persistence.permission.Policy;
import br.unifesp.maritaca.util.CFforTesting;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class UserDAOTest extends BaseEmbededServerSetupTest {
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;
	
	String ROOT;
	String PASSROOT;
	String ROOTEMAIL;
	String CFG_ROOT;
	String ALL_USERS;
	//UUID rootKey;
	
	@Before
	public void loadData() {
		ROOT = "root";
		PASSROOT = "dc76e9f0c0006e8f919e0c515c66dbba3982f785"; //sha1 for 'root'
		ROOTEMAIL = "root@maritaca.com";
		CFG_ROOT = "root";
		ALL_USERS = "all_users";
	}
	
	@Test
	public void testCreateExists() {
		assertFalse(emHectorImpl.columnFamilyExists(User.class));
		assertTrue(emHectorImpl.createColumnFamily(User.class));
		assertFalse(emHectorImpl.columnFamilyExists(MaritacaList.class));
		assertTrue(emHectorImpl.createColumnFamily(MaritacaList.class));
		assertFalse(emHectorImpl.columnFamilyExists(Form.class));
		assertTrue(emHectorImpl.createColumnFamily(Form.class));
	}
	
	@Test
	public void testCreateAndFindRootUser() {
		if(emHectorImpl.columnFamilyExists(User.class) && emHectorImpl.columnFamilyExists(MaritacaList.class)) { //test!!!
			//----------------- User
			User rootUser = new User();
			rootUser.setFirstname(ROOT);
			rootUser.setEmail(ROOTEMAIL);
			rootUser.setPassword(PASSROOT);
			emHectorImpl.persist(rootUser);
			//----------------- MaritacaList for user(root)
			MaritacaList list = new MaritacaList();
			list.setOwner(rootUser);
			list.setName(rootUser.getEmail());
			emHectorImpl.persist(list);
			//----------------- User (again)
			rootUser.setMaritacaList(list);
			emHectorImpl.persist(rootUser);
			//----------------- MaritacaList for all users()
			MaritacaList gr = new MaritacaList();
			gr.setName(ALL_USERS);
			gr.setOwner(rootUser);
			emHectorImpl.persist(gr);
			
			//----------------- User
			User userFound = emHectorImpl.find(rootUser.getClass(), rootUser.getKey());
			Assert.assertNotNull("User shoudn't be null", userFound);
			Assert.assertEquals("Email doesn't match", userFound.getEmail(), ROOTEMAIL);
			//----------------- MaritacaList for user(root)
			MaritacaList listForRootFound = emHectorImpl.find(list.getClass(), list.getKey());
			Assert.assertNotNull("ListForUser shoudn't be null", listForRootFound);
			Assert.assertEquals("root user\'s key doesn't match", listForRootFound.getOwner().getKey(), rootUser.getKey());
			//----------------- User (again)
			User userFound2 = emHectorImpl.find(rootUser.getClass(), rootUser.getKey());
			Assert.assertNotNull("User shoudn't be null", userFound2);
			Assert.assertEquals("list\'s key doesn't match", userFound.getMaritacaList().getKey(), list.getKey());
			//----------------- MaritacaList for all users()
			MaritacaList listForAllUsersFound = emHectorImpl.find(gr.getClass(), gr.getKey());
			Assert.assertNotNull("ListForAllUsers shoudn't be null", listForAllUsersFound);
			Assert.assertEquals("root user\'s key doesn't match", listForAllUsersFound.getOwner().getKey(), rootUser.getKey());
			//
			//
			//
			Form form = new Form();
			form.setTitle("new Form");
			form.setUrl("ABC");
			form.setXml("<form>xml</xml>");
			form.setPolicy(Policy.PRIVATE);
			User userFoundF = emHectorImpl.find(User.class, rootUser.getKey());
			Assert.assertNotNull("User shoudn't be null", userFoundF);
			form.setUser(userFoundF);
			emHectorImpl.persist(form);
			Form formFound = emHectorImpl.find(form.getClass(), form.getKey());
			Assert.assertNotNull("Form shoudn't be null", formFound);
			Assert.assertEquals("rootKey doesn't match", formFound.getUser().getKey(), rootUser.getKey());			
		}
	}
	
	//@Test
	public void testCreateForm() {
		/*System.out.println("uh___");
		System.out.println("rootkey: " + rootKey);
		Form form = new Form();
		form.setTitle("new Form");
		form.setUrl("ABC");
		form.setXml("<form>xml</xml>");
		form.setPolicy(Policy.PRIVATE);
		User userFound = emHectorImpl.find(User.class, rootKey);
		Assert.assertNotNull("User shoudn't be null", userFound);
		form.setUser(userFound);
		emHectorImpl.persist(form);
		Form formFound = emHectorImpl.find(form.getClass(), form.getKey());
		System.out.println("form de mela: " + formFound!=null ? "notNull":"Null");
		//Assert.assertNotNull("Form shoudn't be null", formFound);
		//Assert.assertEquals("rootKey doesn't match", formFound.getUser().getKey(), rootKey);*/
	}
}