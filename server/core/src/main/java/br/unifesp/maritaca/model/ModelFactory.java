package br.unifesp.maritaca.model;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.impl.FormAnswerModelImpl;
import br.unifesp.maritaca.model.impl.ManagerModelImpl;
import br.unifesp.maritaca.model.impl.UserModelImpl;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.util.UserLocator;

public class ModelFactory {
	private static final Log log = LogFactory.getLog(ModelFactory.class);
	private static ModelFactory instance;
	private HashMap<UUID, Integer> sessionCounter;
	private HashMap<UUID, UserModel> userModelList;
	private HashMap<UUID, ManagerModel> managerModelList;
	private HashMap<UUID, FormAnswerModel> formAnswModelList;

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
		return createFormResponseModel(getCurrentUser());
	}
	
	public FormAnswerModel createFormResponseModel(User currentUser){
		FormAnswerModel formAnsModel = null;
		synchronized (formAnswModelList) {
			if(currentUser != null)
				formAnsModel = formAnswModelList.get(currentUser.getKey());
			if (formAnsModel == null) {

				formAnsModel = new FormAnswerModelImpl();
				formAnsModel.setCurrentUser(currentUser);
				EntityManager em = EntityManagerFactory.getInstance()
						.createEntityManager(
								EntityManagerFactory.HECTOR_MARITACA_EM);
				formAnsModel.setEntityManager(em);
				formAnsModel.setUserModel(createUserModel(currentUser));
				if(currentUser != null)
					formAnswModelList.put(currentUser.getKey(), formAnsModel);
			}
		}
		return formAnsModel;
	}

	public UserModel createUserModel(User currentUser) {
		UserModel userModel = null;
		synchronized (userModelList) {
			if(currentUser != null)
				userModel = userModelList.get(currentUser.getKey());
			
			if (userModel == null) {
				userModel = new UserModelImpl();
				userModel.setCurrentUser(currentUser);
				EntityManager em = EntityManagerFactory.getInstance()
						.createEntityManager(
								EntityManagerFactory.HECTOR_MARITACA_EM);
				userModel.setEntityManager(em);
				userModel.setManagerModel(createManagerModel(currentUser));
				if(currentUser != null)
					userModelList.put(currentUser.getKey(), userModel);
			}
		}
		return userModel;
	}

	public UserModel createUserModel() {
		return createUserModel(getCurrentUser());
	}
	
	public ManagerModel createManagerModel(User currentUser) {
		ManagerModel managerModel = null;
		synchronized (managerModelList) {
			if(currentUser != null)
				managerModel = managerModelList.get(currentUser.getKey());
			
			if (managerModel == null) {
				managerModel = new ManagerModelImpl();
				managerModel.setCurrentUser(currentUser);
				EntityManager em = EntityManagerFactory.getInstance()
						.createEntityManager(
								EntityManagerFactory.HECTOR_MARITACA_EM);
				managerModel.setEntityManager(em);
				if(currentUser != null)
					managerModelList.put(currentUser.getKey(), managerModel);
			}
		}

		return managerModel;
	}

	public ManagerModel createManagerModel() {
		return createManagerModel(getCurrentUser());
	}

	public void invalidateModelsForUser(User user) {
		if(user==null || user.getKey() == null)return;
		invalidateModelForUserKey(user.getKey());
	}
	
	public void invalidateModelForUserKey(UUID key){
		Integer counter = sessionCounter.get(key);
		if(counter == null){
			return;
		}else if(--counter == 0){
			sessionCounter.remove(key);
			UserModel usMod = userModelList.remove(key);
			FormAnswerModel faMod = formAnswModelList.remove(key);
			ManagerModel manMod = managerModelList.remove(key);
			
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
			sessionCounter.put(key, counter);
		}
		//TODO: add timeout to invalidate models for user
		
		log.debug("session invalidated for user: " + key + " counter: " + counter);
	}
	
	public void registryUser(User user){
		if(user==null || user.getKey() == null)return;
		Integer counter = sessionCounter.get(user.getKey());
		if(counter == null){
			counter = 1;
			sessionCounter.put(user.getKey(), counter);
		}else{
			sessionCounter.put(user.getKey(), ++counter);
		}
		
		log.debug("new session for user: " + user + " counter: " + counter);
	}

	private User getCurrentUser() {
		return UserLocator.getCurrentUser();
	}
}
