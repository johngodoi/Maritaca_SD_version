package br.unifesp.maritaca.web.jsf.converters;

import java.util.UUID;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.UserControl;
import br.unifesp.maritaca.core.User;

@FacesConverter("br.unifesp.maritaca.web.jsf.converters.UserConverter")
public class UserConverter implements Converter {
	private UserControl userCtrl;
	public UserConverter() {
		userCtrl = ControllerFactory.getInstance().createUserCtrl();
	}
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		User user = userCtrl.getUser(UUID.fromString(arg2));
		return user;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2.toString();
	}
	
	

}
