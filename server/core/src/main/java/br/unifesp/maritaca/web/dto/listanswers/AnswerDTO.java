package br.unifesp.maritaca.web.dto.listanswers;

import java.util.Date;
import java.util.List;

/**
 * DTO used to create the listAnswers view.
 * This DTO has information about one particular group of answers of one form.
 * @author tiago.barabasz
 */
public class AnswerDTO {	
	private String       author;	
	private Date         collectionDate;
	private List<String> questionAnswers;
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Date getCollectionDate() {
		return collectionDate;
	}
	
	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public List<String> getQuestionAnswers() {
		return questionAnswers;
	}

	public void setQuestionAnswers(List<String> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}
}
