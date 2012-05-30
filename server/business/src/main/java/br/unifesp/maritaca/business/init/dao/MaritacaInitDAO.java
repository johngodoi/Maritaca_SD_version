package br.unifesp.maritaca.business.init.dao;

import java.util.Map;
import java.util.UUID;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.util.ConstantsBusiness;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.entity.Answer;
import br.unifesp.maritaca.persistence.entity.Configuration;
import br.unifesp.maritaca.persistence.entity.Form;
import br.unifesp.maritaca.persistence.entity.FormAccessibleByList;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.OAuthClient;
import br.unifesp.maritaca.persistence.entity.OAuthCode;
import br.unifesp.maritaca.persistence.entity.OAuthToken;
import br.unifesp.maritaca.persistence.entity.User;

/**
 * Pattern DAO to create, init and finish Maritaca entities
 * @author alvaro
 */
public class MaritacaInitDAO extends BaseDAO {

	private static final String ROOT = "root";
	private static final String PASSROOT = "dc76e9f0c0006e8f919e0c515c66dbba3982f785"; //sha1 for 'root'
	private static final String ROOTEMAIL = "root@maritaca.com";
	private static final String CFG_ROOT = "root";
	private static final String ALL_USERS = "all_users";
	
	private static final String MARITACAMOBILE = "maritacamobile";
	private static final String MARITACASECRET = "maritacasecret";
	
	
	public void createAllEntities(Map<String, String> params) {
		if (entityManager == null) {
			EntityManagerFactory.getInstance().setHectorParams(params);
			this.entityManager = EntityManagerFactory.getInstance()
					.createEntityManager(
							EntityManagerFactory.HECTOR_MARITACA_EM);
		}
		
		User rootUser = null;
		if (!entityManager.columnFamilyExists(User.class)) {
			// create superuser root
			rootUser = new User();
			rootUser.setFirstname(ROOT);
			rootUser.setPassword(PASSROOT);
			rootUser.setEmail(ROOTEMAIL);
			if (entityManager.persist(rootUser)) {				
				// save id of main user in Configuration table
				Configuration cf = new Configuration();
				cf.setName(CFG_ROOT);
				cf.setValue(rootUser.getKey().toString());
				if (!entityManager.persist(cf)) {
					entityManager.delete(rootUser);
					rootUser = null;
				}
			} else {
				rootUser = null;
			}
		} else {
			// get main user
			rootUser = getRootUser();
		}
		
		if (rootUser == null) {
			throw new RuntimeException("Not main user found");
		}
		
		entityManager.createColumnFamily(Form.class);
		entityManager.createColumnFamily(Answer.class);
		entityManager.createColumnFamily(FormAccessibleByList.class);
		
		if (!entityManager.columnFamilyExists(MaritacaList.class)) {
			entityManager.createColumnFamily(MaritacaList.class);						

			// create ALL_USERS list
			MaritacaList gr = new MaritacaList();
			gr.setName(ALL_USERS);
			gr.setOwner(rootUser);
			entityManager.persist(gr);
			
			if(rootUser!=null){
				createRootMaritacaList(rootUser);
			}
		}
		
		entityManager.createColumnFamily(OAuthToken.class);
		entityManager.createColumnFamily(OAuthCode.class);
		
		//client id for mobile client
		if(!entityManager.columnFamilyExists(OAuthClient.class)){
			entityManager.createColumnFamily(OAuthClient.class);
			OAuthClient oaclient = new OAuthClient();
			oaclient.setClientId(MARITACAMOBILE);
			oaclient.setSecret(MARITACASECRET);
			entityManager.persist(oaclient);
		}
		
		//
		this.createMobileConstants();
	}
	
	public void destroyEntityManager(){
		EntityManagerFactory.getInstance().closeEntityManager(
				EntityManagerFactory.HECTOR_MARITACA_EM);
	}
	
	private User getRootUser() {
		User rootUser = null;
		for (Configuration cfUser : entityManager.cQuery(Configuration.class,
				"name", CFG_ROOT)) {
			rootUser = entityManager.find(User.class,
					UUID.fromString(cfUser.getValue()));
			break;
		}
		return rootUser;
	}
	
	private void createRootMaritacaList(User rootUser) {
		MaritacaList list = new MaritacaList();
		list.setOwner(rootUser);
		list.setName(rootUser.getEmail());
		entityManager.persist(list);
		
		rootUser.setMaritacaList(list.getKey());
		entityManager.persist(rootUser);
	}
	
	private void createMobileConstants() {
		Configuration cfScriptLocation = new Configuration();
		cfScriptLocation.setName(ConstantsBusiness.MOB_SCRIPT_LOCATION);
		cfScriptLocation.setValue("/home/maritaca/test-mobile/maritaca.sh");
		entityManager.persist(cfScriptLocation);
		//
		Configuration cfMaritacaPath = new Configuration();
		cfMaritacaPath.setName(ConstantsBusiness.MOB_MARITACA_PATH);
		cfMaritacaPath.setValue("/home/maritaca/test-mobile/");
		entityManager.persist(cfMaritacaPath);
		//
		Configuration cfProjectsPath = new Configuration();
		cfProjectsPath.setName(ConstantsBusiness.MOB_PROJECTS_PATH);
		cfProjectsPath.setValue("/home/maritaca/test-mobile/apps/");
		entityManager.persist(cfProjectsPath);
	}	
}
