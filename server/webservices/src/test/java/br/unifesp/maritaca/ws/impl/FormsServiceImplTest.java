package br.unifesp.maritaca.ws.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

public class FormsServiceImplTest {
	FormAnswerModel frControl;
	FormsServiceImpl formService;
	private static final String uuid = "637dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid2 = "737dea60-146e-11e1-a7c0-d2b70b6d4d67";

	@Before
	public void setUp() throws Exception {
		frControl = mock(FormAnswerModel.class);
		formService = new FormsServiceImpl();
		formService.setFormResponse(frControl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetForm() {
		String xml = "<abc/>";
		Form form = new Form();
		form.setKey(UUID.fromString(uuid));
		form.setXml(xml);
		try {
			when(frControl.getForm(any(UUID.class), any(Boolean.class))).thenReturn(form);
			Form fresp = formService.getForm(uuid);
			assertEquals(form.getKey(), fresp.getKey());

			when(frControl.getForm(form.getKey(), false)).thenThrow(
					new IllegalArgumentException("default exception"));
			try {
				formService.getForm(uuid2);
			} catch (Exception e) {
				assertEquals("default exception", e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSaveForm() {
		String xmlForm = "<something/>";
		try {

			when(frControl.saveForm(any(Form.class))).thenAnswer(
					new Answer<Boolean>() {

						@Override
						public Boolean answer(InvocationOnMock invocation)
								throws Throwable {
							Object args[] = invocation.getArguments();
							Form form = (Form) args[0];
							form.setKey(UUID.fromString(uuid));
							return true;
						}

					});

			MaritacaResponse resp = formService.saveForm(xmlForm, uuid2);
			assertEquals(Response.Status.OK.getStatusCode(), resp.getCode());
			assertEquals(Response.Status.OK.getReasonPhrase(), resp.getStatus());
			assertTrue(resp instanceof XmlSavedResponse);

			XmlSavedResponse okresp = (XmlSavedResponse) resp;
			assertEquals(uuid, okresp.getId());
			assertEquals(MaritacaResponse.FORM_TYPE, okresp.getType());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSaveFormError() {
		String xmlForm = "<something/>";
		try {

			when(frControl.saveForm(any(Form.class))).thenReturn(false);

			try {
				MaritacaResponse resp = formService.saveForm(xmlForm, uuid2);
			} catch (Exception e) {
				assertTrue(e instanceof MaritacaWSException);
				MaritacaWSException me = (MaritacaWSException) e;
				assertEquals(MaritacaResponse.FAIL, me.getResponse()
						.getStatus());
				assertEquals(
						javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
								.getStatusCode(),
						me.getResponse().getCode());
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testListFormsMinimal() {
		String[] ids = { uuid, uuid2 };
		Form form1 = new Form();
		form1.setKey(UUID.fromString(uuid));

		Form form2 = new Form();
		form2.setKey(UUID.fromString(uuid2));

		ArrayList<Form> list = new ArrayList<Form>();
		list.add(form1);
		list.add(form2);

		try {
			when(frControl.listAllFormsMinimal()).thenReturn(list);
			MaritacaResponse resp = formService.listFormsMinimal();
			assertTrue(resp instanceof ResultSetResponse);
			ResultSetResponse<Form> okresp = (ResultSetResponse<Form>) resp;
			assertEquals(list.size(), okresp.getSize());
			int i = 0;
			for (Form f : okresp.getList()) {
				assertEquals(ids[i++], f.getKey().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
}
