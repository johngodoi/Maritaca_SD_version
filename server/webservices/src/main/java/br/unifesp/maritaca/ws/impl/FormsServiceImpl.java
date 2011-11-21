package br.unifesp.maritaca.ws.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import br.unifesp.maritaca.control.FormResponseController;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.ws.api.FormsService;

@Path("/forms")
public class FormsServiceImpl implements FormsService {
	
	private FormResponseController formRespCtlr;

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
	public Response saveForm(String xmlForm) {
		Form form = new Form();
		form.setXml(xmlForm);
		boolean result = false;
		try {
			result = formRespCtlr.saveForm(form);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result){
			return Response.ok().build();
		}else{
			return Response.serverError().build();
		}
	}

}
