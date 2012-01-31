package br.unifesp.maritaca.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;

public class FormAnswerCtrlImplTest {
	private static final String uuid = "637dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid2 = "537dea60-146e-11e1-a7c0-d2b70b6d4d67";
	// private static final String uuid3 =
	// "437dea60-146e-11e1-a7c0-d2b70b6d4d67";

	private EntityManager em;
	private FormAnswerModelImpl frControl;
	private UserModel userModel;

	@Before
	public void setUp() throws Exception {
		em = mock(EntityManager.class);
		frControl = new FormAnswerModelImpl();
		frControl.setEntityManager(em);
		userModel = mock(UserModel.class);
		frControl.setUserModel(userModel);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSaveForm() {
		Form form = new Form();
		form.setUser(uuid2);

		when(em.persist(any())).thenAnswer(new Answer<Boolean>() {

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments()[0] instanceof Form) {
					Form form = (Form) invocation.getArguments()[0];
					form.setKey(uuid);
				}
				return true;
			}
		});

		when(em.rowDataExists((Class<User>) notNull(), any(UUID.class)))
				.thenReturn(true);
		
		when(userModel.getAllUsersGroup()).thenAnswer(new Answer<Group>() {

			@Override
			public Group answer(InvocationOnMock invocation) throws Throwable {
				Group g = new Group();
				g.setKey(uuid2);
				g.setName(ManagerModelImpl.ALL_USERS);
				return g;
			}
		});

		assertNull(form.getKey());
		assertTrue(frControl.saveForm(form));
		assertEquals(uuid, form.getKey().toString());
	}

	@Test
	public void testGetForm() {
		assertNull(frControl.getForm(null));

		UUID uid = UUID.fromString(uuid);
		Form form = new Form();
		form.setKey(uid);
		when(em.find(Form.class, uid)).thenReturn(form);
		assertNotNull(frControl.getForm(uid));
	}

	@Test
	public void testListAllForms() {
		// dumb test while FormAnswerControl just redirect the request
		// to entitymanager.listAll(Form.class)
		Form form1 = new Form();
		form1.setKey(uuid);
		Form form2 = new Form();
		form2.setKey(uuid2);
		ArrayList<Form> list = new ArrayList<Form>();
		list.add(form1);
		list.add(form2);

		when(em.listAll(Form.class)).thenReturn(list);

		Collection<Form> lresult = frControl.listAllForms();
		assertNotNull(lresult);

		for (Form f : lresult) {
			assertTrue(lresult.contains(f));
		}
	}

	// Tests no finished because methods in
	// FormAnswerControlImpl are just redirecting
	// the request to the entitymanager

	// @Test
	// public void testListAllFormsMinimal(){}
	//
	// @Test
	// public void testSaveAnswer(){}
	//
	// @Test
	// public void testGetAnswer(){}
	//
	// @Test
	// public void testlistAllAnswer(){}
	//
	// @Test
	// public void testlistAllAnswerMinimal(){}

}
