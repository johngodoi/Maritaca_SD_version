package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.answer.list.AnswersListerEJB;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.ws.api.AnswersService;
import br.unifesp.maritaca.ws.api.resp.AnswerListResponse;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;
import br.unifesp.maritaca.ws.util.UtilsWS;

@Stateless
@Path("/answer")
public class AnswersServiceImpl implements AnswersService {
	
	private static final Log log = LogFactory.getLog(AnswersServiceImpl.class);

	@Inject
	private AnswersListerEJB answersListerEJB;
	
	private UserDTO userDTO;
	
	public AnswersServiceImpl() { 
		log.info("in AnswersServiceImpl");
	}

	@Override
	public Answer getAnswer(HttpServletRequest request, String respId) 
			throws MaritacaWSException {
		setUserDTO(UtilsWS.createUserDTO(request));
		UUID uuid = UUID.fromString(respId);
		Answer resp = null;
		//TODO
//		resp = formRespModel.getAnswer(uuid);
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
	public MaritacaResponse saveAnswer(HttpServletRequest request,
			String xmlAnsw, String formId, String userId) 
					throws MaritacaWSException {
		setUserDTO(UtilsWS.createUserDTO(request));
		Answer answ = new Answer();
		answ.setForm(formId);
		answ.setXml(xmlAnsw);
		answ.setUser(userId);
		//TODO
//		if (formRespModel.saveAnswer(answ)) {
//			XmlSavedResponse okresp = new XmlSavedResponse();
//			okresp.setId(answ.getKey());
//			okresp.setType(MaritacaResponse.RESPONSE_TYPE);
//			return okresp;
//		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
					.getStatusCode());
			error.setMessage("unknown error, not possible to save the response");
			throw new MaritacaWSException(error);
//		}
	}

	@Override
	public MaritacaResponse listAnswersMinimal(HttpServletRequest request,
			String formId) {
		// TODO
		UUID uuid = null;
		if (formId != null) {
			uuid = UUID.fromString(formId);
		}
		AnswerListResponse resp = new AnswerListResponse();
//		resp.setList(getFormRespModel().listAllAnswersMinimal(uuid));
		return resp;
	}
	
	@Override
	public MaritacaResponse listAnswersMinimal(HttpServletRequest request) {
		// TODO
		return null;
	}
	
	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}


}
