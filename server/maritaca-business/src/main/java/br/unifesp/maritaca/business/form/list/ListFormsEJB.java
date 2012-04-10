package br.unifesp.maritaca.business.form.list;

import java.util.Collection;

import javax.ejb.Stateless;

import br.unifesp.maritaca.business.base.BaseDAO;
import br.unifesp.maritaca.core.Form;

@Stateless
public class ListFormsEJB extends BaseDAO {

	public Collection<Form> updateListOwnForms() {
		return null;
	}
	
	public Collection<Form> updateListSharedForms() {
		return null;
	}
}
