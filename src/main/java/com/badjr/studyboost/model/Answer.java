package com.badjr.studyboost.model;

import java.util.Objects;

public class Answer {
	private String answerText;

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Answer answer = (Answer) o;
		return Objects.equals(answerText, answer.answerText);
	}

	@Override
	public int hashCode() {
		return Objects.hash(answerText);
	}
}
