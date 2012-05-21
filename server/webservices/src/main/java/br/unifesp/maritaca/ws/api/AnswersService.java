package br.unifesp.maritaca.ws.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import br.unifesp.maritaca.business.answer.list.dto.DataCollectedDTO;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

/**
 * This Interface defines RESTFul services to Answer.
 * 
 * @author emigueltriana and alvarohenry
 */
public interface AnswersService {
	
	/**
	 * Save an Answer for a form
	 * @param xmlResp: XML representation of the new Answer
	 * @param formId: Form Id which the response belongs
	 * @return XmlSavedResponse with the Answer ID and Type.
	 * @throws MaritacaWSException 
	 */
	@PUT
	@Path("add")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse putAnswer(	@Context HttpServletRequest request,
								DataCollectedDTO collectedDTO) 
			throws MaritacaWSException;
	
}
