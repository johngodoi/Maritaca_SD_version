package br.unifesp.maritaca.business.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import br.unifesp.maritaca.business.exception.ObjectConversionException;
import br.unifesp.maritaca.persistence.util.UtilsPersistence;

/**
 * This class has generic methods that are use in the persistence 
 * business
 * 
 * @author Maritaca team
 */
public class UtilsBusiness {

	private static Log log = LogFactory.getLog(UtilsBusiness.class);
	
	/**
	 * This method is used to reflex classes. An example should be from 
	 * entity class to DTO class and vice versa.<br>
	 * This is performed by creating a new instance of the desired class
	 * (passed as the second parameter) and searching the original object
	 * (passed as the first parameters) for getters that have a return
	 * type and name that matches those used by the setters in the desired
	 * class.
	 *  
	 * @param origin is the Source Class that contains the data
	 * @param target is the Target Class that will be reflected
	 * @return An object of the desired class with correspondent attributes
	 * 		   initiated to match the values of the given object.
	 */
	public static final <T> T reflectClasses(Object origin, Class<T> target){
		if(origin == null){
			return null;
		}
		Field currentField = null;
		try {
			T           targetObj    = target.getConstructor().newInstance();			
			List<Field> targetFields = Arrays.asList(target.getDeclaredFields());
			for(Field tField : targetFields){
				currentField = tField;

				String getterName = "get"+UtilsPersistence.toUpperFirst(tField.getName());				
				Method getter;
				try{
					getter = origin.getClass().getDeclaredMethod(getterName);
				} catch (NoSuchMethodException e){
					convertClassLogError(tField, target, origin);
					continue;
				}
				
				String setterName = "set"+UtilsPersistence.toUpperFirst(tField.getName());
				Method setter = null;
				Object value  = null;
				try{
					setter = targetObj.getClass().getDeclaredMethod(setterName, getter.getReturnType());
					value  = getter.invoke(origin);
				} catch (NoSuchMethodException e){
				}
				try{
					if(setter==null){
						setter = targetObj.getClass().getDeclaredMethod(setterName, String.class);
						value  = getter.invoke(origin);
						if(value != null){
							value = value.toString();
						}
					}
				} catch (NoSuchMethodException e){
					convertClassLogError(tField, target, origin);
					continue;
				}								
				if(value == null )
					continue;
				setter.invoke(targetObj, value);
			}			
			return targetObj;
		} catch (Exception e) {
			throw new ObjectConversionException(origin.getClass(), target.getClass(), currentField);
		}		
	}	
	
	/**
	 * This methods is to log an error in reflectClasses method.
	 * @param tField
	 * @param targetClass
	 * @param originalObj
	 */
	private static void convertClassLogError(Field tField, Class<?> targetClass, Object originalObj){
		getLog().warn(	"Property: "+tField.getName()+
				 		", from target class: "+targetClass.getSimpleName()+
				 		", not found in object: " + originalObj.getClass().getSimpleName());		
	}

	public static Log getLog() {
		return log;
	}
}
