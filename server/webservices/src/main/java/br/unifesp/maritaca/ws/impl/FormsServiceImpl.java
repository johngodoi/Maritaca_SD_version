package br.unifesp.maritaca.ws.impl;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.form.list.FormListerEJB;
import br.unifesp.maritaca.persistence.dto.UserDTO;
import br.unifesp.maritaca.ws.api.FormsService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.FormListResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;
import br.unifesp.maritaca.ws.exceptions.MaritacaWSException;
import br.unifesp.maritaca.ws.util.UtilsWS;

@Stateless
@Path("/form")
public class FormsServiceImpl implements FormsService {

	private static final Log log = LogFactory.getLog(FormsServiceImpl.class);
	
	@Inject
	private FormListerEJB formListerEJB;
	
	@Inject
	private FormEditorEJB formEditorEJB;
	
	private UserDTO userDTO;

	public FormsServiceImpl() { 
		log.info("in FormsServiceImpl");
	}

	@Override
	public FormDTO getForm(HttpServletRequest request, String formId) 
			throws MaritacaWSException {
		setUserDTO(UtilsWS.createUserDTO(request));
		UUID uuid = UUID.fromString(formId);
		FormDTO formDTO = null;
		formDTO = formEditorEJB.getFormDTOById(uuid, getUserDTO());
		if (formDTO != null)
			return formDTO;
		else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(Response.Status.NO_CONTENT.getStatusCode());
			error.setMessage("Form with Id: " + formId + " not found");
			throw new MaritacaWSException(error);
		}
	}

	@Override
	public MaritacaResponse listFormsMinimal(HttpServletRequest request) {
		setUserDTO(UtilsWS.createUserDTO(request));
		FormListResponse resp = new FormListResponse();
		resp.setList(formListerEJB.getListOwnForms(getUserDTO()));
		return resp;
	}

	@Override
	public FormDTO getFormSharing(HttpServletRequest request, String url) 
			throws MaritacaWSException {
		setUserDTO(UtilsWS.createUserDTO(request));
		FormDTO formDTO = formEditorEJB.getFormDTOFromUrl(url);
		if (formDTO != null) {
			return formDTO;
		} else {
			ErrorResponse error = new ErrorResponse();
			error.setCode(Response.Status.NO_CONTENT.getStatusCode());
			error.setMessage("Form with URL: " + url + " not found");
			throw new MaritacaWSException(error);
		}
	}

	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}
	
}
