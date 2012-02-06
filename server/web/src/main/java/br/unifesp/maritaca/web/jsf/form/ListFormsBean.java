package br.unifesp.maritaca.web.jsf.form;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.jsf.account.CurrentUserBean;

@ManagedBean
@RequestScoped
public class ListFormsBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	private Collection<Form> forms;
	private Collection<Form> sharedForms;
	private boolean updateList;

	@ManagedProperty("#{currentUserBean}")
	private CurrentUserBean currentUser;

	public ListFormsBean() {
		super(true, false);
		setUpdateList(false);
	}

	public Collection<Form> getForms() {
		if (isUpdateList()) {
			updateListOwnForms();
			setUpdateList(false);
		}
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
	}
	
	@PostConstruct
	public void updateFormsList(){
		updateListOwnForms();
		updateListSharedForms();		
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

	public void listOwnFormsChanged(ActionEvent evt) {
		setUpdateList(true);
	}

	private void updateListOwnForms() {
		setForms(formAnswCtrl.listAllFormsMinimalByUser(currentUser.getUser()));
	}

	private void updateListSharedForms() {
		setSharedForms(formAnswCtrl.listAllSharedForms(currentUser.getUser(),
				true));
	}

	public boolean isUpdateList() {
		return updateList;
	}

	public void setUpdateList(boolean updateList) {
		this.updateList = updateList;
	}
	
	/**
	 * True if current user can delete the form
	 * @param form
	 * @return
	 */
	public boolean canDelete(Form form){
		return formAnswCtrl.currentUserHasPermission(form, Operation.DELETE);
	}

}
