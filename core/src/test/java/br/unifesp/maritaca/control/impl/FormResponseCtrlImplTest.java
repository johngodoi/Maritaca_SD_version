package br.unifesp.maritaca.control.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public class FormResponseCtrlImplTest {
	private EntityManager em;
	private FormResponseCtrlImpl frControl;

	@Before
	public void setUp() throws Exception {
		em = mock(EntityManager.class);
		frControl = new FormResponseCtrlImpl();
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
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
