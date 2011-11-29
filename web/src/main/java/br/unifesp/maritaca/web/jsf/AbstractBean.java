package br.unifesp.maritaca.web.jsf;

import br.unifesp.maritaca.control.ControllerFactory;
import br.unifesp.maritaca.control.FormAnswerControl;
import br.unifesp.maritaca.control.UserControl;

public abstract class AbstractBean {
	protected FormAnswerControl formAnswCtrl;
	protected UserControl userCtrl;
	
	public AbstractBean(boolean useFormAnsw, boolean useUser) {
		if(useFormAnsw){
			formAnswCtrl = ControllerFactory.getInstance().createFormResponseCtrl();
		}
		
		if(useUser){
			userCtrl = ControllerFactory.getInstance().createUserCtrl();
		}
	}

}
