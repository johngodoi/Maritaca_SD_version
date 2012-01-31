package br.unifesp.maritaca.web.jsf.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.unifesp.maritaca.core.Form;

@FacesConverter("br.unifesp.maritaca.web.jsf.converters.FormConverter")
public class FormConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		try {
			Form form = new Form();
			form.setKey(arg2);
			return form;
			
		} catch (Exception e) {
			System.out.println("Not possivel to get UUID from " + arg2);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2.toString();
	}

}
