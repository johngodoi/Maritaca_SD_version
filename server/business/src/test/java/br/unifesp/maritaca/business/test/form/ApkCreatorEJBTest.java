package br.unifesp.maritaca.business.test.form;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.business.account.edit.AccountEditorDAO;
import br.unifesp.maritaca.business.account.edit.AccountEditorEJB;
import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.base.dao.ConfigurationDAO;
import br.unifesp.maritaca.business.base.dao.FormAccessibleByListDAO;
import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.dto.FormWSDTO;
import br.unifesp.maritaca.business.form.edit.ApkCreatorEJB;
import br.unifesp.maritaca.business.form.edit.FormEditorDAO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.list.edit.ListEditorDAO;
import br.unifesp.maritaca.business.list.edit.ListEditorEJB;
import br.unifesp.maritaca.business.list.list.ListMaritacaListDAO;
import br.unifesp.maritaca.business.list.list.ListMaritacaListEJB;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;
import br.unifesp.maritaca.persistence.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.persistence.entity.Configuration;
import br.unifesp.maritaca.persistence.entity.Form;
import br.unifesp.maritaca.persistence.entity.FormAccessibleByList;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.User;
import br.unifesp.maritaca.persistence.util.UtilsPersistence;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class ApkCreatorEJBTest extends BaseEmbededServerSetupTest {

	private static final String FORM_XML = 
				"<form>" +
					"<title>testForm</title>" +
					"<questions>" +
						"<text id=\"0\" next=\"1\" required=\"true\">" +
							"<label>label text</label>" +
							"<help>help text</help>" +
							"<size>100</size>" +
							"<default>default text</default>" +
						"</text>" +
						"<number id=\"4\" next=\"5\" required=\"true\" min=\"0\" max=\"10\">" + 
							"<label>label number</label>" +
							"<help>help number</help>" +
							"<default>0</default>" +
						"</number>" +						
					"</questions>" +
				"</form>";
	
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;
	//User and List	
	private AccountEditorEJB accountEditorEjb;
	private ListMaritacaListEJB listEjb;
	private ListEditorEJB listEditorEjb;
	//Form Editor
	private FormEditorEJB formEditorEJB;
	private ApkCreatorEJB apkCreatorEJB;
	//
	private String LIST_ONE   = "listOne";
	private String LIST_TWO   = "listTwo";
	private String EMAIL_USR1 = "usr1@mail.com";
	private String EMAIL_USR2 = "usr2@mail.com";
	
	private User            user1;
	private User            user2;
	
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
		formEditorEJB.setCreateAnswers(false);
		formEditorEJB.setConfigurationDAO(new ConfigurationDAO());
		formEditorEJB.setListMaritacaListDAO((new ListMaritacaListDAO()));
		formEditorEJB.setUserDAO(new UserDAO());
		formEditorEJB.setFormDAO(new FormDAO());
		formEditorEJB.setFormEditorDAO(new FormEditorDAO());
		formEditorEJB.getFormEditorDAO().setFormAccessibleByListDAO(new FormAccessibleByListDAO());
		//
		apkCreatorEJB = new ApkCreatorEJB();
		apkCreatorEJB.setFormDAO(new FormDAO());
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
	public void testGetUnmarshalledFormFromXML() {
		Form form = new Form();
		form.setTitle("testForm");
		form.setUrl(UtilsPersistence.randomString());
		form.setXml(FORM_XML);
		emHectorImpl.persist(form);
		
		FormDTO formDTO = new FormDTO();
		formDTO.setKey(form.getKey());
		
		FormWSDTO formWSDTO = apkCreatorEJB.getUnmarshalledFormFromXML(formDTO);
		
		assertEquals(form.getTitle(), formWSDTO.getTitle());
		assertEquals(form.getUrl(), formWSDTO.getUrl());
		assertEquals(form.getKey(), formWSDTO.getKey());
	}
	
	@Test
	public void testGetMarshalledFormFromXML() {
		try {
			Form form = new Form();
			form.setTitle("testForm");
			form.setUrl(UtilsPersistence.randomString());
			form.setXml(FORM_XML);
			emHectorImpl.persist(form);
			
			FormDTO formDTO = new FormDTO();
			formDTO.setKey(form.getKey());
			
			String formMarshalled = apkCreatorEJB.getMarshalledFormFromXML(formDTO);
		
			JAXBContext jaxbContext;
				jaxbContext = JAXBContext.newInstance(FormWSDTO.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			InputStream is = new
					ByteArrayInputStream(formMarshalled.getBytes("UTF-8"));
			FormWSDTO formWSDTO = (FormWSDTO)unmarshaller.unmarshal(is);
			
			assertEquals(form.getTitle(), formWSDTO.getTitle());
			assertEquals(form.getUrl(), formWSDTO.getUrl());
			assertEquals(form.getKey(), formWSDTO.getKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	private void createUserAndList() {
		UserDTO userDTO1 = new UserDTO(); userDTO1.setEmail(EMAIL_USR1);
		UserDTO userDTO2 = new UserDTO(); userDTO2.setEmail(EMAIL_USR2);		
		accountEditorEjb.saveNewAccount(userDTO1);
		accountEditorEjb.saveNewAccount(userDTO2);
		user1 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR1);
		user2 = formEditorEJB.getUserDAO().findUserByEmail(EMAIL_USR2);

		MaritacaListDTO list1User2DTO = new MaritacaListDTO();			
		MaritacaListDTO list2User1DTO = new MaritacaListDTO();
		list1User2DTO.setOwner(user2.getKey()); list1User2DTO.setName(LIST_ONE);
		list2User1DTO.setOwner(user1.getKey()); list2User1DTO.setName(LIST_TWO);		
		listEditorEjb.saveMaritacaList(list1User2DTO);
		listEditorEjb.saveMaritacaList(list2User1DTO);
	}
	
}
