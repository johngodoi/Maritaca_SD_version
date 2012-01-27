package br.unifesp.maritaca.web.jsf.form;

import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.home.CurrentUserBean;

@ManagedBean
public class ListFormsBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	private Collection<Form> forms;
	private Collection<Form> sharedForms;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUser;

	public ListFormsBean() {
		super(true, false);

	}

	public Collection<Form> getForms() {
		return forms;
	}

	public void sortByName() {
		forms = formAnswCtrl.listAllFormsSortedbyName(currentUser.getUser());
	}

	public void sortByDate() {
		forms = formAnswCtrl.listAllFormsSortedbyDate(currentUser.getUser());
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public String viewForm() {
		return "viewForm";
	}

	public CurrentUserBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(CurrentUserBean currentUser) {
		this.currentUser = currentUser;
		forms = formAnswCtrl.listAllFormsMinimalByUser(currentUser.getUser());
		sharedForms = formAnswCtrl.listAllSharedForms(currentUser.getUser(),
				true);
	}

	/**
	 * @return all forms that the user has access but is not the owner
	 */
	public Collection<Form> getSharedForms() {
		return sharedForms;
	}

	public void setSharedForms(Collection<Form> sharedForms) {
		this.sharedForms = sharedForms;
	}
}
