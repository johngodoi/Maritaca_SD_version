package br.unifesp.maritaca.web.dto.listanswers;

import java.util.List;

/**
 * DTO used to create the listAnswers view.
 * This DTO holds info about all the answers that were given to one form.
 * @author tiago.barabasz
 */
//TODO This class belongs to the business module (and not the core) 
public class ListAnswersDTO {
	private String          formTitle;
	private List<AnswerDTO> answersDTO; 
	private List<String>    questionLabels;
	
	public ListAnswersDTO() {
	}
	
	public String searchAnswer(String question, AnswerDTO answer){
		//TODO Modify method in order to stop using the for loop
		Integer questionNumber = null;
		for(int i=0; i<questionLabels.size(); i++){
			if(questionLabels.get(i).equals(question)){
				questionNumber = i;
			}
		}		
		return answer.getQuestionAnswers().get(questionNumber);
	}

	public String getFormTitle() {
		return formTitle;
	}

	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}

	public List<String> getQuestionLabels() {
		return questionLabels;
	}

	public void setQuestionLabels(List<String> questionLabels) {
		this.questionLabels = questionLabels;
	}

	public List<AnswerDTO> getAnswersDTO() {
		return answersDTO;
	}

	public void setAnswersDTO(List<AnswerDTO> answersDTO) {
		this.answersDTO = answersDTO;
	}
}
