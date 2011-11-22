package br.unifesp.maritaca.ws.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;

public interface FormsService {
	@GET
	@Path("/{id}")
	Form getForm(@PathParam("id") String formId) throws Exception;
	
	/**
	 * 
	 * @param xmlForm
	 * @return maritacaResponse
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse saveForm(@FormParam("xml")String xmlForm) throws Exception ;
	
	@GET
	@Path("/list")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	MaritacaResponse listFormsMinimal() throws Exception ;
}
