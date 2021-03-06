package br.unifesp.maritaca.business.init;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;


/**
 * Enterprise Java Bean to init Maritaca entities
 * @author alvaro
 */
@Stateless
public class MaritacaInitEJB {
	
	@Inject
	private MaritacaInitDAO maritacaInitDAO;
	
	public void initMaritaca(Map<String, String> params) {
		maritacaInitDAO.createAllEntities(params);
	}
	
	public void terminateMaritaca(){
		maritacaInitDAO.destroyEntityManager();
	}

}
