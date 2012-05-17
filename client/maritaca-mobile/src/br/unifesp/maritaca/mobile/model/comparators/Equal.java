package br.unifesp.maritaca.mobile.model.comparators;

import br.unifesp.maritaca.mobile.model.Clause;

public class Equal extends Clause {

	private static final String EQUAL = "equal";

	public Equal(String value, Integer goToIndex) {
		super(value, goToIndex);
	}

	public String getType() {
		return EQUAL;
	}

	@Override
	public boolean evaluate(String value) {
		if (super.value.compareTo(value) == 0)
			return true;
		return false;
	}

	@Override
	public boolean evaluate(Integer value) {
		return evaluate(value.toString());
	}

	@Override
	public boolean evaluate(Float value) {
		return evaluate(value.toString());
	}

}