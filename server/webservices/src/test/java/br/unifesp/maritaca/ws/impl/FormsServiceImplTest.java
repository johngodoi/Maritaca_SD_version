package br.unifesp.maritaca.ws.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.unifesp.maritaca.control.FormResponseController;
import br.unifesp.maritaca.core.Form;

public class FormsServiceImplTest {
	FormResponseController frControl;
	FormsServiceImpl formService;

	@Before
	public void setUp() throws Exception {
		frControl = mock(FormResponseController.class);
		formService = new FormsServiceImpl();
		formService.setFormResponse(frControl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetForm() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveForm() {
		String xmlForm = "<something/>";
		try {
			when(frControl.saveForm(new Form())).thenReturn(true);
			Response resp = formService.saveForm(xmlForm);
			assertEquals(200, resp.getStatus());
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

}
