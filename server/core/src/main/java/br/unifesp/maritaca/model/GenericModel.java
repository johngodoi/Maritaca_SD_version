package br.unifesp.maritaca.model;

import br.unifesp.maritaca.core.User;

public interface GenericModel {
	void setCurrentUser(User currentUser);
	User getCurrentUser();
}
