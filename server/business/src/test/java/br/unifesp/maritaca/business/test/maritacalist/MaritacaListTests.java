package br.unifesp.maritaca.business.test.maritacalist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

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
import br.unifesp.maritaca.business.list.edit.ListEditorEJB;
import br.unifesp.maritaca.business.list.list.ListMaritacaListDAO;
import br.unifesp.maritaca.business.list.list.ListMaritacaListEJB;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;
import br.unifesp.maritaca.persistence.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class MaritacaListTests extends BaseEmbededServerSetupTest{

	private static final String LIST_ONE_NAME = "listOne";	
	private static final String EMAIL_USR1    = "usr1@mail.com";
	private static final String EMAIL_USR2    = "usr2@mail.com";
	private static final String UUID_1        = "111dea60-146e-11e1-a7c0-d2b70b6d4d67";

	@Resource
	private EntityManagerHectorImpl emHectorImpl;	
	private ListMaritacaListEJB     listEjb;
	private ListEditorEJB   		listEditorEjb;
	
	@Before
	public void setUp(){
		EntityManagerFactory.setHectorEntityManager(emHectorImpl);
		
		listEjb = new ListMaritacaListEJB();
		listEjb.setMaritacaListDAO(new ListMaritacaListDAO());
		
		listEditorEjb = new ListEditorEJB();
		listEditorEjb.setListEditorDAO(new ListEditorDAO());
				
		emHectorImpl.createColumnFamily(MaritacaList.class);
		emHectorImpl.createColumnFamily(User.class);
	}
	
	@After
	public void cleanUp(){
		emHectorImpl.dropColumnFamily(MaritacaList.class);
		emHectorImpl.dropColumnFamily(User.class);
	}
	
	@Test
	public void createListTest(){
		MaritacaListDTO listDTO = new MaritacaListDTO();
		listEditorEjb.saveMaritacaList(listDTO);
	}
	
	@Test
	public void findListTest(){
		UUID ownerId = UUID.fromString(UUID_1);
		
		MaritacaListDTO foundList = null;
		foundList = listEditorEjb.searchMaritacaListByName(LIST_ONE_NAME,ownerId);
		assertTrue(foundList == null);
		
		MaritacaListDTO listDTO = new MaritacaListDTO();
		listDTO.setName(LIST_ONE_NAME);
		listDTO.setOwner(ownerId);
		listEditorEjb.saveMaritacaList(listDTO);
		
		foundList = listEditorEjb.searchMaritacaListByName(LIST_ONE_NAME,ownerId);
		assertTrue(foundList != null);
		assertTrue(foundList.getName().equals(LIST_ONE_NAME));
	}
	
	@Test
	public void removeListTest(){
		UUID ownerId = UUID.fromString(UUID_1);
		
		MaritacaListDTO listDTO = new MaritacaListDTO();
		listDTO.setName(LIST_ONE_NAME);
		listDTO.setOwner(ownerId);
		
		listEditorEjb.saveMaritacaList(listDTO);
		listEjb.removeMaritacaList(listDTO.getKey());
				
		MaritacaListDTO foundList;
		foundList = listEditorEjb.searchMaritacaListByName(LIST_ONE_NAME,ownerId);
		assertTrue(foundList == null);		
	}
	
	@Test
	public void listByUserTest(){
		AccountEditorEJB accountEditor = new AccountEditorEJB();
		accountEditor.setAccountEditorDAO(new AccountEditorDAO());
		accountEditor.getAccountEditorDAO().setListEditorDAO(new ListEditorDAO());
		
		UserDTO user1 = new UserDTO();
		UserDTO user2 = new UserDTO();
		
		user1.setEmail(EMAIL_USR1);
		user2.setEmail(EMAIL_USR2);
		
		accountEditor.saveNewAccount(user1);
		accountEditor.saveNewAccount(user2);
				
		MaritacaListDTO list1User1 = new MaritacaListDTO();				
		MaritacaListDTO list2User1 = new MaritacaListDTO();
		MaritacaListDTO list1User2 = new MaritacaListDTO();
		
		list1User1.setOwner(user1.getKey());
		list2User1.setOwner(user1.getKey());
		list1User2.setOwner(user2.getKey());
		
		listEditorEjb.saveMaritacaList(list1User1);
		listEditorEjb.saveMaritacaList(list2User1);
		listEditorEjb.saveMaritacaList(list1User2);
				
		List<MaritacaListDTO> listFoundUsr1;		
		List<MaritacaListDTO> listFoundUsr2;
		
		listFoundUsr1 = listEjb.getMaritacaListsByOwner(user1.getKey());
		listFoundUsr2 = listEjb.getMaritacaListsByOwner(user2.getKey());
		
		assertTrue(listFoundUsr1.size()==3);
		assertTrue(listFoundUsr2.size()==2);
	}
	
	@Test
	public void searchUserListTest(){
		AccountEditorEJB accountEditor = new AccountEditorEJB();
		accountEditor.setAccountEditorDAO(new AccountEditorDAO());
		accountEditor.getAccountEditorDAO().setListEditorDAO(new ListEditorDAO());
		
		UserDTO user1 = new UserDTO();
		UserDTO user2 = new UserDTO();
		
		user1.setEmail(EMAIL_USR1);
		user2.setEmail(EMAIL_USR2);
		
		accountEditor.saveNewAccount(user1);
		accountEditor.saveNewAccount(user2);
				
		MaritacaListDTO list1User1 = new MaritacaListDTO();				
		MaritacaListDTO list1User2 = new MaritacaListDTO();
		
		list1User1.setOwner(user1.getKey());
		list1User2.setOwner(user2.getKey());
		
		list1User1.setName(LIST_ONE_NAME);
		list1User2.setName(LIST_ONE_NAME);
		
		listEditorEjb.saveMaritacaList(list1User1);
		listEditorEjb.saveMaritacaList(list1User2);
		
		MaritacaListDTO listDto;
		listDto = listEditorEjb.searchMaritacaListByName(LIST_ONE_NAME, user1.getKey());
		assertNotNull(listDto);
		assertEquals(listDto.getName(),LIST_ONE_NAME);
		assertEquals(listDto.getOwner(),user1.getKey());

		listDto = listEditorEjb.searchMaritacaListByName(LIST_ONE_NAME, user2.getKey());
		assertNotNull(listDto);
		assertEquals(listDto.getName(),LIST_ONE_NAME);
		assertEquals(listDto.getOwner(),user2.getKey());
	}	
}
