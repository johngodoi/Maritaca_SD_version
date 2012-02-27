package br.unifesp.maritaca.model.impl;

import static br.unifesp.maritaca.util.UtilsCore.verifyEM;

import java.util.Map;
import java.util.UUID;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.OAuthClient;
import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.core.OpenId;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class ManagerModelImpl implements br.unifesp.maritaca.model.ManagerModel {

	private EntityManager entityManager;
	private User currentUser;

	public ManagerModelImpl() {
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
	}

	@Override
	public void initMaritaca(Map<String, String> params) {
		if (entityManager == null) {
			EntityManagerFactory.getInstance().setHectorParams(params);
			this.entityManager = EntityManagerFactory.getInstance()
					.createEntityManager(
							EntityManagerFactory.HECTOR_MARITACA_EM);

		}
		User rootUser = null;
		if (!entityManager.tableExists(User.class)) {
			// create main user
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

		// create another tables
		entityManager.createTable(Form.class);
		entityManager.createTable(Answer.class);

		if (!entityManager.tableExists(Group.class)) {
			entityManager.createTable(Group.class);
			// create ALL_USERS group
			Group gr = new Group();
			gr.setName(ALL_USERS);
			gr.setOwner(rootUser);
			entityManager.persist(gr);
		}

		entityManager.createTable(GroupUser.class);
		entityManager.createTable(FormPermissions.class);
		entityManager.createTable(OpenId.class);
		entityManager.createTable(OAuthToken.class);
		entityManager.createTable(OAuthCode.class);
		
		//client id for mobile client
		if(!entityManager.tableExists(OAuthClient.class)){
			entityManager.createTable(OAuthClient.class);
			OAuthClient oaclient = new OAuthClient();
			oaclient.setClientId("maritacamobile");
			oaclient.setSecret("maritacasecret");
			entityManager.persist(oaclient);
		}

	}

	@Override
	public void stopMaritaca() {
		if (entityManager != null) {
			entityManager.close();
		}
	}

	@Override
	public User getRootUser() {
		verifyEM(entityManager);

		User rootUser = null;
		for (Configuration cfUser : entityManager.cQuery(Configuration.class,
				"name", CFG_ROOT)) {
			rootUser = entityManager.find(User.class,
					UUID.fromString(cfUser.getValue()));
			break;
		}
		return rootUser;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	@Override
	public void close() {
		entityManager = null;
		currentUser = null;
	}
}