package br.unifesp.maritaca.model.impl;


import java.util.Map;

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
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class ManagerModelImpl implements br.unifesp.maritaca.model.ManagerModel, UseEntityManager {

	private EntityManager entityManager;
	private User currentUser;
	private UserModel userModel;

	public ManagerModelImpl() {
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.entityManager = em;
		if(userModel!=null&&userModel.getEntityManager()==null){
			userModel.setEntityManager(em);
		}
	}

	@Override
	public void initMaritaca(Map<String, String> params) {
		if (entityManager == null) {
			EntityManagerFactory.getInstance().setHectorParams(params);
			EntityManager em = EntityManagerFactory.getInstance()
					.createEntityManager(
							EntityManagerFactory.HECTOR_MARITACA_EM);
			setEntityManager(em);
		}
		User rootUser = null;
		if (!entityManager.tableExists(User.class)) {
			// create main user
			rootUser = userModel.createRootUser();
			if (entityManager.persist(rootUser)) {				
				// save id of main user in Configuration table
				Configuration cf = new Configuration();
				cf.setName(UserModel.CFG_ROOT);
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
			rootUser = userModel.getRootUser();
		}

		if (rootUser == null) {
			throw new RuntimeException("Not main user found");
		}

		// create another tables
		entityManager.createTable(Form.class);
		entityManager.createTable(Answer.class);

		if (!entityManager.tableExists(MaritacaList.class)) {
			entityManager.createTable(MaritacaList.class);						

			// create ALL_USERS list
			MaritacaList gr = new MaritacaList();
			gr.setName(UserModel.ALL_USERS);
			gr.setOwner(rootUser);
			entityManager.persist(gr);
			
			if(rootUser!=null){
				createRootMaritacaList(rootUser);
			}
		}

		entityManager.createTable(MaritacaListUser.class);
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

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
}