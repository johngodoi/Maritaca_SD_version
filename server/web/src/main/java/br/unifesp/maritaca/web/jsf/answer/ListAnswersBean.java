package br.unifesp.maritaca.web.jsf.answer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.web.Manager;
import br.unifesp.maritaca.web.dto.listanswers.ListAnswersDTO;
import br.unifesp.maritaca.web.jsf.AbstractBean;

@ManagedBean
@ViewScoped
public class ListAnswersBean extends AbstractBean {

	private static final long serialVersionUID = 1L;
	
	private ListAnswersDTO listAnswersDTO;
	
	@ManagedProperty("#{manager}")
	private Manager moduleManager;

	public ListAnswersBean() {
		super(true, false);
	}
	
	public String listAnswers(Form form){
		String formId = form.getKey().toString();
		listAnswersDTO = super.formAnswCtrl.findAnswersFromForm(formId);
		getModuleManager().activeModAndSub("Answer", "listAnswers");
		return null;
	}

	public String getViewAnswer() {
		return "viewAnswer";
	}

	public ListAnswersDTO getListAnswersDTO() {
		return listAnswersDTO;
	}

	public void setListAnswersDTO(ListAnswersDTO listAnswersDTO) {
		this.listAnswersDTO = listAnswersDTO;
	}

	public Manager getModuleManager() {
		return moduleManager;
	}

	public void setModuleManager(Manager moduleManager) {
		this.moduleManager = moduleManager;
	}
	
}