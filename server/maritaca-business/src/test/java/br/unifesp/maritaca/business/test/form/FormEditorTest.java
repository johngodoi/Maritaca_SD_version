package br.unifesp.maritaca.business.test.form;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.business.account.edit.AccountEditorEJB;
import br.unifesp.maritaca.business.account.edit.dao.AccountEditorDAO;
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
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.persistence.permission.Policy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class FormEditorTest extends BaseEmbededServerSetupTest {
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;
	//User and List
	private static final String LIST_ONE = "listOne";
	private static final String LIST_TWO = "listTwo";
	private static final String EMAIL_USR1    = "usr1@mail.com";
	private static final String EMAIL_USR2    = "usr2@mail.com";
	private AccountEditorEJB accountEditorEjb;
	private ListMaritacaListEJB listEjb;
	private ListEditorEJB listEditorEjb;
	
	User _user1 = null;
	User _user2 = null;
	MaritacaListDTO _list1User2 = null;
	MaritacaListDTO _list2User1 = null;
	
	Form _privateForm			 = null;
	Form _sharedHierarchicalForm = null;
	Form _sharedSocialForm 		 = null;
	Form _publicForm 			 = null;
	FormAccessibleByList _fAL1	 = null;
	FormAccessibleByList _fAL2	 = null;
	
			
	//Form Editor
	private FormEditorEJB formEditorEJB;
		
	
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
		
		emHectorImpl.createColumnFamily(Configuration.class);
		emHectorImpl.createColumnFamily(User.class);
		emHectorImpl.createColumnFamily(MaritacaList.class);
		
		emHectorImpl.createColumnFamily(Form.class);
		emHectorImpl.createColumnFamily(FormAccessibleByList.class);
	}
	
	@After
	public void cleanUp(){
		emHectorImpl.dropColumnFamily(User.class);
		emHectorImpl.dropColumnFamily(MaritacaList.class);
		
		emHectorImpl.createColumnFamily(Form.class);
		emHectorImpl.createColumnFamily(FormAccessibleByList.class);
	}
	
	//Improve w/Arquillian!!! 
	@Test
	public void createUserAndList() {
		UserDTO user1 = new UserDTO(); user1.setEmail(EMAIL_USR1);
		UserDTO user2 = new UserDTO(); user2.setEmail(EMAIL_USR2);		
		accountEditorEjb.saveAccount(user1);
		accountEditorEjb.saveAccount(user2);
		
		_user1 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR1);
		_user2 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR1);
		assertNotNull("UserFound1 not found", _user1.getKey());
		assertNotNull("UserFound2 not found", _user2.getKey());
		
		//		
		MaritacaListDTO list1User2 = new MaritacaListDTO();			
		MaritacaListDTO list2User1 = new MaritacaListDTO();
		list1User2.setOwner(_user2.getKey()); list1User2.setName(LIST_ONE);
		list2User1.setOwner(_user1.getKey()); list2User1.setName(LIST_TWO);		
		listEditorEjb.saveMaritacaList(list1User2);
		listEditorEjb.saveMaritacaList(list2User1);
				
		_list1User2 = null;
		_list2User1 = null;
		_list1User2 = listEditorEjb.searchMaritacaListByName(LIST_ONE, _user2.getKey());
		_list2User1 = listEditorEjb.searchMaritacaListByName(LIST_TWO, _user1.getKey());
		assertNotNull("List-List1 not found", _list1User2.getKey());
		assertNotNull("List-List2 not found", _list2User1.getKey());
		////Private Form
		FormDTO privateFDTO = new FormDTO();
		privateFDTO.setTitle("new Form");
		privateFDTO.setXml("<form>private</form>");		
		privateFDTO.setUserKey(_user1.getKey());
		Form tmp1 = formEditorEJB.saveNewForm2(privateFDTO);
		_privateForm = formEditorEJB.getFormDAO().getFormByKey(tmp1.getKey(), false);
		assertNotNull("form not found", _privateForm);
		assertNotNull("formKey not found", _privateForm.getKey());
		assertEquals("form policy is not private", Policy.PRIVATE.getIdPolicy(), _privateForm.getPolicy().getIdPolicy());
		////Public Form
		FormDTO publicFDTO = new FormDTO();
		publicFDTO.setTitle("new Form");
		publicFDTO.setXml("<form>public</form>");		
		publicFDTO.setUserKey(_user1.getKey());
		Form tmp2 = formEditorEJB.saveNewForm2(publicFDTO);
		_publicForm = formEditorEJB.getFormDAO().getFormByKey(tmp2.getKey(), false);
		assertNotNull("form not found", _publicForm);
		assertNotNull("formKey not found", _publicForm.getKey());
		assertEquals("form policy is not private", Policy.PRIVATE.getIdPolicy(), _publicForm.getPolicy().getIdPolicy());
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(_publicForm, Policy.PUBLIC), getUserDTO(_user1), getUsedItems(_list1User2, _list2User1));
		_publicForm = formEditorEJB.getFormDAO().getFormByKey(tmp2.getKey(), false);
		assertNotNull("form not found", _publicForm);
		assertNotNull("formKey not found", _publicForm.getKey());
		assertEquals("form policy is not public", Policy.PUBLIC.getIdPolicy(), _publicForm.getPolicy().getIdPolicy());
		assertEquals("why this form has a maritacalist???", 0, _publicForm.getLists()!=null?_publicForm.getLists().size():0);
		////Shared Hierarchical Form
		FormDTO shaHieFDTO = new FormDTO();
		shaHieFDTO.setTitle("new Form");
		shaHieFDTO.setXml("<form>sharedH</form>");		
		shaHieFDTO.setUserKey(_user1.getKey());
		Form tmp3 = formEditorEJB.saveNewForm2(shaHieFDTO);
		_sharedHierarchicalForm = formEditorEJB.getFormDAO().getFormByKey(tmp3.getKey(), false);
		assertNotNull("form not found", _sharedHierarchicalForm);
		assertNotNull("formKey not found", _sharedHierarchicalForm.getKey());
		assertEquals("form policy is not private", Policy.PRIVATE.getIdPolicy(), _sharedHierarchicalForm.getPolicy().getIdPolicy());
		formEditorEJB.updateFormFromPolicyEditor(getFormDTO(_sharedHierarchicalForm, Policy.SHARED_HIERARCHICAL), getUserDTO(_user1), getUsedItems(_list1User2, _list2User1));
		_sharedHierarchicalForm = formEditorEJB.getFormDAO().getFormByKey(tmp3.getKey(), false);
		assertNotNull("form not found", _sharedHierarchicalForm);
		assertNotNull("formKey not found", _sharedHierarchicalForm.getKey());
		assertEquals("form policy is not shared hierarchical", Policy.SHARED_HIERARCHICAL.getIdPolicy(), _sharedHierarchicalForm.getPolicy().getIdPolicy());
		assertEquals("it must have 2 lists!", 2, _sharedHierarchicalForm.getLists()!=null?_sharedHierarchicalForm.getLists().size():0);
		//
		for(UUID uuid : _sharedHierarchicalForm.getLists()) {
			_fAL1 = formEditorEJB.getFormEditorDAO().getFormAccessibleByListDAO().findFormAccesibleByKey(uuid);			
		}
		assertEquals("form accessible error!", 1, _fAL1.getForms().size());
		
	}
	
	private UserDTO getUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setKey(user.getKey());
		return userDTO;
	}
	
	private List<MaritacaListDTO> getUsedItems(MaritacaListDTO... maritacaLists) {
		List<MaritacaListDTO> mList = new ArrayList<MaritacaListDTO>();
		mList.add(maritacaLists[0]);
		mList.add(maritacaLists[1]);
		return mList;		
	}

	private FormDTO getFormDTO(Form form, Policy policy) {
		FormDTO formDTO = new FormDTO();
		formDTO.setKey(form.getKey());
		formDTO.setPolicy(policy);
		return formDTO;
	}
	
	private FormDTO createPrivateForm() {
		System.out.println(_user1);
		FormDTO formDTO = new FormDTO();
		formDTO.setTitle("new Form");
		formDTO.setXml("<form></form>");
		formDTO.setUrl("private");
		formDTO.setUserKey(_user1.getKey());
		formEditorEJB.saveNewForm(formDTO);
		return formDTO;
	}
	
	//@Test
	public void findFormByUrl() {
		FormDTO tmpDTO = this.createPrivateForm();
		Form tmp = formEditorEJB.getFormDAO().findFormByUrl(tmpDTO.getUrl());
		assertNotNull("form not found", tmp);
		assertNotNull("formKey not found", tmp.getKey());
	}
	
	
	

}