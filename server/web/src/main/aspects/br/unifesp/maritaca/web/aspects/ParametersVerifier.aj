package br.unifesp.maritaca.web.aspects;


import br.unifesp.maritaca.business.annotations.VerifyObject;
import br.unifesp.maritaca.business.exception.ParameterException;
import br.unifesp.maritaca.model.UseEntityManager;
import br.unifesp.maritaca.persistence.EntityManager;

/**
 * Aspect used to check if the entity manager is up before
 * using it in the model classes.
 * 
 * @author tiagobarabasz
 *
 */
public aspect ParametersVerifier {
			
	/**
	 * 
	 */	
	pointcut verifiedArgumentMethod(Object obj):
		call(public * *(.., @VerifyObject (*),..)) &&
		args(@VerifyObject obj);
	
	before(Object obj): verifiedArgumentMethod(obj){	
		if(obj == null){
			throw new ParameterException(thisJoinPoint.getSignature().toString());
		}
	}
	
	pointcut publicUseEntityManagerMethods(UseEntityManager useEM):
		call(public * * (..)) &&
		target(useEM) &&
		!call(public * getEntityManager (..)) &&
		!call(public * ManagerModel.initMaritaca (..));
	
	before(UseEntityManager useEM): publicUseEntityManagerMethods(useEM){
		System.out.println("Aspect triggered!");
		EntityManager entityManager  = useEM.getEntityManager();		
		if (entityManager == null)
			throw new RuntimeException("Entity manager not initialized");		
	}
}
