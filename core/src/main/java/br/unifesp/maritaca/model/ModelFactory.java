package br.unifesp.maritaca.model;

import br.unifesp.maritaca.model.impl.FormAnswerModelImpl;
import br.unifesp.maritaca.model.impl.ManagerModelImpl;
import br.unifesp.maritaca.model.impl.UserModelImpl;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class ModelFactory {
	private static ModelFactory instance;

	private ManagerModel managerModel;//not singleton
	private UserModel userModel;//not singleton
	private FormAnswerModel formAnsModel;//not singleton

	public static ModelFactory getInstance() {
		if (instance == null) {
			instance = new ModelFactory();
		}
		return instance;
	}

	public FormAnswerModel createFormResponseModel() {
		if (formAnsModel == null) {
			formAnsModel = new FormAnswerModelImpl();
			EntityManager em = EntityManagerFactory.getInstance()
					.createEntityManager(
							EntityManagerFactory.HECTOR_MARITACA_EM);
			formAnsModel.setEntityManager(em);
			formAnsModel.setUserModel(createUserModel());
		}
		return formAnsModel;
	}	

	public UserModel createUserModel() {
		if (userModel == null) {
			userModel = new UserModelImpl();
			EntityManager em = EntityManagerFactory.getInstance()
					.createEntityManager(
							EntityManagerFactory.HECTOR_MARITACA_EM);
			userModel.setEntityManager(em);
			userModel.setManagerModel(createManagerModel());
		}
		return userModel;
	}

	public ManagerModel createManagerModel() {
		if (managerModel == null) {
			managerModel = new ManagerModelImpl();
			EntityManager em = EntityManagerFactory.getInstance()
					.createEntityManager(
							EntityManagerFactory.HECTOR_MARITACA_EM);
			managerModel.setEntityManager(em);
		}
		return managerModel;
	}
}
