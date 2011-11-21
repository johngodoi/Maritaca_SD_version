package br.unifesp.maritaca.ws.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.ws.rs.Path;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.FormResponseController;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.ws.api.FormsService;
import br.unifesp.maritaca.ws.api.resp.ErrorResponse;
import br.unifesp.maritaca.ws.api.resp.XmlSavedResponse;
import br.unifesp.maritaca.ws.api.resp.MaritacaResponse;

@Path("/forms")
public class FormsServiceImpl implements FormsService {
	
	private FormResponseController formRespCtlr;
	
	public FormsServiceImpl() {
		formRespCtlr = ControllerFactory.getInstance().createFormResponseCtrl();
	}

	public FormResponseController getFormResponse() {
		return formRespCtlr;
	}

	public void setFormResponse(FormResponseController formResponse) {
		this.formRespCtlr = formResponse;
	}

	@Override
	public Form getForm(String formId) {
		UUID uuid = UUID.fromString(formId);
		Form form=null;
		try {
			form = formRespCtlr.getForm(uuid);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return form;
	}

	@Override
	public MaritacaResponse saveForm(String xmlForm) {
		Form form = new Form();
		form.setXml(xmlForm);
		boolean result = false;
		MaritacaResponse resp;
		try {
			if(result = formRespCtlr.saveForm(form)){
				XmlSavedResponse okresp = new XmlSavedResponse();
				okresp.setId(form.getKey());
				okresp.setType(MaritacaResponse.FORM_TYPE);
				resp = okresp;
			}else{
				ErrorResponse error = new ErrorResponse();
				error.setMessage("unknown error");
				resp = error;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			resp = new ErrorResponse(e);
		}
		
		return resp;
		
	}

}
