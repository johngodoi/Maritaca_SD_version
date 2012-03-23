package br.unifesp.maritaca.web.aspects;


public aspect EntityManagerVerifier {

	/*
	pointcut controlledParameterMethods():
		call(public * FormAnswerModel.* (..));
	
	before(): controlledParameterMethods(){
		System.out.println("Executando método de FormAnsw...");
		MethodSignature sig = (MethodSignature)thisJoinPoint.getSignature();
		sig.getMethod().getParameterTypes()
	}
	*/
}
