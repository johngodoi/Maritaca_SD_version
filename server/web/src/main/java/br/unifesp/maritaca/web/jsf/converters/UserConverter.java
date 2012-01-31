package br.unifesp.maritaca.web.jsf.converters;

import java.util.UUID;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

@FacesConverter("br.unifesp.maritaca.web.jsf.converters.UserConverter")
public class UserConverter implements Converter {
	private UserModel userCtrl;
	public UserConverter() {
		userCtrl = ModelFactory.getInstance().createUserModel();
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
