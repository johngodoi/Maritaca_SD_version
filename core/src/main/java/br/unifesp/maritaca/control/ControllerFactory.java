package br.unifesp.maritaca.control;

import br.unifesp.maritaca.control.impl.FormAnswerCtrlImpl;
import br.unifesp.maritaca.control.impl.UserControlImpl;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

public class ControllerFactory {
	private static ControllerFactory instance;
	
	public static ControllerFactory getInstance() {
		if(instance == null){
			instance = new ControllerFactory();
		}
		return instance;
	}
	
	public FormAnswerControl createFormResponseCtrl(){
		FormAnswerControl control = new FormAnswerCtrlImpl();
		EntityManager em = EntityManagerFactory.getInstance().createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
		control.setEntityManager(em);
		return control;
	}
	
	public UserControl createUserCtrl(){
		UserControl control = new UserControlImpl();
		EntityManager em = EntityManagerFactory.getInstance().createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
		control.setEntityManager(em);
		return control;
	}
}
