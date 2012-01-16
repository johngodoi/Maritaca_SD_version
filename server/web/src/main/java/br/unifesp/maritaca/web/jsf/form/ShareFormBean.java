package br.unifesp.maritaca.web.jsf.form;

import java.util.List;
import java.util.UUID;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormAccess;
import br.unifesp.maritaca.core.FormShare;
import br.unifesp.maritaca.web.jsf.AbstractBean;

/**
 * Managedbean for the Form sharing service the bean loads the url for sharing.
 * In the future, it must load and update access levels of the form
 * 
 * @author emiguel
 * 
 */
@ManagedBean
public class ShareFormBean extends AbstractBean {
	private static final long serialVersionUID = 1L;
	private static final String ROOT_FOR_SHARING = "/ws/form/share/";
	private Form form;
	private FormShare formShare;
	private List<FormAccess> userAccessList;
	private boolean show;

	public ShareFormBean() {
		super(true, false);
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
		setFormShare(formAnswCtrl.getFormShare(form));
		setShow(true);
	}

	public void setForm(String formKey) {
		if (formKey == null)
			return;
		Form form = formAnswCtrl.getForm(UUID.fromString(formKey));
		setForm(form);
	}

	public FormShare getFormShare() {
		return formShare;
	}

	public void setFormShare(FormShare formShare) {
		this.formShare = formShare;
	}

	public List<FormAccess> getUserAccessList() {
		return userAccessList;
	}

	public void setUserAccessList(List<FormAccess> userAccessList) {
		this.userAccessList = userAccessList;
	}

	public String getUrl() {
		if (formShare == null)
			return "";
		return formShare.getUrl();
	}

	public void setUrl(String newUrl) {
		formShare.setUrl(newUrl);
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

}
