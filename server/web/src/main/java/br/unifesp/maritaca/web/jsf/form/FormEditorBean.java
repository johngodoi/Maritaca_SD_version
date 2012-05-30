package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.persistence.permission.Operation;
import br.unifesp.maritaca.persistence.permission.Permission;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@ViewScoped
public class FormEditorBean extends MaritacaJSFBean {
	
	private static final long serialVersionUID = 1L;
	
	@Inject private FormEditorEJB formEditorEJB;

	@ManagedProperty("#{manager}")
	private Manager manager;
	
	private FormDTO formDTO;
	private String xml;
	private String saveFormAsTitle;
	
	private boolean newForm;
	private boolean editableForm;

	public FormEditorBean() {
		clean();
	}

	public void clean() {
		setFormDTO(new FormDTO());
		getFormDTO().setPermission(new Permission(Operation.READ, Operation.UPDATE, Operation.DELETE, Operation.SHARE));
		setXml("");
		setNewForm(true);
		setEditableForm(true);
	}
	
	public void setFormFromLister(FormDTO formDTO){
		setFormDTO(formEditorEJB.getFormDTOByUserDTOAndFormDTO(formDTO, getCurrentUser()));
	}
	
	public void saveForm() {
		formDTO.setUser(getCurrentUser().getKey());
		if (formDTO.getKey() == null) {
			formEditorEJB.saveNewForm(formDTO);
		} else {
			formEditorEJB.updateForm(formDTO, getCurrentUser());			
		}
		
		setNewForm(false);
		addMessageInfo("form_edit_save_success");
	}
	
	public boolean hasPermission(String operation){
		if(isNewForm()){
			return true;
		}
		// TODO
		return true;//super.formAnswCtrl.currentUserHasPermission(formDTO, Operation.fromString(operation));
	}
	
	public void saveFormAs() {
		setFormDTO(new FormDTO());
		setNewForm(true);
		setEditableForm(true);
		formDTO.setXml(getXml());
		formDTO.setTitle(getSaveFormAsTitle());
		saveForm();
	}

	public void deleteForm() {
		if (getFormDTO().getKey() != null) {
			formEditorEJB.deleteForm(getFormDTO(), getCurrentUser());
			clean();
			setNewForm(true);
			addMessageInfo("form_edit_delete_success");
			return;
		}
		addMessageError("form_edit_delete_error");
	}
	
	public void setFormAsCollectable(){
		System.out.println("set as collectable: " + formDTO.getTitle());
	}

	////// Getters and Setters //////
	public String getXml() {
		if (formDTO != null && formDTO.getXml() != null) {
			return formDTO.getXml();
		}
		return xml;
	}

	public void setXml(String xml) {
		if (getForm() != null) {
			getForm().setXml(xml);
		}
		this.xml = xml;
	}

	public FormDTO getForm() {
		return formDTO;
	}
	
	public boolean isNewForm() {
		return newForm;
	}
	
	public void setNewForm(boolean newForm) {
		this.newForm = newForm;
		if (newForm) {
			setEditableForm(newForm);
		}
	}	
		
	public boolean isEditableForm() {
		if (isNewForm()) {
			return true;
		} else if (formDTO != null && formDTO.getKey() != null && formDTO.getPermission().getUpdate()) {
			return true;//manager.isOperationEnabled(formDTO, Operation.UPDATE);
		} else {
			return editableForm;
		}
	}
	
	public void setEditableForm(boolean editableForm) {
		this.editableForm = editableForm;
	}
	
	public String getSaveFormAsTitle() {
		return saveFormAsTitle;
	}

	public void setSaveFormAsTitle(String saveFormAsTitle) {
		this.saveFormAsTitle = saveFormAsTitle;
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public FormDTO getFormDTO() {
		return formDTO;
	}

	public void setFormDTO(FormDTO formDTO) {
		this.formDTO = formDTO;
	}

	public FormEditorEJB getFormEditorEJB() {
		return formEditorEJB;
	}

	public void setFormEditorEJB(FormEditorEJB formEditorEJB) {
		this.formEditorEJB = formEditorEJB;
	}
}