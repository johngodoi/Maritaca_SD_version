package br.unifesp.maritaca.web.jsf.form;

import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.unifesp.maritaca.business.manage_forms.ListFormsEJB;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@ViewScoped
public class ListFormsBean extends MaritacaJSFBean {
	
	@Inject private ListFormsEJB listFormsEJB;
		
	private Collection<Form> forms;
	private Collection<Form> sharedForms;
	private boolean updateList;

	//@ManagedProperty("#{currentUserBean}")
	//private CurrentUserBean currentUser;

	public ListFormsBean() {
		//setUpdateList(false);		
	}
	
	public void showForms() {
		System.out.println("showForms");
		showListOwnForms();
		showListSharedForms();
	}
	
	private void showListSharedForms() {
		setSharedForms(listFormsEJB.updateListOwnForms());
	}

	private void showListOwnForms() {
		setForms(listFormsEJB.updateListOwnForms());		
	}

	///

	public Collection<Form> getForms() {
		if (isUpdateList()) {
			updateListOwnForms();
			setUpdateList(false);
		}
		return forms;
	}
	
	public boolean hasPermission(Form form, String operation) {
		return true;//super.formAnswCtrl.currentUserHasPermission(form, Operation.fromString(operation));
	}

	public void sortByName() {
		/*User dbUser = new User();
			dbUser.setEmail(currentUser.getUser().getEmail());
			//dbUser.setKey(currentUser.getUser().getKey());
		forms = formAnswCtrl.listAllFormsSortedbyName(dbUser);//(currentUser.getUser());*/
	}

	public void sortByDate() {
		//forms = formAnswCtrl.listAllFormsSortedbyDate(currentUser.getUser());
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public String viewForm() {
		return "viewForm";
	}

	/*public CurrentUserBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(CurrentUserBean currentUser) {
		this.currentUser = currentUser;
	}*/
	
	public String getUpdateFormsList(){
		updateListOwnForms();
		updateListSharedForms();	
		updateFormsOwners();
		System.out.println("currentUserX: " + getCurrentUser());
		return "";
	}
	
	public void setUpdateFormsList(String s){};

	//TODO Temporary method. It fills the user emails in the forms list.
	private void updateFormsOwners() {
		/*for(Form f : getForms()){
			User owner = super.userCtrl.getUser(f.getUser().getKey());
			f.setUser(owner);
		}
		for(Form f : getSharedForms()){
			User owner = super.userCtrl.getUser(f.getUser().getKey());
			f.setUser(owner);
		}*/
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
		setForms(listFormsEJB.updateListOwnForms());
	}

	private void updateListSharedForms() {
		setSharedForms(listFormsEJB.updateListOwnForms());
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
		return true;//formAnswCtrl.currentUserHasPermission(form, Operation.DELETE);
	}
}
