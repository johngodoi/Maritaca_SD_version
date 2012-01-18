package br.unifesp.maritaca.web.jsf;

import java.io.Serializable;

import javax.persistence.Transient;

import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

public abstract class AbstractBean implements Serializable {
	@Transient
	protected FormAnswerModel formAnswCtrl;
	@Transient
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
