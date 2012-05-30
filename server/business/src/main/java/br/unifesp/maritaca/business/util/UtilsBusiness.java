package br.unifesp.maritaca.business.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import br.unifesp.maritaca.business.exception.ObjectConversionException;
import br.unifesp.maritaca.business.exception.ParameterException;
import br.unifesp.maritaca.persistence.util.UtilsPersistence;

public class UtilsBusiness {

	private static Log log = LogFactory.getLog(UtilsBusiness.class);
	
	public static void verifyString(String str, Class<?> parameterClass){	
		if(str==null || str.isEmpty()){
			throw new ParameterException(str, parameterClass);
		}
	}
	
	public static final void verifyObj(Object obj, Class<?> objClass){
		if(obj==null){
			throw new ParameterException(objClass);
		}
	}
	
	/**
	 * This method is used to convert classes from the model to their
	 * respectives DTOs and vice versa.<br>
	 * This is performed by creating a new instance of the desired class
	 * (passed as the second parameter) and searching the original object
	 * (passed as the first parameters) for getters that have a return
	 * type and name that matches those used by the setters in the desired
	 * class.<br> 
	 * @param originalObj
	 * @param targetClass
	 * @return An object of the desired class with correspondent atributes
	 * initiated to match the values of the given object.
	 */
	public static final <T> T convertToClass(Object originalObj, Class<T> targetClass){
		if(originalObj == null){
			return null;
		}
		Field currentField = null;
		try {
			T           targetObj    = targetClass.getConstructor().newInstance();			
			List<Field> targetFields = Arrays.asList(targetClass.getDeclaredFields());
			for(Field tField : targetFields){
				currentField = tField;

				String getterName = "get"+UtilsPersistence.toUpperFirst(tField.getName());				
				Method getter;
				try{
					getter = originalObj.getClass().getDeclaredMethod(getterName);
				} catch (NoSuchMethodException e){
					convertClassLogError(tField, targetClass, originalObj);
					continue;
				}
				
				String setterName = "set"+UtilsPersistence.toUpperFirst(tField.getName());
				Method setter = null;
				Object value  = null;
				try{
					setter = targetObj.getClass().getDeclaredMethod(setterName, getter.getReturnType());
					value  = getter.invoke(originalObj);
				} catch (NoSuchMethodException e){
				}
				try{
					if(setter==null){
						setter = targetObj.getClass().getDeclaredMethod(setterName, String.class);
						value  = getter.invoke(originalObj);
						if(value != null){
							value = value.toString();
						}
					}
				} catch (NoSuchMethodException e){
					convertClassLogError(tField, targetClass, originalObj);
					continue;
				}								
				if(value == null )
					continue;
				setter.invoke(targetObj, value);
			}			
			return targetObj;
		} catch (Exception e) {
			throw new ObjectConversionException(originalObj.getClass(), targetClass.getClass(), currentField);
		}		
	}	
	
	private static void convertClassLogError(Field tField, Class<?> targetClass, Object originalObj){
		getLog().warn(	"Property: "+tField.getName()+
				 		", from target class: "+targetClass.getSimpleName()+
				 		", not found in object: " + originalObj.getClass().getSimpleName());		
	}

	public static Log getLog() {
		return log;
	}
}
