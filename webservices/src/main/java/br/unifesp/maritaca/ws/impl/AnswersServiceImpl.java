package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ws.rs.Path;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.ws.api.AnswersService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

@Path("/answer")
public class AnswersServiceImpl implements AnswersService {

	private FormAnswerModel formAnswModel;

	public AnswersServiceImpl() {
		formAnswModel = ModelFactory.getInstance().createFormResponseModel();
	}

	public FormAnswerModel getFormAnswerModel() {
		return formAnswModel;
	}

	public void setFormAnswerModel(FormAnswerModel formResponse) {
		this.formAnswModel = formResponse;
	}
	
	@Override
	public Answer getAnswer(String respId)throws MaritacaWSException{
		UUID uuid = UUID.fromString(respId);
		Answer resp = null;
		resp = formAnswModel.getAnswer(uuid);
		if(resp != null)
		return resp;
		else{
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode());
			error.setMessage("Form with Id: " +respId + " not found");
			throw new MaritacaWSException(error);
		}
	}

	@Override
	public MaritacaResponse saveAnswer(String xmlAnsw, String formId, String userId)throws MaritacaWSException{
		Answer answ = new Answer();
		answ.setForm(formId);
		answ.setXml(xmlAnsw);
		answ.setUser(userId);
		if (formAnswModel.saveAnswer(answ)) {
			XmlSavedResponse okresp = new XmlSavedResponse();
			okresp.setId(answ.getKey());
			okresp.setType(MaritacaResponse.RESPONSE_TYPE);
			return okresp;
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			error.setMessage("unknown error, not possible to save the response");
			throw new MaritacaWSException(error);
		}
	}

	@Override
	public MaritacaResponse listAnswersMinimal(String formId) {
		UUID uuid = null;
		if(formId != null ){
			uuid = UUID.fromString(formId);
		}
		ResultSetResponse<Answer> resp = new ResultSetResponse<Answer>();
		resp.setList(formAnswModel.listAllAnswersMinimal(uuid));
		return resp;
	}

	@Override
	public MaritacaResponse listAnswersMinimal() {
		return listAnswersMinimal(null);
	}

}
