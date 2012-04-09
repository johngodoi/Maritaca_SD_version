package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.ws.api.FormsService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.FormListResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

@Path("/form")
public class FormsServiceImpl implements FormsService {

	private static final Log log = LogFactory.getLog(FormsServiceImpl.class);
	private FormAnswerModel formRespModel;
	private User currentUser;

	public FormsServiceImpl() {	}

	public FormsServiceImpl(@HeaderParam("curruserkey") String userkey) {
		//get the user
		if(userkey == null){
			throw new RuntimeException("not current user");
		}
		log.debug("current user: " + userkey);
		User user = new User();
		user.setKey(userkey);
		setCurrentUser(user);
		
		formRespModel = ModelFactory.getInstance().createFormResponseModel(getCurrentUser());
		ModelFactory.getInstance().registryUser(user);
		
	}

	public FormAnswerModel getFormAnswModel() {
		return getFormRespModel();
	}

	@Override
	public Form getForm(String formId) throws MaritacaWSException {
		UUID uuid = UUID.fromString(formId);
		Form form = null;
		form = getFormRespModel().getForm(uuid, false);
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
		if (getFormRespModel().saveForm(form)) {
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
		FormListResponse resp = new FormListResponse();
		resp.setList(getFormRespModel().listAllFormsMinimal());
		return resp;
	}

	@Override
	public Form getFormSharing(String url) throws MaritacaWSException {
		String id = getFormRespModel().getFormIdFromUrl(url);
		if (id != null) {
			return getForm(id);
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(Response.Status.NO_CONTENT.getStatusCode());
			error.setMessage("Form with URL: " + url + " not found");
			throw new MaritacaWSException(error);
		}
	}

	private FormAnswerModel getFormRespModel() {
		if(formRespModel == null){
			formRespModel = ModelFactory.getInstance().createFormResponseModel();
		}
		return formRespModel;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public void setFormAnswerModel(FormAnswerModel frControl) {
		this.formRespModel = frControl;
	}

}
