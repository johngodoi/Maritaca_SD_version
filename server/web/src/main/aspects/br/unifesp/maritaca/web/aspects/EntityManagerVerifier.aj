package br.unifesp.maritaca.web.aspects;

import br.unifesp.maritaca.model.UseEntityManager;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.persistence.EntityManager;

/**
 * Aspect used to check if the entity manager is up before
 * using it in the model classes.
 * 
 * @author tiagobarabasz
 *
 */
public aspect EntityManagerVerifier {
	
	/**
	 * This point cut matches all the public methods calls to classes
	 * that implement the UseEntityManager interface.<br>
	 * It excludes the getEntityManager and initMaritaca methods. The
	 * first one results in a infinite loop (as it is used in the advice),
	 * and the second one is responsible for instantiating the entity manager.
	 */	
	pointcut publicUseEntityManagerMethods(UseEntityManager useEM):
		call(public * * (..)) &&
		target(useEM) &&
		!call(public * getEntityManager (..)) &&
		!call(public * ManagerModel.initMaritaca (..));
	
	before(UseEntityManager useEM): publicUseEntityManagerMethods(useEM){		
		EntityManager entityManager  = useEM.getEntityManager();		
		if (entityManager == null)
			throw new RuntimeException("Entity manager not initialized");		
	}
}
