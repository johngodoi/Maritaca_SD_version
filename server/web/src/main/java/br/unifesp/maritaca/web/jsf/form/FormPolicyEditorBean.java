package br.unifesp.maritaca.web.jsf.form;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.web.jsf.util.ItemListBean;
import br.unifesp.maritaca.web.utils.Utils;

/**
 * Managed bean for the Form sharing service the bean loads the url for sharing.
 * In the future, it must load and update access levels of the form
 * 
 * @author emiguel, tiagobarabasz, jimvalsan
 * 
 */
@ManagedBean
@ViewScoped
public class FormPolicyEditorBean extends ItemListBean {
	
	private static final long serialVersionUID = 1L;	
	private static final String ROOT_FOR_SHARING = "/ws/form/share/";
	
	@Inject FormEditorEJB formEditorEJB;
	
	private FormDTO formDTO;
	
	public String getRootForSharing() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest(); 
		return request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getLocalPort() + request.getContextPath()
				+ ROOT_FOR_SHARING;
	}
	
	public void setRootForSharing(String root) {
	}
	
	@Override
	protected boolean saveObject(Object formObj) {
		FormDTO form = (FormDTO) formObj;
		if(!formEditorEJB.updateFormFromPolicyEditor(form, getCurrentUser(), getUsedItens())) {
			return false;
		}				
		return true;
	}

	@Override
	protected Object createObjectFromItem() {		
		return formDTO;
	}
	
	public String cancel(){
		return MaritacaConstants.FACES_HOME;
	}
	
	private FormDTO updateFormDTO(FormDTO formDTO) {
		return formEditorEJB.getFormDTOByUserDTOAndFormDTO(formDTO, getCurrentUser());
	}
	
	private void populateFormSharedList(FormDTO formDTO) {
		super.getUsedItens().clear();
		super.getUsedItens().addAll(formEditorEJB.populateFormSharedList(formDTO));
	}

	@Override
	protected String searchItemInDatabase(String selectedItem) {
		return formEditorEJB.searchMaritacaListByName(getSelectedItem());
	}

	@Override
	public List<String> autoComplete(String prefix) {
		return formEditorEJB.getOwnerOfMaritacaListByPrefix(prefix);
	}

	@Override
	protected boolean newItem(Object form) {
		//Forms being shared are already saved in the database
		return false;
	}
	
	@Override
	public void setRemoveItem(String item){
		clearAddItemError();		
		String currentUsrEmail = getCurrentUser().getEmail();		
		if(item.equals(currentUsrEmail)) {
			setAddItemError(Utils.getMessageFromResourceProperties("list_remove_owner_error"));
			return;
		}
		getUsedItens().remove(item);
	}
	
	/*** Getters y Setters ***/
	public FormDTO getFormDTO() {
		return formDTO;
	}

	public void setFormDTO(FormDTO formDTO) {
		//Creating a copy of the form in order to prevent temporary modifications
		this.populateFormSharedList(formDTO);
		this.formDTO = updateFormDTO(formDTO);
	}
}