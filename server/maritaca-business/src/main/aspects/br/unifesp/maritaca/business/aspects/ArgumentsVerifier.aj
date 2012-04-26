package br.unifesp.maritaca.business.aspects;

import javax.ejb.Stateless;

import br.unifesp.maritaca.business.exception.ParameterException;

/**
 * Aspect used to check if one of the arguments passed to public
 * methods of EJB classes are null.
 * 
 * @author tiagobarabasz
 *
 */
public aspect ArgumentsVerifier {
			
	/**
	 * This pointcut matches all calls to public method of classes
	 * annoted with @Stateless, wich in our project correspond to
	 * the EJB classes. It also exposes all the arguments passed in
	 * the function call.
	 */	
	pointcut verifiedArgumentMethod(Object obj):
		within(@Stateless *) &&
		execution(public * *(..))&&		
		args(obj);
	
	before(Object obj): verifiedArgumentMethod(obj){		
		if(obj == null){
			throw new ParameterException(thisJoinPoint.getSignature().toString());
		}		
	}
}
