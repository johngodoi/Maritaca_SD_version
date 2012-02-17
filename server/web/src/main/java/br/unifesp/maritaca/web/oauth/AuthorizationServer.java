package br.unifesp.maritaca.web.oauth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.smartam.leeloo.as.issuer.MD5Generator;
import net.smartam.leeloo.as.issuer.OAuthIssuer;
import net.smartam.leeloo.as.issuer.OAuthIssuerImpl;
import net.smartam.leeloo.as.request.OAuthAuthzRequest;
import net.smartam.leeloo.as.request.OAuthTokenRequest;
import net.smartam.leeloo.as.response.OAuthASResponse;
import net.smartam.leeloo.common.OAuth;
import net.smartam.leeloo.common.message.OAuthResponse;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

public class AuthorizationServer extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	
	private UserModel userModel;
	
	public AuthorizationServer() {
		setUserModel(ModelFactory.getInstance().createUserModel());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		try {
			RequestDispatcher rd = request.getRequestDispatcher("/faces/views/login.xhtml");
			request.setAttribute("returnUrl", "localhost");
			rd.include(request, response);
			
			return;
//			
//			// dynamically recognize an OAuth profile based on request
//			// characteristic (params,
//			// method, content type etc.), perform validation
//			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
//			
//			
//			// TODO 
//			String email = request.getParameter("email");
//			String password = request.getParameter("password");
//			
//			User dbUser = getUserModel().getUser(email);
//			
//			if(dbUser==null || !dbUser.getPassword().equals(password)){
//				response.setStatus(0);
//				return;
//			} 
//
//			OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
//
//			String redirectURI = oauthRequest
//					.getParam(OAuth.OAUTH_REDIRECT_URI);
//			// build OAuth response
//			OAuthResponse resp = OAuthASResponse
//					.authorizationResponse(HttpServletResponse.SC_FOUND)
//					.setCode(oauthIssuerImpl.authorizationCode())
//					.location(redirectURI).buildQueryMessage();
//
//			String uri = resp.getLocationUri();
//
//			response.sendRedirect(uri);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OAuthTokenRequest oauthRequest = null;

		OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

		try {
			oauthRequest = new OAuthTokenRequest(request);

			String authzCode = oauthRequest.getCode();
			// some code

			String accessToken = oauthIssuerImpl.accessToken();
			String refreshToken = oauthIssuerImpl.refreshToken();

			// some code

			OAuthResponse oAuthResponse = OAuthASResponse
					.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken).setExpiresIn("3600")
					.setRefreshToken(refreshToken).buildBodyMessage();

			response.setStatus(oAuthResponse.getResponseStatus());
			PrintWriter pw = response.getWriter();
			pw.print(oAuthResponse.getBody());
			pw.flush();
			pw.close();

			// if something goes wrong
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
	
}
