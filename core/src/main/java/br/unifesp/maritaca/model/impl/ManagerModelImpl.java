package br.unifesp.maritaca.model.impl;

import java.util.Map;
import java.util.UUID;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class ManagerModelImpl implements br.unifesp.maritaca.model.ManagerModel {

	private EntityManager entityManager;

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
				entityManager.persist(cf);
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

	}

	@Override
	public void stopMaritaca() {
		if (entityManager != null) {
			entityManager.close();
		}
	}
	
	@Override
	public User getRootUser(){
		User rootUser = null;
		for (Configuration cfUser : entityManager.cQuery(
				Configuration.class, "name", CFG_ROOT)) {
			rootUser = entityManager.find(User.class,
					UUID.fromString(cfUser.getValue()));
			break;
		}
		return rootUser;
	}
}