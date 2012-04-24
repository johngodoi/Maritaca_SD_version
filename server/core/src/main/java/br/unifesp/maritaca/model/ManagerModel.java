package br.unifesp.maritaca.model;

import java.util.Map;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;

@Deprecated
public interface ManagerModel extends GenericModel{
	String ROOT = "root";
	String PASSROOT = "dc76e9f0c0006e8f919e0c515c66dbba3982f785"; //sha1 for 'root'
	String ROOTEMAIL = "root@maritaca.com";
	String CFG_ROOT = "root";
	String ALL_USERS = "all_users";

	void setEntityManager(EntityManager em);

	void stopMaritaca();

	void initMaritaca(Map<String, String> params);

	User getRootUser();

	void close();
}