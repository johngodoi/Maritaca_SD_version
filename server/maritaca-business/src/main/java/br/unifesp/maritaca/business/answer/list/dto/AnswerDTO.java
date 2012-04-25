package br.unifesp.maritaca.business.answer.list.dto;

import java.util.Date;
import java.util.List;

/**
 * This DTO is used to represent one collect of the form
 * containing one answer per question.  
 * @author tiagobarabasz
 */
public class AnswerDTO {
	
	private String       userEmail;
	
	private Date         collectDate;
	
	private List<String> answers;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Date getCollectDate() {
		return collectDate;
	}

	public void setCollectDate(Date collectDate) {
		this.collectDate = collectDate;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}
	
}
