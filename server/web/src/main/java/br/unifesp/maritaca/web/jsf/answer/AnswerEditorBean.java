package br.unifesp.maritaca.web.jsf.answer;

import javax.inject.Inject;

import br.unifesp.maritaca.business.answer.list.AnswersListerEJB;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

public class AnswerEditorBean extends MaritacaJSFBean {

	
	@Inject
	private AnswersListerEJB answersListerEJB;
	
	public String answer(FormDTO form) {
		getModuleManager().activeModAndSub("Answer", "newAnswer");
		return null;
	}
}
