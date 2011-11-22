package br.unifesp.maritaca.ws.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.FormResponseController;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.ws.api.FormsService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;
import br.unifesp.maritaca.ws.util.MaritacaExceptionMapper;

@Path("/forms")
public class FormsServiceImpl implements FormsService {

	private FormResponseController formRespCtlr;

	public FormsServiceImpl() {
		formRespCtlr = ControllerFactory.getInstance().createFormResponseCtrl();
	}

	public FormResponseController getFormResponse() {
		return formRespCtlr;
	}

	public void setFormResponse(FormResponseController formResponse) {
		this.formRespCtlr = formResponse;
	}

	@Override
	public Form getForm(String formId) throws Exception {
		UUID uuid = UUID.fromString(formId);
		Form form = null;
		form = formRespCtlr.getForm(uuid);
		if(form != null)
		return form;
		else{
			ErrorResponse error = new ErrorResponse();
			error.setCode(Response.Status.NO_CONTENT.getStatusCode());
			error.setMessage("Form with Id: " +formId + " not found");
			throw new MaritacaWSException(error);
		}
	}

	@Override
	public MaritacaResponse saveForm(String xmlForm) throws Exception {
		Form form = new Form();
		form.setXml(xmlForm);
		MaritacaResponse resp;
		if (formRespCtlr.saveForm(form)) {
			XmlSavedResponse okresp = new XmlSavedResponse();
			okresp.setId(form.getKey());
			okresp.setType(MaritacaResponse.FORM_TYPE);
			resp = okresp;
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setMessage("unknown error");
			resp = error;
		}

		return resp;

	}

	@Override
	public MaritacaResponse listFormsMinimal() throws Exception {
		ResultSetResponse<Form> resp = new ResultSetResponse<Form>();
		resp.setList(formRespCtlr.listAllFormsMinimal());
		return resp;
	}

}
