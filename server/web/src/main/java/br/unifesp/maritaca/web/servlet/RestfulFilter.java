package br.unifesp.maritaca.web.servlet;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

/*import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;*/

public class RestfulFilter {/*implements ContainerRequestFilter,
		ContainerResponseFilter {
	private static final Log log = LogFactory.getLog(RestfulFilter.class);
	private UserModel userModel;

	@Override
	public ContainerRequest filter(ContainerRequest req) {
		if (userModel == null) {
			userModel = ModelFactory.getInstance().createUserModel();
		}
		log.debug("in restful filter");
		String accessToken = req.getQueryParameters().getFirst("access_token");
		if (accessToken == null) {
			throw new IllegalArgumentException("login required");
		}
		User user = userModel.getUserByToken(accessToken);
		if (user == null) {
			throw new IllegalArgumentException("invalid token");
		}

		req.getRequestHeaders().add("curruserkey", user.getKey().toString());
		return req;
	}

	@Override
	public ContainerResponse filter(ContainerRequest arg0,
			ContainerResponse arg1) {
		List<String> userkey = arg0.getRequestHeaders().get("curruserkey");
		log.debug("exit: " + arg0.getRequestHeaders().get("curruserkey"));
		if (userkey != null) {
			ModelFactory.getInstance().invalidateModelForUserKey(
					UUID.fromString(userkey.get(0)));
		}
		return arg1;
	}*/

}
