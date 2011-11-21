package br.unifesp.maritaca.control;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public interface FormResponseController {
	boolean saveForm(Form form) throws IllegalArgumentException,
	SecurityException, IllegalAccessException, NoSuchMethodException,
	InvocationTargetException;
	
	Form getForm(UUID uid)throws IllegalArgumentException,
	SecurityException, InstantiationException, IllegalAccessException,
	InvocationTargetException, NoSuchMethodException;
	
	void setEntityManager(EntityManager em);
}
