package br.unifesp.maritaca.ws.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import br.unifesp.maritaca.persistence.entity.Answer;
import br.unifesp.maritaca.persistence.entity.Form;
import br.unifesp.maritaca.persistence.entity.OAuthToken;
import br.unifesp.maritaca.persistence.entity.User;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;

/**
 * Class to generate the XML/JSON correctly for
 * the responses in restful
 * 
 * @author emigueltriana
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	
	private JAXBContext context;
	// supported classes in this resolver
	@SuppressWarnings("rawtypes")
	private Class[] types = {Form.class, 
			User.class, 
			Answer.class, 
			MaritacaResponse.class, 
			XmlSavedResponse.class,
			ErrorResponse.class,
			OAuthToken.class};

	public JAXBContextResolver() {
		try {
			context = JAXBContext.newInstance(types);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	@Override
	public JAXBContext getContext(Class<?> type) {
		return context;
	}

}
