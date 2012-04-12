package br.unifesp.maritaca.web.jsf.form;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.form.edit.dao.NewFormDTO;
import br.unifesp.maritaca.business.form.edit.dto.FormDTO;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@RequestScoped
public class FormEditorBean extends MaritacaJSFBean {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(FormEditorBean.class);
	
	@Inject private FormEditorEJB formEditorEJB;

	@ManagedProperty("#{manager}")
	private Manager manager;
	
	private FormDTO formDTO;
	private String xml;
	private String saveFormAsTitle;
	
	private boolean newForm;
	private boolean editableForm;

	public FormEditorBean() {
		log.info("create FormEditorBean");
		clean();
	}

	public void clean() {
		setFormDTO(new FormDTO());
		setXml("");
		setNewForm(true);
		setEditableForm(true);
	}
	
	public void saveForm() {
		NewFormDTO newFormDTO = new NewFormDTO();
		newFormDTO.setUserKey(getCurrentUser().getKey());
		newFormDTO.setTitle(formDTO.getTitle());
		newFormDTO.setXml(formDTO.getXml());
		
		formEditorEJB.saveForm(newFormDTO);
		
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

	public String deleteForm() {
		if (getForm().getKey() != null) {
			// TODO
//			formAnswCtrl.deleteForm(getForm());
			clean();
		}
		return null;
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
	
	public void setForm(FormDTO formDTO) {
		if (formDTO == null)
			return;
		this.formDTO = formDTO;
		// if form has key, it is not a new form
		if (this.formDTO.getKey() == null) {
			setNewForm(true);
		} else {
			// TODO
//			if (this.formDTO.getXml() == null) {
//				this.formDTO = formAnswCtrl.getForm(formDTO.getKey(), false);
//			}
			xml = this.formDTO.getXml();
		}
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
		} else if (formDTO != null && formDTO.getKey() != null) {
			// TODO
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
