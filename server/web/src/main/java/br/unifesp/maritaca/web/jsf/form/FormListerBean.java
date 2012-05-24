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
	private Integer numberOfPagesOwnList;
	private Integer numberOfPagesSharedList;
	private Integer numberOfPages;
		
	private Collection<FormDTO> ownForms;
	private Collection<FormDTO> sharedForms;
	
	public String getAllForms() {
		loadLists();
		return "";
	}
	
	public void setAllForms(String nothing) {
	}
        
    public void listOwnFormsChanged(ActionEvent evt) {
    	loadLists();
	}
    
    private void loadLists() {
    	setOwnForms(formListerEJB.getListOwnForms(getCurrentUser()));
		setSharedForms(formListerEJB.getListSharedForms(getCurrentUser()));
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
        
    /*** Setters y Getters ***/
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

	public Integer getNumberOfPagesOwnList() {
		Integer numPages = ownForms!=null?ownForms.size():0;
		this.numberOfPages= super.getNumberOfPages(numPages, getItemsPerPage());
		return this.numberOfPages;
	}

	public void setNumberOfPagesOwnList(Integer numberOfPagesOwnList) {
		this.numberOfPagesOwnList = numberOfPagesOwnList;
	}

	public Integer getNumberOfPagesSharedList() {
		Integer numPages = sharedForms!=null?ownForms.size():0;
		return super.getNumberOfPages(numPages, super.getItemsPerPage());
	}

	public void setNumberOfPagesSharedList(Integer numberOfPagesSharedList) {
		this.numberOfPagesSharedList = numberOfPagesSharedList;
	}
	
	public int[] getPagesToScroll(){
		int vector[] = new int[this.numberOfPages];
		for(Integer i =1; i<=this.numberOfPages; i++)
			vector[i-1]=i;
		return vector;
	}
}