package br.unifesp.maritaca.web.jsf.form;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.unifesp.maritaca.business.base.MaritacaConstants;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

/**
 * Managed bean for the Form sharing service the bean loads the url for sharing.
 * In the future, it must load and update access levels of the form
 * 
 * @author emiguel, tiagobarabasz, jimvalsan
 * 
 */
@ManagedBean
@ViewScoped
public class FormPolicyEditorBean extends MaritacaJSFBean {
	
	private static final long   serialVersionUID = 1L;	
	private static final String ROOT_FOR_SHARING = "/ws/form/share/";
	
	@Inject
	private FormEditorEJB    formEditorEJB;	
	private FormDTO          formDTO;	
	private MaritacaListItem listItem;
	
	@PostConstruct
	public void setUp(){
		listItem = new MaritacaListItem();
		listItem.setFormEditorEJB(formEditorEJB);
	}
	
	public String getRootForSharing() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest(); 
		return request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getLocalPort() + request.getContextPath()
				+ ROOT_FOR_SHARING;
	}
	
	public void setRootForSharing(String root) {
	}
	
	public String save(){
		if(!formEditorEJB.updateFormFromPolicyEditor(getFormDTO(), getCurrentUser(), getListItem().getUsedItens())) {
			
		}		
		return null;
	}
	
	public String cancel(){
		return MaritacaConstants.FACES_HOME;
	}
	
	private FormDTO updateFormDTO(FormDTO formDTO) {
		return formEditorEJB.getFormDTOByUserDTOAndFormDTO(formDTO, getCurrentUser());
	}
	
	private void populateFormSharedList(FormDTO formDTO) {
		getListItem().getUsedItens().clear();
		getListItem().getUsedItens().addAll(formEditorEJB.populateFormSharedList(formDTO));
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

	public MaritacaListItem getListItem() {
		return listItem;
	}

	public void setListItem(MaritacaListItem listItem) {
		this.listItem = listItem;
	}
}
