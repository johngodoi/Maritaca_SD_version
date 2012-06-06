package br.unifesp.maritaca.persistence.entity;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.unifesp.maritaca.persistence.annotation.Index;
import br.unifesp.maritaca.persistence.annotation.Minimal;

@Entity
@Table(name="Answer")
public class Answer {
	
	@Id
	private UUID key;

	@Minimal
	@Index(indexed=true)
	@Column(name="form")
	private UUID form;

	@Minimal
	@Index(indexed=true)
	@Column(name="user")
	private UUID user;

	@me.prettyprint.hom.annotations.Column(name="questions")
	private List<QuestionAnswer> questions;
	
	@Column(name="creationDate")
	private Long creationDate;

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public void setKey(String ks) {
		this.key = UUID.fromString(ks);
	}
	
	public UUID getForm() {
		return form;
	}

	public void setForm(UUID form) {
		this.form = form;
	}

	public UUID getUser() {
		return user;
	}

	public void setUser(UUID user) {
		this.user = user;
	}

	public List<QuestionAnswer> getQuestions() {
		return questions;
	}
	
	public void setQuestions(List<QuestionAnswer> questions) {
		this.questions = questions;
	}
	
	@Override
	public String toString() {
		if (getKey() != null) {
			return getKey().toString();
		}
		return super.toString();
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}
}
