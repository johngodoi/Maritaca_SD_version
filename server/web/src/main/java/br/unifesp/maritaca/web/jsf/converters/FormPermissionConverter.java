package br.unifesp.maritaca.web.jsf.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.unifesp.maritaca.core.FormPermissions;

@FacesConverter("br.unifesp.maritaca.web.jsf.converters.FormPermissionConverter")
public class FormPermissionConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		FormPermissions fp = new FormPermissions();
		fp.setKey(arg2);
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2.toString();
	}

}
