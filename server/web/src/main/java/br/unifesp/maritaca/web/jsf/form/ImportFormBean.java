package br.unifesp.maritaca.web.jsf.form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

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

import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;
import br.unifesp.maritaca.web.utils.UtilsWeb;


/**
 * ImportFormBean manages import from url and fileupload.
 * 
 * @author alvaro 
 */
@ManagedBean
@RequestScoped
public class ImportFormBean extends MaritacaJSFBean {

	private static final Log log = LogFactory.getLog(ImportFormBean.class);
	private static final long serialVersionUID = 1L;

	private String url;
	private boolean successful;
	
	@ManagedProperty("#{formEditorBean}")
	private FormEditorBean formEditorBean;

	private String result;

	// TODO Validate the xml content.
	public void listener(FileUploadEvent event) throws Exception {
		UploadedFile file = event.getUploadedFile();
		String data = new String(file.getData());
		data = data.replace('\n', ' ');
		
		FormDTO formDTO = new FormDTO();
		formDTO.setTitle(file.getName());
		formDTO.setXml(data);
		formEditorBean.setFormDTO(formDTO);

		setSuccessful(true);
	}

	// TODO Validate the xml content.
	public void processUrl() {
		String value = UtilsWeb
				.getMessageFromResourceProperties("form_import_successful");
		try {
			log.info(getUrl());
			FormDTO formDTO = new FormDTO();
			formDTO.setTitle(getUrl());
			formDTO.setXml(readFileFromURL(getUrl()));
			formEditorBean.setFormDTO(formDTO);
			
			setResult(value);
			setSuccessful(true);
			addMessageInfo("form_import_successful");
		} catch (Exception e) {
			log.error("Invalid URL", e);
		}
	}

	public URI getBaseURI(String url) {
		return UriBuilder.fromUri(url).build();
	}

	public void validateUrl(FacesContext context, UIComponent toValidate,
			Object value) throws ValidatorException {
		String valueBundle = UtilsWeb
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
	
	public String readFileFromURL(String stringURL) {
		try {
			URL url = new URL(stringURL);
			BufferedReader in = new BufferedReader(new 
					InputStreamReader(url.openStream()));
			String inputLine;
			String dataFile = "";
			while( (inputLine = in.readLine()) != null)
				dataFile += inputLine;
			return dataFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	public FormEditorBean getFormEditorBean() {
		return formEditorBean;
	}

	public void setFormEditorBean(FormEditorBean formEditorBean) {
		this.formEditorBean = formEditorBean;
	}
}
