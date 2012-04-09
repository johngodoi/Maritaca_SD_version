package br.unifesp.maritaca.oauth;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import br.unifesp.maritaca.oauth.impl.AuthorizationServerImpl;
import br.unifesp.maritaca.ws.util.MaritacaExceptionMapper;

public class OAuthServiceApp extends Application {
	
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public OAuthServiceApp() {
		singletons.add(new AuthorizationServerImpl());

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
