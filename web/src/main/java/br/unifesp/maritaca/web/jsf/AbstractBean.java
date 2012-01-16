package br.unifesp.maritaca.web.jsf;

import java.io.Serializable;

import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

public abstract class AbstractBean implements Serializable {
	protected FormAnswerModel formAnswCtrl;
	protected UserModel userCtrl;
	
	public AbstractBean(boolean useFormAnsw, boolean useUser) {
		if(useFormAnsw){
			formAnswCtrl = ModelFactory.getInstance().createFormResponseModel();
		}
		
		if(useUser){
			userCtrl = ModelFactory.getInstance().createUserModel();
		}
	}

}
