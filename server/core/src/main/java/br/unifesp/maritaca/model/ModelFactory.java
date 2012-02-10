package br.unifesp.maritaca.model;

import java.util.HashMap;
import java.util.UUID;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.impl.FormAnswerModelImpl;
import br.unifesp.maritaca.model.impl.ManagerModelImpl;
import br.unifesp.maritaca.model.impl.UserModelImpl;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.util.UserLocator;

public class ModelFactory {
	private static ModelFactory instance;
	private HashMap<UUID, Integer> sessionCounter;
	private HashMap<UUID, UserModel> userModelList;
	private HashMap<UUID, ManagerModel> managerModelList;
	private HashMap<UUID, FormAnswerModel> formAnswModelList;
	private UUID uidForNull;// uuid for user null, temporary, while restful
							// services has authentication

	public static ModelFactory getInstance() {
		if (instance == null) {
			instance = new ModelFactory();
		}
		return instance;
	}

	private ModelFactory() {
		sessionCounter = new HashMap<UUID, Integer>();
		userModelList = new HashMap<UUID, UserModel>();
		managerModelList = new HashMap<UUID, ManagerModel>();
		formAnswModelList = new HashMap<UUID, FormAnswerModel>();
	}

	public FormAnswerModel createFormResponseModel() {
		User currUser = getCurrentUser();

		FormAnswerModel formAnsModel = null;
		synchronized (formAnswModelList) {
			formAnsModel = formAnswModelList.get(currUser.getKey());
			if (formAnsModel == null) {

				formAnsModel = new FormAnswerModelImpl();
				EntityManager em = EntityManagerFactory.getInstance()
						.createEntityManager(
								EntityManagerFactory.HECTOR_MARITACA_EM);
				formAnsModel.setEntityManager(em);
				formAnsModel.setUserModel(createUserModel());
				formAnswModelList.put(currUser.getKey(), formAnsModel);
			}
		}
		return formAnsModel;
	}

	public UserModel createUserModel() {
		User currUser = getCurrentUser();

		UserModel userModel = null;
		synchronized (userModelList) {
			userModel = userModelList.get(currUser.getKey());
			if (userModel == null) {
				userModel = new UserModelImpl();
				EntityManager em = EntityManagerFactory.getInstance()
						.createEntityManager(
								EntityManagerFactory.HECTOR_MARITACA_EM);
				userModel.setEntityManager(em);
				userModel.setManagerModel(createManagerModel());
				userModelList.put(currUser.getKey(), userModel);
			}
		}
		return userModel;
	}

	public ManagerModel createManagerModel() {
		User currUser = getCurrentUser();

		ManagerModel managerModel = null;
		synchronized (managerModelList) {
			managerModel = managerModelList.get(currUser.getKey());
			if (managerModel == null) {
				managerModel = new ManagerModelImpl();
				EntityManager em = EntityManagerFactory.getInstance()
						.createEntityManager(
								EntityManagerFactory.HECTOR_MARITACA_EM);
				managerModel.setEntityManager(em);
				managerModelList.put(currUser.getKey(), managerModel);
			}
		}

		return managerModel;
	}

	public void invalidateModelsForUser(User user) {
		if(user==null || user.getKey() == null)return;
		Integer counter = sessionCounter.get(user.getKey());
		if(counter == null){
			return;
		}else if(--counter == 0){
			sessionCounter.remove(user.getKey());
			UserModel usMod = userModelList.remove(user.getKey());
			FormAnswerModel faMod = formAnswModelList.remove(user.getKey());
			ManagerModel manMod = managerModelList.remove(user.getKey());
			
			if(usMod!=null){
				usMod.close();
			}
			
			if(faMod!=null){
				faMod.close();
			}

			if(manMod!=null){
				manMod.close();
			}
		}else{
			sessionCounter.put(user.getKey(), counter);
		}
	}
	
	public void registryUser(User user){
		if(user==null || user.getKey() == null)return;
		Integer counter = sessionCounter.get(user.getKey());
		if(counter == null){
			sessionCounter.put(user.getKey(), 1);
		}else{
			sessionCounter.put(user.getKey(), ++counter);
		}
	}

	private User getCurrentUser() {
		User currUser = UserLocator.getCurrentUser();
		if (currUser == null) {
			currUser = new User();
			currUser.setKey(uidForNull);// SET FOR NULL USER, MUST BE CHANGED
		}
		return currUser;
	}
}
