package br.unifesp.maritaca.business.test;

import static org.junit.Assert.*;

import org.junit.Test;

import br.unifesp.maritaca.business.account.edit.AccountEditorEJB;
import br.unifesp.maritaca.business.exception.ParameterException;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.list.list.ListMaritacaListEJB;

public class ParameterVerfierTest {

	@Test
	public void testNullParamaterAccountEjb(){
		AccountEditorEJB accountEditorEJB = new AccountEditorEJB();				
		try{
			accountEditorEJB.saveAccount(null);
			fail();
		} catch (ParameterException e){			
		}		
		try{
			accountEditorEJB.registeredEmail(null);
			fail();
		} catch (ParameterException e){			
		}
	}
	
	@Test
	public void testNullParamaterListEjb(){
		ListMaritacaListEJB listMaritacaEjb  = new ListMaritacaListEJB();		
		try{
			listMaritacaEjb.removeMaritacaList(null);
			fail();
		} catch (ParameterException e){			
		}
	}
	
	@Test
	public void testNullParamaterFormEjb(){
		FormEditorEJB formEditorEjb    = new FormEditorEJB();		
		try{
			formEditorEjb.getFormDTOByKey(null);
			fail();
		} catch (ParameterException e){			
		}		
	}
}
