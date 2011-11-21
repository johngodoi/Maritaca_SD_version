package br.unifesp.maritaca.ws.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.unifesp.maritaca.core.Form;

public interface FormsService {
	@GET
	@Path("/{id}")
	Form getForm(@PathParam("id") String formId);
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	Response saveForm(@FormParam("xml")String xmlForm);
}
