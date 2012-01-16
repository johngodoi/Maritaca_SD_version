package br.unifesp.maritaca.ws.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

public interface AnswersService {
	
	/**
	 * Get an Answer for an UUID
	 * @param respId: Answer Id in UUID (string) format
	 * @return Answer with Form Id, Answer Id and XML
	 * representation of the Answer
	 * @throws MaritacaWSException 
	 */
	@GET
	@Path("/{id}")
	Answer getAnswer(@PathParam("id") String answId) throws MaritacaWSException;
	
	/**
	 * Save an Answer for a form
	 * @param xmlResp: XML representation of the new Answer
	 * @param formId: Form Id which the response belongs
	 * @return XmlSavedResponse with the Answer ID and Type.
	 * @throws MaritacaWSException 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse saveAnswer(@FormParam("xml")String xmlAnsw, @FormParam("formId")String formId,  @FormParam("userId")String userId) throws MaritacaWSException;
	
	/**
	 * Get all Answers of a Form
	 * @param formId: Form ID in UUID format
	 * @return ResultSetResponse with the list of
	 * Answer IDs for the given formId, if no formId
	 * is given, it will return all available Responses
	 * @throws Exception
	 */
	@GET
	@Path("/list/{formId}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse listAnswersMinimal(@PathParam("formId") String formId);
	
	/**
	 * 
	 * @return ResultSetResponse, it will return all available Responses
	 * @throws Exception
	 */
	@GET
	@Path("/list")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse listAnswersMinimal();
}
