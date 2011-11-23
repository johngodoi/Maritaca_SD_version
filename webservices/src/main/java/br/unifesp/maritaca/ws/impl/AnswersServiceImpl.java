package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ws.rs.Path;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.FormAnswerControl;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.ws.api.AnswersService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

@Path("/answer")
public class AnswersServiceImpl implements AnswersService {

	private FormAnswerControl formAnswCtlr;

	public AnswersServiceImpl() {
		formAnswCtlr = ControllerFactory.getInstance().createFormResponseCtrl();
	}

	public FormAnswerControl getFormAnswerControl() {
		return formAnswCtlr;
	}

	public void setFormAnswerControl(FormAnswerControl formResponse) {
		this.formAnswCtlr = formResponse;
	}
	
	@Override
	public Answer getAnswer(String respId)throws MaritacaWSException{
		UUID uuid = UUID.fromString(respId);
		Answer resp = null;
		resp = formAnswCtlr.getAnswer(uuid);
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
	public MaritacaResponse saveAnswer(String xmlAnsw, String formId)throws MaritacaWSException{
		Answer answ = new Answer();
		answ.setForm(formId);
		answ.setXml(xmlAnsw);
		if (formAnswCtlr.saveAnswer(answ)) {
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
		resp.setList(formAnswCtlr.listAllAnswersMinimal(uuid));
		return resp;
	}

	@Override
	public MaritacaResponse listAnswersMinimal() {
		return listAnswersMinimal(null);
	}

}
