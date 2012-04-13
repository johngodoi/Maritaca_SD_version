package br.unifesp.maritaca.business.util;

import br.unifesp.maritaca.business.exception.ParameterException;

public class UtilsBusiness {

	public static void verifyString(String str, Class<?> parameterClass){	
		if(str==null || str.isEmpty()){
			throw new ParameterException(str, parameterClass);
		}
	}
	
	public static final void verifyObj(Object obj, Class<?> objClass){
		if(obj==null){
			throw new ParameterException(objClass);
		}
	}
}
