package br.unifesp.maritaca.ws.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

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
	Form getForm(@PathParam("id") String formId) throws MaritacaWSException;
	
	/**
	 * 
	 * @param xml: a string of the XML representation of the new form
	 * @return XmlSavedResponse with the Form ID and identifier type<br>
	 * @throws MaritacaWSException 
	 * @see XmlSavedResponse
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse saveForm(@FormParam("xml")String xmlForm, @FormParam("userId")String userId) throws MaritacaWSException ;
	
	/**
	 * 
	 * @return ResultSetResponse with a  collection of the formId of all 
	 * available forms
	 * @throws Exception
	 */
	@GET
	@Path("/list")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse listFormsMinimal() ;
	
	/**
	 * 
	 * @param url for sharing
	 * @return XML or JSON
	 * @throws MaritacaWSException
	 */
	@GET
	@Path("/share/{url}")
	Form getFormSharing(@PathParam("url") String url) throws MaritacaWSException;
}
