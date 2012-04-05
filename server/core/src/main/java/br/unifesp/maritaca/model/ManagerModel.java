package br.unifesp.maritaca.model;

import java.util.Map;

import br.unifesp.maritaca.persistence.EntityManager;

public interface ManagerModel extends GenericModel{

	void setEntityManager(EntityManager em);

	void stopMaritaca();

	void initMaritaca(Map<String, String> params);
	
	void setUserModel(UserModel userModel);

	void close();
}