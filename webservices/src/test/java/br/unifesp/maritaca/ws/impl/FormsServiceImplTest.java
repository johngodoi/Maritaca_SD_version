package br.unifesp.maritaca.ws.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.hamcrest.BaseMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.unifesp.maritaca.control.FormResponseController;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;

public class FormsServiceImplTest {
	FormResponseController frControl;
	FormsServiceImpl formService;
	private static final String uuid="637dea60-146e-11e1-a7c0-d2b70b6d4d67";

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
	@Ignore
	public void testGetForm() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveForm() {
		String xmlForm = "<something/>";
		try {
			
			when(frControl.saveForm(any(Form.class))).thenAnswer(new Answer<Boolean>() {

				@Override
				public Boolean answer(InvocationOnMock invocation)
						throws Throwable {
					Object args[] = invocation.getArguments();
					Form form = (Form)args[0];
					form.setKey(UUID.fromString(uuid));
					return true;
				}
				
			});
		
			MaritacaResponse resp = formService.saveForm(xmlForm);
			assertEquals(MaritacaResponse.OK_CODE, resp.getCode());
			assertEquals(MaritacaResponse.OK, resp.getStatus());
			assertTrue(resp instanceof XmlSavedResponse);
		
			XmlSavedResponse okresp = (XmlSavedResponse)resp;
			assertEquals(uuid, okresp.getId());
			assertEquals(MaritacaResponse.FORM_TYPE, okresp.getType());
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
