package br.unifesp.maritaca.control.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public class FormResponseCtrlImplTest {
	private EntityManager em;
	private FormAnswerCtrlImpl frControl;

	@Before
	public void setUp() throws Exception {
		em = mock(EntityManager.class);
		frControl = new FormAnswerCtrlImpl();
		frControl.setEntityManager(em);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSaveForm() {
		Form form = new Form();
		try {
			when(em.persist(form)).thenReturn(true);
			assertNull(form.getKey());
			assertTrue(frControl.saveForm(form));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
	}

	@Test
	public void testGetForm() {
		UUID uid = UUID.randomUUID();
		Form form = new Form();
		form.setKey(uid);
		try {
			when(em.find(Form.class, uid)).thenReturn(form);
			assertNotNull(frControl.getForm(uid));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
