package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.ws.api.FormsService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

@Path("/form")
public class FormsServiceImpl implements FormsService {

	private FormAnswerModel formRespModel;

	public FormsServiceImpl() {
		formRespModel = ModelFactory.getInstance().createFormResponseModel();
	}

	public FormAnswerModel getFormAnswModel() {
		return formRespModel;
	}

	public void setFormResponse(FormAnswerModel formResponse) {
		this.formRespModel = formResponse;
	}

	@Override
	public Form getForm(String formId) throws MaritacaWSException {
		UUID uuid = UUID.fromString(formId);
		Form form = null;
		form = formRespModel.getForm(uuid, false);
		if (form != null)
			return form;
		else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(Response.Status.NO_CONTENT.getStatusCode());
			error.setMessage("Form with Id: " + formId + " not found");
			throw new MaritacaWSException(error);
		}
	}

	@Override
	public MaritacaResponse saveForm(String xmlForm, String userId)
			throws MaritacaWSException {
		Form form = new Form();
		form.setXml(xmlForm);
		form.setUser(userId);
		if (formRespModel.saveForm(form)) {
			XmlSavedResponse okresp = new XmlSavedResponse();
			okresp.setId(form.getKey());
			okresp.setType(MaritacaResponse.FORM_TYPE);
			return okresp;
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
					.getStatusCode());
			error.setMessage("unknown error, not possible to save the form");
			throw new MaritacaWSException(error);
		}

	}

	@Override
	public MaritacaResponse listFormsMinimal() {
		ResultSetResponse<Form> resp = new ResultSetResponse<Form>();
		resp.setList(formRespModel.listAllFormsMinimal());
		return resp;
	}

	@Override
	public Form getFormSharing(String url) throws MaritacaWSException {
		String id = formRespModel.getFormIdFromUrl(url);
		if (id != null) {
			return getForm(id);
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(Response.Status.NO_CONTENT.getStatusCode());
			error.setMessage("Form with URL: " + url + " not found");
			throw new MaritacaWSException(error);
		}
	}

}
