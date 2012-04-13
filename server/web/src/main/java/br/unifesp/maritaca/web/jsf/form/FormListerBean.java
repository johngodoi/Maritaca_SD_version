package br.unifesp.maritaca.web.jsf.form;

import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.unifesp.maritaca.business.form.list.FormListerEJB;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@ViewScoped
public class FormListerBean extends MaritacaJSFBean {

	private static final long serialVersionUID = 1L;

	@Inject private FormListerEJB formListerEJB;
		
	private Collection<FormDTO> ownForms;
	private Collection<FormDTO> sharedForms;
	
	public String getAllForms() {
		setOwnForms(formListerEJB.getListOwnForms(getCurrentUser()));
		setSharedForms(formListerEJB.getListSharedForms(getCurrentUser()));
		return "";
	}
	
	public void setAllForms(String nothing) {
	}
	
	public Collection<FormDTO> getOwnForms() {
		return ownForms;
	}

	public void setOwnForms(Collection<FormDTO> ownForms) {
		this.ownForms = ownForms;
	}

	public Collection<FormDTO> getSharedForms() {
		return sharedForms;
	}

	public void setSharedForms(Collection<FormDTO> sharedForms) {
		this.sharedForms = sharedForms;
	}

	//TODO: it's not working
	public void sortByName() {
		/*User dbUser = new User();
			dbUser.setEmail(currentUser.getUser().getEmail());
			//dbUser.setKey(currentUser.getUser().getKey());
		forms = formAnswCtrl.listAllFormsSortedbyName(dbUser);//(currentUser.getUser());*/
	}

	//TODO: it's not working
	public void sortByDate() {
		//forms = formAnswCtrl.listAllFormsSortedbyDate(currentUser.getUser());
	}
	
	public void listOwnFormsChanged(ActionEvent evt) {
		setOwnForms(formListerEJB.getListOwnForms(getCurrentUser()));
		setSharedForms(formListerEJB.getListSharedForms(getCurrentUser()));
	}
}
