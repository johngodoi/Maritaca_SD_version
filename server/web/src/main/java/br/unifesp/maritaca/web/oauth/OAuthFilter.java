package br.unifesp.maritaca.web.oauth;

import java.io.IOException;
import java.io.PrintWriter;
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

import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.exception.MaritacaException;
import br.unifesp.maritaca.business.oauth.OAuthEJB;
import br.unifesp.maritaca.business.oauth.OAuthTokenDTO;

/**
 * OAuth Filter that filter requests to resources.
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
				PrintWriter printWriter;
				printWriter = response.getWriter();
				
				String[] params = {"error", String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED),
									"error_description", "Invalid oauth_token"};
				
				printWriter.append('{');
				for (int i = 0; i < params.length; i+=2) {
					if (i > 0) {
						printWriter.append(',');
					}
					printWriter.append('"');
					printWriter.append(params[i]);
					printWriter.append('"');
					printWriter.append(':');
					printWriter.append('"');
					printWriter.append(params[i+1]);
					printWriter.append('"');
				}
				printWriter.append('}');
				
				response.setStatus(HttpURLConnection.HTTP_OK);
				return;
			}
			
			request.setAttribute(MaritacaConstants.WS_USER_KEY, oauthTokenDTO.getUser());
			
			log.info("doFilter");
			filterChain.doFilter(request, response);
		} catch (OAuthProblemException e) {
			// TODO
			throw new MaritacaException(e.getMessage());
		} catch (Exception e) {
			throw new MaritacaException(e.getMessage());
		}
		
	}
	
	@Override
	public void destroy() {
	}

}
