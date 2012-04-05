package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.ws.api.AnswersService;
import br.unifesp.maritaca.ws.api.resp.AnswerListResponse;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

@Path("/answer")
public class AnswersServiceImpl implements AnswersService {
	private static final Log log = LogFactory.getLog(AnswersServiceImpl.class);

	private FormAnswerModel formRespModel;
	private User currentUser;
	
	public AnswersServiceImpl() { }

	public AnswersServiceImpl(@HeaderParam("curruserkey") String userkey) {
		// get the user
		if (userkey == null) {
			throw new RuntimeException("not current user");
		}
		log.debug("current user: " + userkey);
		User user = new User();
		user.setKey(userkey);
		setCurrentUser(user);

		formRespModel = ModelFactory.getInstance().createFormResponseModel(
				getCurrentUser());
		ModelFactory.getInstance().registryUser(user);
	}

	public FormAnswerModel getFormAnswerModel() {
		return formRespModel;
	}

	public void setFormAnswerModel(FormAnswerModel formResponse) {
		this.formRespModel = formResponse;
	}

	@Override
	public Answer getAnswer(String respId) throws MaritacaWSException {
		UUID uuid = UUID.fromString(respId);
		Answer resp = null;
		resp = formRespModel.getAnswer(uuid);
		if (resp != null)
			return resp;
		else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.NO_CONTENT
					.getStatusCode());
			error.setMessage("Form with Id: " + respId + " not found");
			throw new MaritacaWSException(error);
		}
	}

	@Override
	public MaritacaResponse saveAnswer(String xmlAnsw, String formId,
			String userId) throws MaritacaWSException {
		Answer answ = new Answer();
		answ.setForm(formId);
		answ.setXml(xmlAnsw);
		answ.setUser(userId);
		if (formRespModel.saveAnswer(answ)) {
			XmlSavedResponse okresp = new XmlSavedResponse();
			okresp.setId(answ.getKey());
			okresp.setType(MaritacaResponse.RESPONSE_TYPE);
			return okresp;
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
					.getStatusCode());
			error.setMessage("unknown error, not possible to save the response");
			throw new MaritacaWSException(error);
		}
	}

	@Override
	public MaritacaResponse listAnswersMinimal(String formId) {
		UUID uuid = null;
		if (formId != null) {
			uuid = UUID.fromString(formId);
		}
		AnswerListResponse resp = new AnswerListResponse();
		resp.setList(getFormRespModel().listAllAnswersMinimal(uuid));
		return resp;
	}
	
	private FormAnswerModel getFormRespModel() {
		if(formRespModel == null){
			formRespModel = ModelFactory.getInstance().createFormResponseModel();
		}
		return formRespModel;
	}

	@Override
	public MaritacaResponse listAnswersMinimal() {
		return listAnswersMinimal(null);
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

}
