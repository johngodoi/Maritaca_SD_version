package br.unifesp.maritaca.web.jsf.answer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.unifesp.maritaca.business.answer.list.AnswersListerEJB;
import br.unifesp.maritaca.business.answer.list.dto.AnswerDTO;
import br.unifesp.maritaca.business.answer.list.dto.AnswerListerDTO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.web.base.MaritacaJSFBean;

@ManagedBean
@ViewScoped
public class AnswersListerBean extends MaritacaJSFBean {
	private static final long serialVersionUID = 1L;
	
	private static final String answFileNameSufix  = "-answers.csv"; 
	
	@Inject
	private AnswersListerEJB answersListerEJB;	

	private AnswerListerDTO answerListerDTO;	
	
	public AnswersListerBean() {
		/* Default constructor: Nothing here */
	}
	
	public String listAnswers(FormDTO form){
		setAnswerListerDTO(answersListerEJB.
				findAnswerListerDTO(form, super.getCurrentUser()));
		getModuleManager().activeModAndSub("Answer", "listAnswers");
		return null;
	}

	public AnswerListerDTO getAnswerListerDTO() {
		return answerListerDTO;
	}
	
	public void setAnswerListerDTO(AnswerListerDTO answerListerDTO) {
		this.answerListerDTO = answerListerDTO;
	}
	
	public String exportAnswersAsCsv() throws IOException{
		String fileName = getAnswerListerDTO().getFormTitle() + answFileNameSufix;
		
		getResponse().setHeader("Content-Disposition","attachment; filename=" + fileName);  		
		PrintWriter pr = getResponse().getWriter();
				
		for(AnswerDTO answDto : answerListerDTO.getAnswers()){
			pr.print(toCsv(answDto.getUserEmail()));
			pr.print(toCsv(answDto.getCollectDate().toString()));
			String questions = "";
			for(String answ : answDto.getAnswers()){
				questions += toCsv(answ);
			}
			pr.print(questions.replaceAll(",$", ""));
			pr.print("\n");
		}
		pr.flush();
		pr.close();
		
		return null;
	}
	
	private String toCsv(String value){
		return "\"" + value.replace("\n", "") + "\",";
	}
}
