package br.unifesp.maritaca.model;

import br.unifesp.maritaca.model.impl.FormAnswerModelImpl;
import br.unifesp.maritaca.model.impl.UserModelImpl;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class ModelFactory {
	private static ModelFactory instance;
	
	public static ModelFactory getInstance() {
		if(instance == null){
			instance = new ModelFactory();
		}
		return instance;
	}
	
	public FormAnswerModel createFormResponseModel(){
		FormAnswerModel control = new FormAnswerModelImpl();
		EntityManager em = EntityManagerFactory.getInstance().createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
		control.setEntityManager(em);
		return control;
	}
	
	public UserModel createUserModel(){
		UserModel control = new UserModelImpl();
		EntityManager em = EntityManagerFactory.getInstance().createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
		control.setEntityManager(em);
		return control;
	}
}
