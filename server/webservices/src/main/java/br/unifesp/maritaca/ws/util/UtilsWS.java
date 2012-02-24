package br.unifesp.maritaca.ws.util;

import javax.servlet.http.HttpServletRequest;

public class UtilsWS {
	
	public static String buildApplicationUrl(HttpServletRequest request, String url){
		String contextPath = request.getContextPath();
		String serverName = request.getServerName();
		String port = String.valueOf(request.getServerPort());
		String scheme = request.getScheme();
		String queryStr = request.getQueryString();
        return scheme + "://" + serverName + ":" + port + contextPath + url + "?" + queryStr;
    }

}
