package br.unifesp.maritaca.web.jsf.form;

import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.edit.FormEditorEJB;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@ViewScoped
public class FormDownloader extends MaritacaJSFBean{

	private static final String XML_EXTENSION = ".xml";

	private static final long serialVersionUID = 1L;
			
	@Inject
	private FormEditorEJB formEditorEJB;
	
	public String download(FormDTO formDto) throws IOException{
		String fileName = formDto.getTitle()+XML_EXTENSION;
		
		getResponse().setHeader("Content-Disposition","attachment; filename=" + fileName);  		
		PrintWriter pr = getResponse().getWriter();
		
		String formXml = formDto.getXml();
		if(formDto.getXml()==null || formDto.getXml().equals("")){
			formXml = loadForm(formDto).getXml();
		}
		
		pr.print(formXml);

		pr.flush();
		pr.close();
		
		return null;
	}
	
	private FormDTO loadForm(FormDTO form){
		return formEditorEJB.getFormDTOByUserDTOAndFormDTO(form, getCurrentUser());
	}
}
