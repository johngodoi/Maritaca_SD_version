package br.unifesp.maritaca.access;

import java.util.ArrayList;

import br.unifesp.maritaca.access.operation.Delete;
import br.unifesp.maritaca.access.operation.Edit;
import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.access.operation.Read;

public class ReadWrite extends AccessLevel {

	private static ReadWrite instance;

	private ReadWrite() {
		ArrayList<Operation> list = new ArrayList<Operation>();
		list.add(Read.getInstance());
		list.add(Edit.getInstance());
		list.add(Delete.getInstance());
		setEnabledOperations(list);
	}

	public static ReadWrite getInstance() {
		if (instance == null) {
			instance = new ReadWrite();
		}
		return instance;
	}

	@Override
	public String toString() {
		return AccessLevelFactory.READWRITE;
	}

}
