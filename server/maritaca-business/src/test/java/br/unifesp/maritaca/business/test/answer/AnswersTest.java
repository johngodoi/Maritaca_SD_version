package br.unifesp.maritaca.business.test.answer;

import static org.junit.Assert.fail;

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
import br.unifesp.maritaca.business.answer.list.AnswersListerEJB;
import br.unifesp.maritaca.business.answer.list.dao.AnswersListerDAO;
import br.unifesp.maritaca.business.base.dao.ConfigurationDAO;
import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.exception.AuthorizationDenied;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.form.edit.dao.FormEditorDAO;
import br.unifesp.maritaca.business.list.edit.ListEditorEJB;
import br.unifesp.maritaca.business.list.edit.dao.ListEditorDAO;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;
import br.unifesp.maritaca.persistence.permission.Policy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class AnswersTest  extends BaseEmbededServerSetupTest {
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;	
	
	private AccountEditorEJB  accountEditorEjb;
	private ListEditorEJB     listEditorEjb;
	private FormEditorEJB     formEditorEjb;
	private AnswersListerEJB  answersListerEjb;

	private UserDTO userA;
	private UserDTO userB;
	private UserDTO userC;
	
	private MaritacaListDTO listFromUsrB;
	
	private FormDTO privFormUsrA;
	private FormDTO shHierFormUsrB;
	private FormDTO shSocFormUsrB;
	private FormDTO pubFormUsrC;
	
	private String  formXml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
							"<form><title>Form 1</title>" +
							"<questions>" +
							"<combobox id=\"0\" required=\"false\" >" +
							"<label>Question One</label>" +
							"<help></help>" +
							"<option></option>" +
							"</combobox>" +
							"<combobox id=\"1\" required=\"false\" >" +
							"<label>Question Two</label>" +
							"<help></help>" +
							"<option></option>" +
							"</combobox>" +
							"</questions>" +
							"</form>"; 

	@Before
	public void setUpTets(){
		EntityManagerFactory.setHectorEntityManager(emHectorImpl);
		
		accountEditorEjb = new AccountEditorEJB();
		listEditorEjb    = new ListEditorEJB();
		formEditorEjb    = new FormEditorEJB();
		answersListerEjb = new AnswersListerEJB();
		
		accountEditorEjb.setAccountEditorDAO(new AccountEditorDAO());
		accountEditorEjb.getAccountEditorDAO().setListEditorDAO(new ListEditorDAO());
		listEditorEjb.setListEditorDAO(new ListEditorDAO());
		formEditorEjb.setFormDAO(new FormDAO());
		formEditorEjb.setUserDAO(new UserDAO());
		formEditorEjb.setFormEditorDAO(new FormEditorDAO());
		answersListerEjb.setAnswersListerDAO(new AnswersListerDAO());
		answersListerEjb.setConfigurationDAO(new ConfigurationDAO());
		answersListerEjb.setUserDAO(new UserDAO());
		answersListerEjb.setFormDAO(new FormDAO());
		
		emHectorImpl.createColumnFamily(User.class);
		emHectorImpl.createColumnFamily(MaritacaList.class);
		emHectorImpl.createColumnFamily(Form.class);
		emHectorImpl.createColumnFamily(Answer.class);
		emHectorImpl.createColumnFamily(Configuration.class);
		
		userA = new UserDTO();
		userA.setEmail("userA@mail.com");
		userB = new UserDTO();
		userB.setEmail("userB@mail.com");
		userC = new UserDTO();
		userC.setEmail("userC@mail.com");

		accountEditorEjb.saveAccount(userA);		
		accountEditorEjb.saveAccount(userB);
		accountEditorEjb.saveAccount(userC);
		
		listFromUsrB = new MaritacaListDTO();
		listFromUsrB.setOwner(userB.getKey());
		List<UUID> listUsersFromB = new ArrayList<UUID>();
		listUsersFromB.add(userC.getMaritacaList());
		listFromUsrB.setUsers(listUsersFromB);
		listEditorEjb.saveMaritacaList(listFromUsrB);
		
		privFormUsrA   = new FormDTO();
		privFormUsrA.setUserKey(userA.getKey());
		privFormUsrA.setPolicy(Policy.PRIVATE);
		privFormUsrA.setXml(formXml);
		
		shHierFormUsrB = new FormDTO();
		shHierFormUsrB.setUserKey(userB.getKey());
		shHierFormUsrB.setPolicy(Policy.SHARED_HIERARCHICAL);
		shHierFormUsrB.setXml(formXml);
		shHierFormUsrB.setLists(listUsersFromB);
				
		shSocFormUsrB  = new FormDTO();
		shSocFormUsrB.setUserKey(userB.getKey());
		shSocFormUsrB.setPolicy(Policy.SHARED_SOCIAL);
		shSocFormUsrB.setXml(formXml);
		shSocFormUsrB.setLists(listUsersFromB);
						
		pubFormUsrC    = new FormDTO();
		pubFormUsrC.setUserKey(userC.getKey());
		pubFormUsrC.setPolicy(Policy.PUBLIC);
		pubFormUsrC.setXml(formXml);
						
		formEditorEjb.saveNewForm(privFormUsrA);
		formEditorEjb.saveNewForm(shHierFormUsrB);
		formEditorEjb.saveNewForm(shSocFormUsrB);
		formEditorEjb.saveNewForm(pubFormUsrC);
	}
	
	@After
	public void cleanUp(){
		emHectorImpl.dropColumnFamily(User.class);
		emHectorImpl.dropColumnFamily(MaritacaList.class);
		emHectorImpl.dropColumnFamily(Form.class);
		emHectorImpl.dropColumnFamily(Answer.class);		
	}
			
	@Test
	public void testAnswerListPrivateFormUserA(){
		try{
			answersListerEjb.findAnswerListerDTO(privFormUsrA, userA);			
		}catch(AuthorizationDenied e){			
			fail();
		}
		
		try{
			answersListerEjb.findAnswerListerDTO(privFormUsrA, userB);
			fail();
		}catch(AuthorizationDenied e){			
		}

		try{
			answersListerEjb.findAnswerListerDTO(privFormUsrA, userC);
			fail();
		}catch(AuthorizationDenied e){			
		}
	}
	
	@Test
	public void testAnswerListPublicFormUserC(){
		try{
			answersListerEjb.findAnswerListerDTO(pubFormUsrC, userA);			
		}catch(AuthorizationDenied e){			
			fail();
		}
		
		try{
			answersListerEjb.findAnswerListerDTO(pubFormUsrC, userB);
		}catch(AuthorizationDenied e){			
			fail();
		}

		try{
			answersListerEjb.findAnswerListerDTO(pubFormUsrC, userC);
		}catch(AuthorizationDenied e){			
			fail();
		}
	}
	
	@Test
	public void testAnswerListShHierFormUserB(){
		try{
			answersListerEjb.findAnswerListerDTO(shHierFormUsrB, userA);
			fail();
		}catch(AuthorizationDenied e){			
		}
		
		try{
			answersListerEjb.findAnswerListerDTO(shHierFormUsrB, userB);
		}catch(AuthorizationDenied e){			
		}

		try{
			answersListerEjb.findAnswerListerDTO(shHierFormUsrB, userC);
			fail();
		}catch(AuthorizationDenied e){			
		}
	}
	
	@Test
	public void testAnswerListShSocialFormUserB(){
		try{
			answersListerEjb.findAnswerListerDTO(shSocFormUsrB, userA);
			fail();
		}catch(AuthorizationDenied e){			
		}
		
		try{
			answersListerEjb.findAnswerListerDTO(shSocFormUsrB, userB);
		}catch(AuthorizationDenied e){			
			fail();
		}

		try{
			answersListerEjb.findAnswerListerDTO(shSocFormUsrB, userC);
		}catch(AuthorizationDenied e){			
			fail();
		}		
	}
}
