package br.unifesp.maritaca.web.jsf.form;

import java.util.List;

import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.web.jsf.util.ItemList;

public class MaritacaListItem extends ItemList<MaritacaListDTO>{

	private FormEditorEJB    formEditorEJB;
	
	@Override
	protected MaritacaListDTO searchItemInDatabase(String selectedList) {
		return getFormEditorEJB().searchMaritacaListByName(getSelectedItem());
	}

	@Override
	protected List<MaritacaListDTO> searchAutoCompleteItens(String prefix) {
		return getFormEditorEJB().getOwnerOfMaritacaListByPrefix(prefix);
	}

	@Override
	protected String itemToString(MaritacaListDTO list) {
		return list.getName();
	}

	public FormEditorEJB getFormEditorEJB() {
		return formEditorEJB;
	}

	public void setFormEditorEJB(FormEditorEJB formEditorEJB) {
		this.formEditorEJB = formEditorEJB;
	}

}
