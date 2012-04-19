package br.unifesp.maritaca.model.impl;


import java.util.Map;
import java.util.UUID;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.OAuthClient;
import br.unifesp.maritaca.core.OAuthCode;
import br.unifesp.maritaca.core.OAuthToken;
import br.unifesp.maritaca.core.OpenId;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.UseEntityManager;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class ManagerModelImpl implements br.unifesp.maritaca.model.ManagerModel, UseEntityManager {

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
		if (!entityManager.columnFamilyExists(User.class)) {
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
		entityManager.createColumnFamily(Form.class);
		entityManager.createColumnFamily(Answer.class);

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

		entityManager.createColumnFamily(MaritacaListUser.class);
		entityManager.createColumnFamily(FormPermissions.class);
		entityManager.createColumnFamily(OpenId.class);
		entityManager.createColumnFamily(OAuthToken.class);
		entityManager.createColumnFamily(OAuthCode.class);
		
		//client id for mobile client
		if(!entityManager.columnFamilyExists(OAuthClient.class)){
			entityManager.createColumnFamily(OAuthClient.class);
			OAuthClient oaclient = new OAuthClient();
			oaclient.setClientId("maritacamobile");
			oaclient.setSecret("maritacasecret");
			entityManager.persist(oaclient);
		}

	}

	private void createRootMaritacaList(User rootUser) {
		MaritacaList list = new MaritacaList();
		list.setOwner(rootUser);
		list.setName(rootUser.getEmail());
		entityManager.persist(list);
		
		rootUser.setMaritacaList(list);
		entityManager.persist(rootUser);
	}

	@Override
	public void stopMaritaca() {
		if (entityManager != null) {
			entityManager.close();
		}
	}

	@Override
	public User getRootUser() {
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
	
	public EntityManager getEntityManager(){
		return this.entityManager;
	}
	
	@Override
	public void close() {
		entityManager = null;
		currentUser = null;
	}
}