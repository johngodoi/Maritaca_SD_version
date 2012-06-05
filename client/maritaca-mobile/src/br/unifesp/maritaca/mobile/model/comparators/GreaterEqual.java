package br.unifesp.maritaca.mobile.model.comparators;

import java.util.Date;

import br.unifesp.maritaca.mobile.model.Clause;

public class GreaterEqual extends Clause {

	public GreaterEqual(String value, Integer goToIndex) {
		super(value, goToIndex);
	}

	public Integer getGoToIndex() {
		return goToIndex;
	}

	public String getType() {
		return "greaterequal";
	}

	@Override
	public boolean evaluate(String value) {
		if (super.value.compareTo(value) > 0)
			return true;
		return false;
	}

	@Override
	public boolean evaluate(Integer value) {
		if (value >= Integer.parseInt(super.value))
			return true;
		return false;
	}

	@Override
	public boolean evaluate(Float value) {
		if (value >= Float.parseFloat(super.value))
			return true;
		return false;
	}

	@Override
	public boolean evaluate(Date value) {
		return true;
	}
}