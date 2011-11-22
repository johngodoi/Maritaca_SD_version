package br.unifesp.maritaca.control;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.persistence.EntityManager;

public interface FormResponseController {
	boolean saveForm(Form form) throws IllegalArgumentException,
	SecurityException, IllegalAccessException, NoSuchMethodException,
	InvocationTargetException;
	
	Form getForm(UUID uid)throws IllegalArgumentException,
	SecurityException, InstantiationException, IllegalAccessException,
	InvocationTargetException, NoSuchMethodException,
	NoSuchFieldException;
	
	void setEntityManager(EntityManager em);
	
	Collection<Form> listAllForms()throws IllegalArgumentException,
	SecurityException, InstantiationException, IllegalAccessException,
	InvocationTargetException, NoSuchMethodException,
	NoSuchFieldException; 
	
	Collection<Form> listAllFormsMinimal()throws IllegalArgumentException,
	SecurityException, InstantiationException, IllegalAccessException,
	InvocationTargetException, NoSuchMethodException,
	NoSuchFieldException; 
}
