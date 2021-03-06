package br.unifesp.maritaca.business.test.account;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.business.account.edit.AccountEditorDAO;
import br.unifesp.maritaca.business.account.edit.AccountEditorEJB;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.list.edit.ListEditorDAO;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;
import br.unifesp.maritaca.persistence.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class AccountTests extends BaseEmbededServerSetupTest{

	private static final String USER_EMAIL = "user@domain.com";
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;	
	private AccountEditorEJB        accountEditorEjb;
	
	
	@Before
	public void setUp(){
		EntityManagerFactory.setHectorEntityManager(emHectorImpl);
		accountEditorEjb = new AccountEditorEJB();
		accountEditorEjb.setAccountEditorDAO(new AccountEditorDAO());
		accountEditorEjb.getAccountEditorDAO().setListEditorDAO(new ListEditorDAO());
		
		emHectorImpl.createColumnFamily(User.class);
		emHectorImpl.createColumnFamily(MaritacaList.class);
	}
	
	@After
	public void cleanUp(){
		emHectorImpl.dropColumnFamily(User.class);
		emHectorImpl.dropColumnFamily(MaritacaList.class);
	}

	
	@Test
	public void createAccountTest(){
		UserDTO userDto = new UserDTO();		
		userDto.setEmail(USER_EMAIL);
		try{
			accountEditorEjb.saveNewAccount(userDto);
		}catch(IllegalArgumentException e){
			fail();
		}
		assertNotNull(userDto.getKey());
		assertNotNull(userDto.getMaritacaList());
		
		try{
			accountEditorEjb.saveNewAccount(userDto);
			fail();
		}catch(IllegalArgumentException e){			
		}
	}
	
	@Test
	public void registeredEmailTest(){
		assertFalse(accountEditorEjb.registeredEmail(USER_EMAIL));
		
		UserDTO userDto = new UserDTO();
		userDto.setEmail(USER_EMAIL);
		accountEditorEjb.saveNewAccount(userDto);
		
		assertTrue(accountEditorEjb.registeredEmail(USER_EMAIL));
	}
}
