package br.unifesp.maritaca.webservices;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/basic")
public class Basic {
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String doSomething(){
		return "<html><body><h1>basic test</h1></body><html>";
	}
}
