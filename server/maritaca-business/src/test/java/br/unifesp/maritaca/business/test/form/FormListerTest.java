package br.unifesp.maritaca.business.test.form;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.business.account.edit.AccountEditorEJB;
import br.unifesp.maritaca.business.account.edit.dao.AccountEditorDAO;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.base.dao.ConfigurationDAO;
import br.unifesp.maritaca.business.base.dao.FormAccessibleByListDAO;
import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.form.edit.dao.FormEditorDAO;
import br.unifesp.maritaca.business.form.list.FormListerEJB;
import br.unifesp.maritaca.business.list.edit.ListEditorEJB;
import br.unifesp.maritaca.business.list.edit.dao.ListEditorDAO;
import br.unifesp.maritaca.business.list.list.ListMaritacaListEJB;
import br.unifesp.maritaca.business.list.list.dao.ListMaritacaListDAO;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormAccessibleByList;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;
import br.unifesp.maritaca.persistence.permission.Policy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class FormListerTest extends BaseEmbededServerSetupTest {
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;
	//User and List	
	private AccountEditorEJB accountEditorEjb;
	private ListMaritacaListEJB listEjb;
	private ListEditorEJB listEditorEjb;
	//Form Editor
	private FormEditorEJB formEditorEJB;
	private FormListerEJB formListerEJB;
	//
	private String LIST_ONE   = "listOne";
	private String LIST_TWO   = "listTwo";
	private String EMAIL_USR1 = "usr1@mail.com";
	private String EMAIL_USR2 = "usr2@mail.com";
	private String EMAIL_USR3 = "usr3@mail.com";
	User user1 = null;
	User user2 = null;
	User user3 = null;
	MaritacaListDTO list1User2 = null;
	MaritacaListDTO list2User1 = null;
	//
	private String LIST_THREE   = "listThree";
	private String LIST_FOUR   = "listFour";
	private String EMAIL_USR4 = "usr4@mail.com";
	private String EMAIL_USR5 = "usr5@mail.com";
	private String EMAIL_USR6 = "usr6@mail.com";
	User user4 = null;
	User user5 = null;
	User user6 = null;
	MaritacaListDTO list3User4 = null;
	MaritacaListDTO list4User5 = null;
	
	@Before
	public void setUp() {
		EntityManagerFactory.setHectorEntityManager(emHectorImpl);
		//User and List
		accountEditorEjb = new AccountEditorEJB();
		accountEditorEjb.setAccountEditorDAO(new AccountEditorDAO());
		accountEditorEjb.getAccountEditorDAO().setListEditorDAO(new ListEditorDAO());
		listEjb = new ListMaritacaListEJB();
		listEjb.setMaritacaListDAO(new ListMaritacaListDAO());		
		listEditorEjb = new ListEditorEJB();
		listEditorEjb.setListEditorDAO(new ListEditorDAO());
		//Form Editor
		formEditorEJB = new FormEditorEJB();
		formEditorEJB.setConfigurationDAO(new ConfigurationDAO());
		formEditorEJB.setListMaritacaListDAO((new ListMaritacaListDAO()));
		formEditorEJB.setUserDAO(new UserDAO());
		formEditorEJB.setFormDAO(new FormDAO());
		formEditorEJB.setFormEditorDAO(new FormEditorDAO());
		formEditorEJB.getFormEditorDAO().setFormAccessibleByListDAO(new FormAccessibleByListDAO());
		//Form Lister
		formListerEJB = new FormListerEJB();
		formListerEJB.setConfigurationDAO(new ConfigurationDAO());
		formListerEJB.setUserDAO(new UserDAO());
		formListerEJB.setFormDAO(new FormDAO());
		//
		emHectorImpl.createColumnFamily(Configuration.class);
		emHectorImpl.createColumnFamily(User.class);
		emHectorImpl.createColumnFamily(MaritacaList.class);		
		emHectorImpl.createColumnFamily(Form.class);
		emHectorImpl.createColumnFamily(FormAccessibleByList.class);
		createUserAndList();
	}
	
	@After
	public void cleanUp(){
		emHectorImpl.dropColumnFamily(Configuration.class);
		emHectorImpl.dropColumnFamily(User.class);
		emHectorImpl.dropColumnFamily(MaritacaList.class);		
		emHectorImpl.dropColumnFamily(Form.class);
		emHectorImpl.dropColumnFamily(FormAccessibleByList.class);
	}
	
	@Test
	public void listFormsForUser1() {
		createPrivateForm(user1.getKey());
		createPublicForm(user1.getKey(), user1, list1User2, list2User1);
		createSharedHierarchicalForm(user1.getKey(), user1, list1User2, list2User1);
		createSharedSocialForm(user1.getKey(), user1, list1User2, list2User1);
		UserDTO userDTO = new UserDTO(); userDTO.setKey(user1.getKey()); userDTO.setEmail(user1.getEmail());
		//
		Collection<FormDTO> ownForms = formListerEJB.getListOwnForms(userDTO);
		assertNotNull("Forms(own) should not be null", ownForms);
		assertEquals("Forms(own) size does not match", 4, ownForms.size());
		for(FormDTO f : ownForms) {
			assertTrue("read(true)",   f.getPermission().getRead());
			assertTrue("update(true)", f.getPermission().getUpdate());
			assertTrue("delete(true)", f.getPermission().getDelete());
			if(f.getPolicy().getIdPolicy() == Policy.PRIVATE.getIdPolicy()) {
				assertTrue("share(true)",  f.getPermission().getShare());
			}
			else {
				assertFalse("share(false)",  f.getPermission().getShare());
			}
		}
		Collection<FormDTO> sharedForms = formListerEJB.getListSharedForms(userDTO);
		assertEquals("Forms(shared) size does not match", 0, sharedForms.size());
	}
	
	@Test
	public void listFormsForUser5AndUser6() {
		createPrivateForm(user4.getKey());
		createPublicForm(user4.getKey(), user4, list3User4, list4User5);
		createSharedHierarchicalForm(user4.getKey(), user4, list3User4, list4User5);
		createSharedSocialForm(user4.getKey(), user4, list3User4, list1User2);
		UserDTO user5DTO = new UserDTO(); user5DTO.setKey(user5.getKey()); user5DTO.setEmail(user5.getEmail());
		UserDTO user6DTO = new UserDTO(); user6DTO.setKey(user6.getKey()); user6DTO.setEmail(user6.getEmail());
		//
		Collection<FormDTO> ownFormsUser5 = formListerEJB.getListOwnForms(user5DTO);
		assertNull("Forms(own) should be null", ownFormsUser5);//User4 is the owner of all the forms
		Collection<FormDTO> sharedFormsUser5 = formListerEJB.getListSharedForms(user5DTO);
		assertEquals("Forms(shared) size does not match", 3, sharedFormsUser5.size());//User5 can read public, sharedHierarchical and sharedSocial
		for(FormDTO f : sharedFormsUser5) {
			assertTrue("read(true)",     f.getPermission().getRead());
			assertFalse("update(false)", f.getPermission().getUpdate());
			assertFalse("delete(false)", f.getPermission().getDelete());
			assertFalse("share(false)",  f.getPermission().getShare());			
		}
		//
		Collection<FormDTO> ownFormsUser6 = formListerEJB.getListOwnForms(user6DTO);
		assertNull("Forms(own) should be null", ownFormsUser6);//User4 is the owner of all the forms
		Collection<FormDTO> sharedFormsUser6 = formListerEJB.getListSharedForms(user6DTO);
		assertEquals("Forms(shared) size does not match", 2, sharedFormsUser6.size());//User5 can read public and sharedHierarchical
	}
	
	private void createUserAndList() {
		UserDTO userDTO1 = new UserDTO(); userDTO1.setEmail(EMAIL_USR1);
		UserDTO userDTO2 = new UserDTO(); userDTO2.setEmail(EMAIL_USR2);
		UserDTO userDTO3 = new UserDTO(); userDTO3.setEmail(EMAIL_USR3);
		
		UserDTO userDTO4 = new UserDTO(); userDTO4.setEmail(EMAIL_USR4);
		UserDTO userDTO5 = new UserDTO(); userDTO5.setEmail(EMAIL_USR5);
		UserDTO userDTO6 = new UserDTO(); userDTO6.setEmail(EMAIL_USR6);
		
		accountEditorEjb.saveAccount(userDTO1);
		accountEditorEjb.saveAccount(userDTO2);
		accountEditorEjb.saveAccount(userDTO3);
		accountEditorEjb.saveAccount(userDTO4);
		accountEditorEjb.saveAccount(userDTO5);
		accountEditorEjb.saveAccount(userDTO6);
		
		user1 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR1);
		user2 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR2);
		user3 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR3);
		user4 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR4);
		user5 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR5);
		user6 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR6);
		
		MaritacaListDTO list1User2DTO = new MaritacaListDTO();			
		MaritacaListDTO list2User1DTO = new MaritacaListDTO();
		
		MaritacaListDTO list3User4DTO = new MaritacaListDTO();
		MaritacaListDTO list4User5DTO = new MaritacaListDTO();
		
		List<UUID> usersList1 = new ArrayList<UUID>(); usersList1.add(user2.getKey()); usersList1.add(user3.getKey());
		List<UUID> usersList2 = new ArrayList<UUID>(); usersList2.add(user1.getKey()); usersList2.add(user2.getKey());
		List<UUID> usersList3 = new ArrayList<UUID>(); usersList3.add(user4.getKey()); usersList3.add(user5.getKey());
		List<UUID> usersList4 = new ArrayList<UUID>(); usersList4.add(user5.getKey()); usersList4.add(user6.getKey());
		
		list1User2DTO.setOwner(user2.getKey()); list1User2DTO.setName(LIST_ONE); list1User2DTO.setUsers(usersList1);
		list2User1DTO.setOwner(user1.getKey()); list2User1DTO.setName(LIST_TWO); list2User1DTO.setUsers(usersList2);
		list3User4DTO.setOwner(user4.getKey()); list3User4DTO.setName(LIST_THREE); list3User4DTO.setUsers(usersList3);
		list4User5DTO.setOwner(user5.getKey()); list4User5DTO.setName(LIST_FOUR);  list4User5DTO.setUsers(usersList4);
		
		listEditorEjb.saveMaritacaList(list1User2DTO);
		listEditorEjb.saveMaritacaList(list2User1DTO);
		listEditorEjb.saveMaritacaList(list3User4DTO);
		listEditorEjb.saveMaritacaList(list4User5DTO);
		
		list1User2 = listEditorEjb.searchMaritacaListByName(LIST_ONE, user2.getKey());
		list2User1 = listEditorEjb.searchMaritacaListByName(LIST_TWO, user1.getKey());
		
		list3User4 = listEditorEjb.searchMaritacaListByName(LIST_THREE, user4.getKey());
		list4User5 = listEditorEjb.searchMaritacaListByName(LIST_FOUR, user5.getKey());
	}
	
	private void createPrivateForm(UUID userKey) {
		FormDTO privateFDTO = new FormDTO();
		privateFDTO.setTitle("new Form");
		privateFDTO.setXml("<form>private</form>");		
		privateFDTO.setUserKey(userKey);
		formEditorEJB.saveNewForm2(privateFDTO);				
	}
	
	private void createPublicForm(UUID userKey, User user, MaritacaListDTO mList1, MaritacaListDTO mList2) {
		FormDTO publicFDTO = new FormDTO();
		publicFDTO.setTitle("new Form");
		publicFDTO.setXml("<form>public</form>");		
		publicFDTO.setUserKey(userKey);
		Form tmp = formEditorEJB.saveNewForm2(publicFDTO);
		Form publicForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);		
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(publicForm, Policy.PUBLIC), getUserDTO(user), getUsedItems(mList1, mList2));					
	}
	
	private void createSharedHierarchicalForm(UUID userKey, User user, MaritacaListDTO mList1, MaritacaListDTO mList2) {
		FormDTO shaHieFormDTO = new FormDTO();
		shaHieFormDTO.setTitle("new Form");
		shaHieFormDTO.setXml("<form>sharedHierarchical</form>");		
		shaHieFormDTO.setUserKey(userKey);
		Form tmp = formEditorEJB.saveNewForm2(shaHieFormDTO);
		Form sharedHierarchicalForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);		
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(sharedHierarchicalForm, Policy.SHARED_HIERARCHICAL), getUserDTO(user), getUsedItems(mList1, mList2));
		formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
	}
	
	private void createSharedSocialForm(UUID userKey, User user, MaritacaListDTO mList1, MaritacaListDTO mList2) {
		FormDTO shaSocFormDTO = new FormDTO();
		shaSocFormDTO.setTitle("new Form");
		shaSocFormDTO.setXml("<form>sharedSocial</form>");		
		shaSocFormDTO.setUserKey(userKey);
		Form tmp = formEditorEJB.saveNewForm2(shaSocFormDTO);
		Form sharedSocialForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);		
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(sharedSocialForm, Policy.SHARED_SOCIAL), getUserDTO(user), getUsedItems(mList1, mList2));
		formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
	}
	
	private UserDTO getUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setKey(user.getKey());
		return userDTO;
	}
	
	private FormDTO getFormDTO(Form form, Policy policy) {
		FormDTO formDTO = new FormDTO();
		formDTO.setKey(form.getKey());
		formDTO.setPolicy(policy);
		return formDTO;
	}
	
	private List<MaritacaListDTO> getUsedItems(MaritacaListDTO... maritacaLists) {
		List<MaritacaListDTO> mList = new ArrayList<MaritacaListDTO>();
		mList.add(maritacaLists[0]);
		mList.add(maritacaLists[1]);
		return mList;		
	}
}