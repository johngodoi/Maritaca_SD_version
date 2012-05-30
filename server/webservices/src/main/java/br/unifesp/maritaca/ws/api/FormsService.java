package br.unifesp.maritaca.ws.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

/**
 * This Interface defines RESTFul services to Form.
 * 
 * @author emigueltriana and alvarohenry
 */
public interface FormsService {
	/**
	 * 
	 * @param formId: Id of the form in UUID format
	 * @return Form with Id and XML representation of the form
	 * @throws MaritacaWSException 
	 * @throws Exception
	 */
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_XML})
	FormDTO getForm(@Context HttpServletRequest request,
					@PathParam("id") String formId) throws MaritacaWSException;
	
	/**
	 * 
	 * @return ResultSetResponse with a  collection of the formId of all 
	 * available forms
	 * @throws Exception
	 */
	@GET
	@Path("/list")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse listFormsMinimal(@Context HttpServletRequest request) 
			throws MaritacaWSException ;
	
	/**
	 * 
	 * @param url for sharing
	 * @return XML or JSON
	 * @throws MaritacaWSException
	 */
	@GET
	@Path("/share/{url}")
	FormDTO getFormSharing(@Context HttpServletRequest request,
						   @PathParam("url") String url) 
			throws MaritacaWSException;
}
