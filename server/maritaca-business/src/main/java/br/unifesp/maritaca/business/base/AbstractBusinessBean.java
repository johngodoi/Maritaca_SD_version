package br.unifesp.maritaca.business.base;

import java.io.Serializable;
import javax.persistence.Transient;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;

public abstract class AbstractBusinessBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	protected FormAnswerModel formAnswCtrl;
	@Transient
	protected UserModel userCtrl;

	public AbstractBusinessBean(boolean useFormAnsw, boolean useUser) {
		if (useFormAnsw) {
			formAnswCtrl = ModelFactory.getInstance().createFormResponseModel();
		}

		if (useUser) {
			userCtrl = ModelFactory.getInstance().createUserModel();
		}
	}
}
