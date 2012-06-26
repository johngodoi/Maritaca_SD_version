package br.unifesp.maritaca.business.answer.list.dto;

import java.util.List;

/**
 * DTO used to retrieve all the answers for one form. It is used mainly in the
 * "List Answers" view.
 * 
 * @author tiagobarabasz
 */
public class AnswerListerDTO {

	private List<AnswerDTO> answers;
	private List<String> questions;
	private String formTitle;
	private List<String> types;
	private int questionPosition = 0;

	public List<AnswerDTO> getAnswers() {
		return answers;
	}

	public boolean isNotQuestionPicture(String question) {
		return !(this.isQuestionPicture(question));
	}

	public boolean isQuestionPicture(String question) {
		boolean assertType = types.get(this.questionPosition).equals("picture");
		return assertType;
	}

	public String getAnswerLikeImage(AnswerDTO answer) {
		return "data:image/jpeg;base64," + this.answerFromQuestion(answer);
	}

	public String answerFromQuestion(AnswerDTO answer) {
		return answer.getAnswers().get(this.questionPosition);
	}

	public boolean isWhatQuestion(String question) {
		for (int i = this.questionPosition; i < questions.size(); i++) {
			if (questions.get(i).equals(question)) {
				this.questionPosition = i;
				return true;
			}
		}
		return false;
	}

	public void setAnswers(List<AnswerDTO> answers) {
		this.answers = answers;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public void setQuestions(List<String> questions) {
		this.questions = questions;
	}

	public String getFormTitle() {
		return formTitle;
	}

	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}

	public void setTypes(List<String> types) {
		this.types = types;

	}

	public boolean reset() {
		this.questionPosition = 0;
		return true;
	}

}
