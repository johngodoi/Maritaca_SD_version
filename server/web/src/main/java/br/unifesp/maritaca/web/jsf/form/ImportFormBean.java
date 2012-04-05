package br.unifesp.maritaca.web.jsf.form;

import java.net.URI;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.UrlValidator;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.utils.Utils;


/**
 * ImportFormBean manages import from url and fileupload.
 * 
 * @author alvaro 
 */
@ManagedBean
@RequestScoped
public class ImportFormBean extends AbstractBean {

	private static final Log log = LogFactory.getLog(ImportFormBean.class);
	private static final long serialVersionUID = 1L;

	private String url;
	private boolean successful;
	@ManagedProperty("#{editFormBean}")
	private EditFormBean editFormBean;

	private String result;

	public ImportFormBean() {
		super(true, true);
	}

	// TODO Validate the xml content.
	public void listener(FileUploadEvent event) throws Exception {
		UploadedFile file = event.getUploadedFile();
		String data = new String(file.getData());
		
		setForm(file.getName(), data);
		setSuccessful(true);
		addMessage("form_import_successful", FacesMessage.SEVERITY_INFO);
	}

	// TODO Validate the xml content.
	public void processUrl() {
//		String value = Utils
//				.getMessageFromResourceProperties("form_import_successful");
		try {
			/*ClientConfigurationImpl config = new DefaultClientConfig();
			Client client = Client.create(config);
			WebResource service = client.resource(getBaseURI(getUrl()));
			String xml = service.accept(MediaType.TEXT_XML).get(String.class);

			setForm(getUrl(), xml);
			setResult(value);
			setSuccessful(true);*/
			addMessage("form_import_successful", FacesMessage.SEVERITY_INFO);
		} catch (Exception e) {
			log.error("Invalid URL", e);
		}
	}

	public void setForm(String title, String xml) {
		xml = xml.replace("\n", "");
		Form form = new Form();
		form.setTitle(title);
		form.setXml(xml);
		getEditFormBean().setForm(form);
	}

	public URI getBaseURI(String url) {
		return UriBuilder.fromUri(url).build();
	}

	public void validateUrl(FacesContext context, UIComponent toValidate,
			Object value) throws ValidatorException {
		String valueBundle = Utils
				.getMessageFromResourceProperties("form_import_urlRequired");

		String url = (String) value;
		String[] schemes = { "http", "https" };
		UrlValidator urlValidator = new UrlValidator(schemes);

		if (!urlValidator.isValid(url)) {
			setResult(valueBundle);
			setSuccessful(false);
		}
		setSuccessful(true);
	}

	public EditFormBean getEditFormBean() {
		return editFormBean;
	}

	public void setEditFormBean(EditFormBean editFormBean) {
		this.editFormBean = editFormBean;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
