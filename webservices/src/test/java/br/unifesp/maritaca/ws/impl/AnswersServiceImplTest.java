package br.unifesp.maritaca.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

public class AnswersServiceImplTest {
	FormAnswerModel frControl;
	AnswersServiceImpl answService;
	private static final String uuid = "637dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid2 = "737dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String uuid3 = "747dea60-146e-11e1-a7c0-d2b70b6d4d67";

	@Before
	public void setUp() throws Exception {
		frControl = mock(FormAnswerModel.class);
		answService = new AnswersServiceImpl();
		answService.setFormAnswerModel(frControl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testgetFormResponse() {
		String xml = "<abc/>";
		Answer response = new Answer();
		response.setKey(uuid);
		response.setForm(uuid2);
		response.setXml(xml);
		try {
			when(frControl.getAnswer(any(UUID.class))).thenReturn(response);

			Answer resp1 = answService.getAnswer(uuid);
			assertEquals(response.getKey(), resp1.getKey());

			when(frControl.getAnswer(any(UUID.class))).thenReturn(null);

			try {
				answService.getAnswer(uuid);
			} catch (Exception e) {
				assertTrue(e instanceof MaritacaWSException);
			}

			when(frControl.getAnswer(any(UUID.class))).thenThrow(
					new IllegalArgumentException("default exception"));
			try {
				answService.getAnswer(uuid2);
			} catch (Exception e) {
				assertEquals("default exception", e.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSaveResponse() {
		String xmlResp = "<something/>";
		try {

			when(frControl.saveAnswer(any(Answer.class))).thenAnswer(
					new org.mockito.stubbing.Answer<Boolean>() {

						@Override
						public Boolean answer(InvocationOnMock invocation)
								throws Throwable {
							Object args[] = invocation.getArguments();
							Answer response = (Answer) args[0];
							response.setKey(uuid);
							return true;
						}

					});

			MaritacaResponse resp = answService.saveAnswer(xmlResp, uuid2, uuid3);
			assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(),
					resp.getCode());
			assertEquals(javax.ws.rs.core.Response.Status.OK.getReasonPhrase(),
					resp.getStatus());
			assertTrue(resp instanceof XmlSavedResponse);

			XmlSavedResponse okresp = (XmlSavedResponse) resp;
			assertEquals(uuid, okresp.getId());
			assertEquals(MaritacaResponse.RESPONSE_TYPE, okresp.getType());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSaveFormError() {
		String xmlResp = "<something/>";
		try {

			when(frControl.saveForm(any(Form.class))).thenReturn(false);

			try {
				MaritacaResponse resp = answService.saveAnswer(xmlResp, uuid, uuid3);
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
	public void testListResponsesMinimal() {
		String[] ids = { uuid, uuid2 };
		Answer form1 = new Answer();
		form1.setKey(UUID.fromString(uuid));

		Answer form2 = new Answer();
		form2.setKey(UUID.fromString(uuid2));

		ArrayList<Answer> list = new ArrayList<Answer>();
		list.add(form1);
		list.add(form2);

		try {
			when(frControl.listAllAnswersMinimal(any(UUID.class))).thenReturn(list);
			MaritacaResponse resp = answService.listAnswersMinimal(uuid3);
			assertTrue(resp instanceof ResultSetResponse);
			ResultSetResponse<Answer> okresp = (ResultSetResponse<Answer>) resp;
			assertEquals(list.size(), okresp.getSize());
			int i = 0;
			for (Answer r : okresp.getList()) {
				assertEquals(ids[i++], r.getKey().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
}
