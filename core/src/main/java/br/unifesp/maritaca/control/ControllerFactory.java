package br.unifesp.maritaca.control;

import br.unifesp.maritaca.control.impl.FormResponseCtrlImpl;
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
	
	public FormResponseController createFormResponseCtrl(){
		FormResponseController control = new FormResponseCtrlImpl();
		EntityManager em = EntityManagerFactory.getInstance().createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
		control.setEntityManager(em);
		return control;
	}
}
