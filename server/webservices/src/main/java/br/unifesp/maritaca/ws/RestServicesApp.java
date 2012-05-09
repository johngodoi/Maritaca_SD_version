package br.unifesp.maritaca.ws;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import br.unifesp.maritaca.ws.impl.AnswersServiceImpl;
import br.unifesp.maritaca.ws.impl.FormsServiceImpl;
import br.unifesp.maritaca.ws.util.MaritacaExceptionMapper;

public class RestServicesApp extends Application {
	
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	public RestServicesApp() {
		classes.add(FormsServiceImpl.class);
		classes.add(AnswersServiceImpl.class);
		classes.add(MaritacaExceptionMapper.class);
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
