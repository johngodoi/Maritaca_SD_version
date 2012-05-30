package br.unifesp.maritaca.business.base.dao;

import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.persistence.entity.Configuration;

public class ConfigurationDAO extends BaseDAO {
	
	public Configuration getSuperUserByName() {
		List<Configuration> confs = entityManager.cQuery(Configuration.class, "name", "root");
		if(!confs.isEmpty() && confs.size() > 0)
			return confs.get(0);
		return null;
	}
	
	public Boolean isRootUser(UUID userKey) {
		Configuration configuration = this.getSuperUserByName();
		if(configuration != null && configuration.getValue().toString().equals(userKey.toString())) {
			return  true;
		}
		return false;
	}
	
	public String getValueByName(String name) {
		List<Configuration> confs = entityManager.cQuery(Configuration.class, "name", name);
		if(confs != null && !confs.isEmpty())
			return confs.get(0).getValue();
		return null;
	}
}