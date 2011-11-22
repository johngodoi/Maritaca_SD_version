package br.unifesp.maritaca.ws.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.Response;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.api.resp.ResultSetResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	
	private JAXBContext context;
	private Class[] types = {Form.class, 
			User.class, 
			Response.class, 
			MaritacaResponse.class, 
			XmlSavedResponse.class,
			ErrorResponse.class,
			ResultSetResponse.class};

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
