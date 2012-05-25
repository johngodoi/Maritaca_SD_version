package br.unifesp.maritaca.web.oauth;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.smartam.leeloo.common.exception.OAuthProblemException;
import net.smartam.leeloo.common.message.types.ParameterStyle;
import net.smartam.leeloo.rs.request.OAuthAccessResourceRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.exception.MaritacaException;
import br.unifesp.maritaca.business.oauth.OAuthEJB;
import br.unifesp.maritaca.business.oauth.OAuthTokenDTO;
import br.unifesp.maritaca.business.util.ConstantsBusiness;
import br.unifesp.maritaca.web.utils.ConstantsWeb;
import br.unifesp.maritaca.web.utils.UtilsWeb;

/**
 * OAuth Filter filters requests to RESTFul resources.
 * 
 * @author alvarohenry
 */
public class OAuthFilter implements Filter {

	private static final Log log = LogFactory.getLog(OAuthFilter.class);
	
	@Inject
	private OAuthEJB oauthEJB;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Loading OAuth Filter");
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		_doFilter((HttpServletRequest)request, (HttpServletResponse)response, 
				filterChain);
	}

	protected void _doFilter(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		try {
			log.debug("Filtering " + request.getMethod() + " " + request.getRequestURL().toString());
			
			OAuthAccessResourceRequest oauthRequest = new 
					OAuthAccessResourceRequest(request, ParameterStyle.QUERY);

			String accessToken = oauthRequest.getAccessToken();

			OAuthTokenDTO oauthTokenDTO = oauthEJB.findOAuthTokenByToken(accessToken);
			if(oauthTokenDTO == null) {
				UtilsWeb.sendValuesInJson(response, 
						ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED),
						ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "Invalid oauth_token");
				
				response.setStatus(HttpURLConnection.HTTP_OK);
				return;
			}
			
			request.setAttribute(ConstantsBusiness.WS_USER_KEY, oauthTokenDTO.getUser());
			
			log.info("doFilter");
			filterChain.doFilter(request, response);
		} catch (OAuthProblemException e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_BAD_REQUEST), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, e.getDescription());
		} catch (Exception e) {
			UtilsWeb.makeResponseInJSON(response, 
					ConstantsWeb.OAUTH_ERROR, String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR), 
					ConstantsWeb.OAUTH_ERROR_DESCRIPTION, "internal server error");
			throw new MaritacaException(e.getMessage());
		}
		
	}
	
	@Override
	public void destroy() {
	}

}
