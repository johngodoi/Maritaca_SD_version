package br.unifesp.maritaca.web.jsf.form;

import java.net.URI;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.validator.UrlValidator;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.jsf.AbstractBean;
import br.unifesp.maritaca.web.utils.Utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * ImportFormBean manages import from url and fileupload.
 * 
 * @author alvaro 
 */
@ManagedBean
@RequestScoped
public class ImportFormBean extends AbstractBean {

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
	}

	// TODO Validate the xml content.
	public void processUrl() {
		String value = Utils
				.getMessageFromResourceProperties("form_import_successful");
		try {
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			WebResource service = client.resource(getBaseURI(getUrl()));
			String xml = service.accept(MediaType.TEXT_XML).get(String.class);

			setForm(getUrl(), xml);
			setResult(value);
			setSuccessful(true);
		} catch (Exception e) {
		}
	}

	public void setForm(String title, String xml) {
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