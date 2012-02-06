package br.unifesp.maritaca.web.jsf.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import org.richfaces.component.UIInplaceSelect;

import br.unifesp.maritaca.access.AccessLevel;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.web.jsf.AbstractBean;

/**
 * Managedbean for the Form sharing service the bean loads the url for sharing.
 * In the future, it must load and update access levels of the form
 * 
 * @author emiguel
 * 
 */
@ManagedBean
@ViewScoped
public class ShareFormBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	private static final String ROOT_FOR_SHARING = "/ws/form/share/";
	private Form form;
	private List<FormPermissions> formPermissions;
	private boolean show;
	private Group newGroup;

	public ShareFormBean() {
		super(true, false);
		formPermissions = new ArrayList<FormPermissions>(0);
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		if (form.getUrl() == null) {
			form = formAnswCtrl.getForm(form.getKey(), true);
		}
		this.form = form;
		setShow(true);
	}

	public void setForm(String formKey) {
		if (formKey == null)
			return;
		Form form = new Form();
		form.setKey(formKey);
		setForm(form);
	}

	public String getUrl() {
		if (form == null)
			return "";
		return form.getUrl();
	}

	public void setUrl(String newUrl) {
		// formShare.setUrl(newUrl);
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public String getRootForSharing() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();

		return "http://" + request.getLocalName() + ":"
				+ request.getLocalPort() + request.getContextPath()
				+ ROOT_FOR_SHARING;
	}

	public void setRootForSharing(String root) {
	}

	public List<FormPermissions> getFormPermissions() {
		if (form != null) {
			formPermissions = formAnswCtrl.getFormPermissions(getForm());
		}
		return formPermissions;
	}

	public void setFormPermissions(List<FormPermissions> formPermissions) {
		this.formPermissions = formPermissions;
	}

	public AccessLevel[] getAccessLevels() {
		return AccessLevel.values();
	}

	public void formAccessChanged(ValueChangeEvent event) {
		AccessLevel newValue = (AccessLevel) event.getNewValue();

		if (newValue.equals(event.getOldValue().toString()))
			return;

		FormPermissions fp = formAnswCtrl
				.getFormPermissionById(getParamValue("formPerm"));
		fp.setFormAccess(newValue);
		savePermission(event, fp);
	}

	public void answAccessChanged(ValueChangeEvent event) {
		AccessLevel newValue = (AccessLevel) event.getNewValue();

		if (newValue.equals(event.getOldValue().toString()))
			return;

		FormPermissions fp = formAnswCtrl
				.getFormPermissionById(getParamValue("formPerm"));
		fp.setAnswAccess(newValue);
		savePermission(event, fp);
	}

	private void savePermission(ValueChangeEvent event, FormPermissions fp) {
		if (!formAnswCtrl.saveFormPermission(fp)) {
			UIInplaceSelect uiis = (UIInplaceSelect) event.getComponent();
			uiis.setValue(event.getOldValue());
			return;
		}
	}

	public void expDateChanged(ValueChangeEvent event) {
		Date newValue = (Date) event.getNewValue();

		if (newValue == null || newValue.before(new Date()))
			return;

		FormPermissions fp = formAnswCtrl
				.getFormPermissionById(getParamValue("formPerm"));
		fp.setExpDate(newValue.getTime());
		formAnswCtrl.saveFormPermission(fp);
	}

	private String getParamValue(String paramname) {
		FacesContext fc = FacesContext.getCurrentInstance();
		return fc.getExternalContext().getRequestParameterMap().get(paramname);
	}

	public void setCurrentPermission(FormPermissions fp) {
		// dumb
	}

	public String getCurrentPermission(FormPermissions fp) {
		return fp.toString();
	}

	public void setCurrent(FormPermissions fp) {
		// dumb
	}

	public String getNewGroup() {
		if(newGroup != null){
			return newGroup.getKey().toString();
		}
		return "";
	}

	public void setNewGroup(Group newGroup) {
		this.newGroup = newGroup;
	}
	
	public void setNewGroup(String groupId){
		Group gr = new Group();
		gr.setKey(groupId);
		setNewGroup(gr);
	}
	
	public void addGroup(){
		if(getNewGroup()!=null){
			//add group permission
			formAnswCtrl.saveDefaultFormPermissions(getForm(), newGroup);
		}else{
			return; //group not added
		}
	}
	
	public void deletePermission(FormPermissions formPerm){
		formAnswCtrl.deleteFormPermission(formPerm);
	}

}
