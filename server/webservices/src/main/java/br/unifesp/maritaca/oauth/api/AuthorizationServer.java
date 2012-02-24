package br.unifesp.maritaca.oauth.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

public interface AuthorizationServer {

	@GET
	@Path("/requestcode")
	void requestAuthorizationCode(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException,
			MaritacaWSException, ServletException;

	@GET
	@Path("/generatecode")
	void generateAuthorizationCode(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws MaritacaWSException, IOException;

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/accesstoken")
	OAuthToken generateAccessToken(@FormParam("grant_type") String grantType,
			@FormParam("client_id") String clientId,
			@FormParam("client_secret") String clientSecret,
			@FormParam("code") String code,
			@FormParam("redirect_uri") String redirectUri)
			throws MaritacaWSException;
}
