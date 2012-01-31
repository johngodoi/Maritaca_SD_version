package br.unifesp.maritaca.access;

import java.util.ArrayList;

import br.unifesp.maritaca.access.operation.Operation;

public class PrivateAccess extends AccessLevel {
	private static PrivateAccess instance;
	
	private PrivateAccess() {
		setEnabledOperations(new ArrayList<Operation>());
		//none operation
	}
	
	public static PrivateAccess getInstance(){
		if(instance == null){
			instance = new PrivateAccess();
		}
		return instance;
	}
	
	@Override
	public String toString() {
		return AccessLevelFactory.PRIVATE;
	}

}
