package br.unifesp.maritaca.ws.util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;

public class UtilsWS {
	
	public static String buildApplicationUrl(HttpServletRequest request, String url){
		String contextPath = request.getContextPath();
		String serverName = request.getServerName();
		String port = String.valueOf(request.getServerPort());
		String scheme = request.getScheme();
		String queryStr = request.getQueryString();
        return scheme + "://" + serverName + ":" + port + contextPath + url + "?" + queryStr;
    }
	
	public static MaritacaWSException buildMaritacaWSException(Status status, String message){
		ErrorResponse error = new ErrorResponse();
		error.setCode(status.getStatusCode());
		error.setMessage(message);
		return new MaritacaWSException(error);
	}

}
