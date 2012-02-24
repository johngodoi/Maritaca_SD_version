package br.unifesp.maritaca.ws.oauth.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.core.HttpContext;

public interface AuthorizationServer {

	@GET
	@Path("/requestcode")
	void requestAuthorizationCode(@Context HttpServletRequest request,
								  @Context HttpServletResponse response);
	
	@GET
	@Path("/generatecode")
	void generateAuthorizationCode(@Context HttpServletRequest request,
								   @Context HttpServletResponse response);
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/accesstoken")
	void generateAccessToken(@Context HttpContext request,
			  				 @Context HttpServletResponse response);
}
