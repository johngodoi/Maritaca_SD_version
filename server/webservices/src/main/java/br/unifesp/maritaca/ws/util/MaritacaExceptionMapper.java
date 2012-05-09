package br.unifesp.maritaca.ws.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

/**
 * Class to return a customized response for Restful
 * @author emigueltriana
 *
 */
@Provider
public class MaritacaExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Log log = LogFactory.getLog(MaritacaExceptionMapper.class);
	
	@Override
	public Response toResponse(Throwable ex) {
		if(ex instanceof MaritacaWSException){
			return translateMaritacaResponse((MaritacaWSException) ex);
		}else if(ex instanceof WebApplicationException){
			return getWebApplicationResponse((WebApplicationException) ex);
		}
		else{
			return genericResponse(ex);
		}
	}

	private Response genericResponse(Throwable ex) {
		ErrorResponse error = new ErrorResponse();
		error.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
		error.setMessage(ex.getMessage());
		log.error(ex.getMessage());
		return Response.serverError().entity(error).build();
	}

	private Response getWebApplicationResponse(WebApplicationException ex) {
		Response r = ex.getResponse();
		if(r==null){
			return genericResponse(ex);
		}else{
			ErrorResponse error = new ErrorResponse();
			error.setCode(r.getStatus());
			error.setStatus(MaritacaResponse.FAIL);
			error.setMessage(ex.getMessage());
			log.error(ex.getMessage());
			return Response.serverError().entity(error).build();
		}
		
	}

	private Response translateMaritacaResponse(MaritacaWSException ex) {
		log.error(ex.getMessage());
		return Response.serverError().entity(ex.getResponse()).build();
	}	
}
