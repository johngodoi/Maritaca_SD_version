package br.unifesp.maritaca.ws.api.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.account.edit.dto.UserDTO;
import br.unifesp.maritaca.business.answer.edit.AnswerEditorEJB;
import br.unifesp.maritaca.business.answer.list.dto.DataCollectedDTO;
import br.unifesp.maritaca.ws.api.AnswersService;
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
	private AnswerEditorEJB answerEditorEJB;
	
	private UserDTO userDTO;
	
	public AnswersServiceImpl() { 
		log.info("in AnswersServiceImpl");
	}

	@Override
	public MaritacaResponse putAnswer(HttpServletRequest request, DataCollectedDTO collectedDTO) 
				throws MaritacaWSException {
		try{
			setUserDTO(UtilsWS.createUserDTO(request));
			
			answerEditorEJB.saveAnswers(collectedDTO);
			
			XmlSavedResponse okresp = new XmlSavedResponse();
			okresp.setStatus("Opereration successful");
			okresp.setType(MaritacaResponse.RESPONSE_TYPE);
			return okresp;
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse();
			error.setCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
					.getStatusCode());
			error.setMessage("unknown error, not possible to save the response");
			throw new MaritacaWSException(error);
		}
	}
	
	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}


}
