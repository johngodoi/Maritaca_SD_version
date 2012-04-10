package br.unifesp.maritaca.business.manage_forms;

import java.util.Collection;

import javax.ejb.Stateless;

import br.unifesp.maritaca.business.base.AbstractBusinessBean;
import br.unifesp.maritaca.core.Form;

@Stateless
public class ListFormsEJB extends AbstractBusinessBean {

	private static final long serialVersionUID = 1L;

	public ListFormsEJB() {
		super(true, true);		
	}
	
	public Collection<Form> updateListOwnForms() {
		return formAnswCtrl.listFormsFromCurrentUser(true);
	}
	
	public Collection<Form> updateListSharedForms() {
		return formAnswCtrl.listSharedFormsFromCurrentUser(true);
	}
}
