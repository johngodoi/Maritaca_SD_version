package br.unifesp.maritaca.business.test.answer;

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
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.answer.list.AnswersListerEJB;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.list.edit.ListEditorEJB;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.core.Answer;
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
	
	@Before
	public void setUpTets(){
		EntityManagerFactory.setHectorEntityManager(emHectorImpl);
		
		emHectorImpl.createColumnFamily(User.class);
		emHectorImpl.createColumnFamily(MaritacaList.class);
		emHectorImpl.createColumnFamily(Form.class);
		emHectorImpl.createColumnFamily(Answer.class);
		
		userA = new UserDTO();
		userB = new UserDTO();		
		userC = new UserDTO();

		accountEditorEjb.saveAccount(userA);		
		accountEditorEjb.saveAccount(userB);
		accountEditorEjb.saveAccount(userC);
		
		listFromUsrB = new MaritacaListDTO();
		listFromUsrB.setOwner(userB.getKey());
		List<UUID> listUsers = new ArrayList<UUID>();
		listUsers.add(userC.getKey());
		listFromUsrB.setUsers(listUsers);
		listEditorEjb.saveMaritacaList(listFromUsrB);
		
		privFormUsrA   = new FormDTO();
		privFormUsrA.setUserKey(userA.getKey());
		privFormUsrA.setPolicy(Policy.PRIVATE);
		
		shHierFormUsrB = new FormDTO();
		shHierFormUsrB.setUserKey(userB.getKey());
		shHierFormUsrB.setPolicy(Policy.SHARED_HIERARCHICAL);
				
		shSocFormUsrB  = new FormDTO();
		shSocFormUsrB.setUserKey(userB.getKey());
		shSocFormUsrB.setPolicy(Policy.SHARED_SOCIAL);
						
		pubFormUsrC    = new FormDTO();
		pubFormUsrC.setUserKey(userC.getKey());
		pubFormUsrC.setPolicy(Policy.PUBLIC);
						
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
	public void testAnswerListPrivateForm(){
		answersListerEjb.findAnswerListerDTO(privFormUsrA, userA);		
		answersListerEjb.findAnswerListerDTO(privFormUsrA, userB);		
		answersListerEjb.findAnswerListerDTO(privFormUsrA, userC);				
	}
	
	@Test
	public void testAnswerListPublicForm(){
		answersListerEjb.findAnswerListerDTO(privFormUsrA, userA);		
		answersListerEjb.findAnswerListerDTO(privFormUsrA, userB);		
		answersListerEjb.findAnswerListerDTO(privFormUsrA, userC);				
	}
	
	
}
