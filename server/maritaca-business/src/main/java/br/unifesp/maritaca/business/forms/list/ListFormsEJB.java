package br.unifesp.maritaca.business.forms.list;

import java.util.Collection;

import javax.ejb.Stateless;

import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.core.Form;

@Stateless
public class ListFormsEJB extends AbstractEJB {

	private static final long serialVersionUID = 1L;

	public Collection<Form> updateListOwnForms() {
		return null;
	}
	
	public Collection<Form> updateListSharedForms() {
		return null;
	}
}
