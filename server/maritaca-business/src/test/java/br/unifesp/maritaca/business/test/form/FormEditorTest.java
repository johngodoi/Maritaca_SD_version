package br.unifesp.maritaca.business.test.form;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
public class FormEditorTest extends BaseEmbededServerSetupTest {
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;
	//User and List	
	private AccountEditorEJB accountEditorEjb;
	private ListMaritacaListEJB listEjb;
	private ListEditorEJB listEditorEjb;
	//Form Editor
	private FormEditorEJB formEditorEJB;
	//
	private String LIST_ONE   = "listOne";
	private String LIST_TWO   = "listTwo";
	private String EMAIL_USR1 = "usr1@mail.com";
	private String EMAIL_USR2 = "usr2@mail.com";
	User user1 = null;
	User user2 = null;
	MaritacaListDTO list1User2 = null;
	MaritacaListDTO list2User1 = null;
	
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
	
	//@Test
	private void createUserAndList() {
		UserDTO userDTO1 = new UserDTO(); userDTO1.setEmail(EMAIL_USR1);
		UserDTO userDTO2 = new UserDTO(); userDTO2.setEmail(EMAIL_USR2);		
		accountEditorEjb.saveAccount(userDTO1);
		accountEditorEjb.saveAccount(userDTO2);
		user1 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR1);
		user2 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR2);
		//assertNotNull("User1 not found", user1.getKey());
		//assertEquals("User1 email does not match", EMAIL_USR1, user1.getEmail());
		//assertNotNull("User2 not found", user2.getKey());
		//assertEquals("User2 email does not match", EMAIL_USR2, user2.getEmail());
		//
		MaritacaListDTO list1User2DTO = new MaritacaListDTO();			
		MaritacaListDTO list2User1DTO = new MaritacaListDTO();
		list1User2DTO.setOwner(user2.getKey()); list1User2DTO.setName(LIST_ONE);
		list2User1DTO.setOwner(user1.getKey()); list2User1DTO.setName(LIST_TWO);		
		listEditorEjb.saveMaritacaList(list1User2DTO);
		listEditorEjb.saveMaritacaList(list2User1DTO);
		list1User2 = listEditorEjb.searchMaritacaListByName(LIST_ONE, user2.getKey());
		list2User1 = listEditorEjb.searchMaritacaListByName(LIST_TWO, user1.getKey());
		//assertNotNull("List1User2 not found", list1User2.getKey());
		//assertNotNull("List2User1 not found", list2User1.getKey());
	}
	
	@Test
	public void createPrivateForm() {
		FormDTO privateFDTO = new FormDTO();
		privateFDTO.setTitle("new Form");
		privateFDTO.setXml("<form>private</form>");		
		privateFDTO.setUserKey(user1.getKey());
		Form tmp = formEditorEJB.saveNewForm2(privateFDTO);
		Form privateForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
		assertNotNull("Form not found", privateForm);
		assertNotNull("FormKey does not match", privateForm.getKey());
		assertEquals("Form policy should be private", Policy.PRIVATE.getIdPolicy(), privateForm.getPolicy().getIdPolicy());
		assertEquals("This form should not have a list", 0, privateForm.getLists()!=null?privateForm.getLists().size():0);
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
	
	@Test
	public void createPublicForm() {		
		FormDTO publicFDTO = new FormDTO();
		publicFDTO.setTitle("new Form");
		publicFDTO.setXml("<form>public</form>");		
		publicFDTO.setUserKey(user1.getKey());
		Form tmp = formEditorEJB.saveNewForm2(publicFDTO);
		Form publicForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
		assertNotNull("Form not found", publicForm);
		assertNotNull("FormKey does not match", publicForm.getKey());
		assertEquals("Form policy should be private", Policy.PRIVATE.getIdPolicy(), publicForm.getPolicy().getIdPolicy());
		assertEquals("This form should not have a list", 0, publicForm.getLists()!=null?publicForm.getLists().size():0);
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(publicForm, Policy.PUBLIC), getUserDTO(user1), getUsedItems(list1User2, list2User1));
		publicForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
		assertNotNull("Form not found", publicForm);
		assertNotNull("FormKey does not found", publicForm.getKey());		
		assertEquals("Form policy should be public", Policy.PUBLIC.getIdPolicy(), publicForm.getPolicy().getIdPolicy());		
		assertEquals("This form should not have a list", 0, publicForm.getLists()!=null?publicForm.getLists().size():0);
	}
	
	//@Test
	public void createSharedHierarchicalForm() {
		FormAccessibleByList formAccByList	 = null;
		FormDTO shaHieFormDTO = new FormDTO();
		shaHieFormDTO.setTitle("new Form");
		shaHieFormDTO.setXml("<form>sharedHierarchical</form>");		
		shaHieFormDTO.setUserKey(user1.getKey());
		Form tmp = formEditorEJB.saveNewForm2(shaHieFormDTO);
		Form sharedHierarchicalForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
		assertNotNull("Form not found", sharedHierarchicalForm);
		assertNotNull("FormKey does not match", sharedHierarchicalForm.getKey());
		assertEquals("Form policy is not private", Policy.PRIVATE.getIdPolicy(), sharedHierarchicalForm.getPolicy().getIdPolicy());
		assertEquals("Private form should not have a list", 0, sharedHierarchicalForm.getLists()!=null?sharedHierarchicalForm.getLists().size():0);
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(sharedHierarchicalForm, Policy.SHARED_HIERARCHICAL), getUserDTO(user1), getUsedItems(list1User2, list2User1));
		sharedHierarchicalForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
		assertNotNull("Form not found", sharedHierarchicalForm);
		assertNotNull("FormKey does not match", sharedHierarchicalForm.getKey());
		assertEquals("Form policy should be shared hierarchical", Policy.SHARED_HIERARCHICAL.getIdPolicy(), sharedHierarchicalForm.getPolicy().getIdPolicy());
		assertEquals("This form should have 2 lists", 2, sharedHierarchicalForm.getLists()!=null?sharedHierarchicalForm.getLists().size():0);
		//
		for(UUID uuid : sharedHierarchicalForm.getLists()) {
			formAccByList = formEditorEJB.getFormEditorDAO().getFormAccessibleByListDAO().findFormAccesibleByKey(uuid);			
		}
		assertEquals("FormAccessibleByList should have 1 list", 1, formAccByList.getForms().size());
	}
	
	@Test
	public void createSharedSocialForm() {
		FormAccessibleByList formAccByList	 = null;
		FormDTO shaSocFormDTO = new FormDTO();
		shaSocFormDTO.setTitle("new Form");
		shaSocFormDTO.setXml("<form>sharedSocial</form>");		
		shaSocFormDTO.setUserKey(user1.getKey());
		Form tmp = formEditorEJB.saveNewForm2(shaSocFormDTO);
		Form sharedSocialForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
		assertNotNull("Form not found", sharedSocialForm);
		assertNotNull("FormKey does not match", sharedSocialForm.getKey());
		assertEquals("Form policy is not private", Policy.PRIVATE.getIdPolicy(), sharedSocialForm.getPolicy().getIdPolicy());
		assertEquals("Private form should not have a list", 0, sharedSocialForm.getLists()!=null?sharedSocialForm.getLists().size():0);
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(sharedSocialForm, Policy.SHARED_SOCIAL), getUserDTO(user1), getUsedItems(list1User2, list2User1));
		sharedSocialForm = formEditorEJB.getFormDAO().getFormByKey(tmp.getKey(), false);
		assertNotNull("Form not found", sharedSocialForm);
		assertNotNull("FormKey does not match", sharedSocialForm.getKey());
		assertEquals("Form policy should be shared social", Policy.SHARED_SOCIAL.getIdPolicy(), sharedSocialForm.getPolicy().getIdPolicy());
		assertEquals("This form should have 2 lists", 2, sharedSocialForm.getLists()!=null?sharedSocialForm.getLists().size():0);
		//
		for(UUID uuid : sharedSocialForm.getLists()) {
			formAccByList = formEditorEJB.getFormEditorDAO().getFormAccessibleByListDAO().findFormAccesibleByKey(uuid);			
		}
		assertEquals("FormAccessibleByList should have 1 list", 1, formAccByList.getForms().size());
	}
	
}