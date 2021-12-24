package com.badjr.studyboost.model;

import java.util.Objects;

public class Question {
	private String questionText;

	public Question() {

	}

	public Question(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Question question = (Question) o;
		return Objects.equals(questionText, question.questionText);
	}

	@Override
	public int hashCode() {
		return Objects.hash(questionText);
	}
}
