package br.unifesp.maritaca.web.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class LoginFilter
 * Checks if the the user is authenticated to redirect him
 * to home.xhtml
 */
public class LoginFilter implements Filter {

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest hreq = (HttpServletRequest) request;
			HttpSession session = hreq.getSession(true);
			if (session.getAttribute("currentuser") != null) {
				((HttpServletResponse) response).sendRedirect(hreq.getContextPath() + "/faces/views/home.xhtml");
			} else {
				chain.doFilter(request, response);
			}
		} else {
			//it must be a no http-client, what to do?
			throw new RuntimeException("request not allowed");
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	@Override
	public void destroy() {
		
	}

}
