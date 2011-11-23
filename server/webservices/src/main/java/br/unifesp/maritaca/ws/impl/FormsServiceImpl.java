package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.FormAnswerControl;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.ws.api.FormsService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

@Path("/form")
public class FormsServiceImpl implements FormsService {

	private FormAnswerControl formRespCtlr;

	public FormsServiceImpl() {
		formRespCtlr = ControllerFactory.getInstance().createFormResponseCtrl();
	}

	public FormAnswerControl getFormResponse() {
		return formRespCtlr;
	}

	public void setFormResponse(FormAnswerControl formResponse) {
		this.formRespCtlr = formResponse;
	}

	@Override
	public Form getForm(String formId)throws MaritacaWSException{
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
	public MaritacaResponse saveForm(String xmlForm) throws MaritacaWSException {
		Form form = new Form();
		form.setXml(xmlForm);
		if (formRespCtlr.saveForm(form)) {
			XmlSavedResponse okresp = new XmlSavedResponse();
			okresp.setId(form.getKey());
			okresp.setType(MaritacaResponse.FORM_TYPE);
			return okresp;
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			error.setMessage("unknown error, not possible to save the form");
			throw new MaritacaWSException(error);
		}

	}

	@Override
	public MaritacaResponse listFormsMinimal() {
		ResultSetResponse<Form> resp = new ResultSetResponse<Form>();
		resp.setList(formRespCtlr.listAllFormsMinimal());
		return resp;
	}

}
