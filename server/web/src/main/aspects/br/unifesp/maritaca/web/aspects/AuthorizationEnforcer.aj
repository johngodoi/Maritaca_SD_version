package br.unifesp.maritaca.web.aspects;

import br.unifesp.maritaca.model.FormAnswerModel;

public aspect AuthorizationEnforcer {

	pointcut formAnswServices():
		call(public * FormAnswerModel.* (..));
	
	before(): formAnswServices(){
		System.out.println("Executando método de FormAnsw...");
	}
}
