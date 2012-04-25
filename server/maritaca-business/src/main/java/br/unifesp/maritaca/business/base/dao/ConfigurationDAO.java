package br.unifesp.maritaca.business.base.dao;

import java.util.UUID;

import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaList;

public class ConfigurationDAO extends BaseDAO {
	
	public Configuration getSuperUserByName() {
		return entityManager.cQuery(Configuration.class, "name", "root").get(0);
	}
	
	public Boolean isRootUser(UUID userKey) {
		Configuration configuration = this.getSuperUserByName();
		if(configuration != null && configuration.getValue().toString().equals(userKey.toString())) {
			return  true;
		}
		return false;
	}
}