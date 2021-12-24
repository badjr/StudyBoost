package com.badjr.studyboost.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Interchange {
	private Question question;
	private Answer answer;
	private List<Answer> answerChoices;

	private Integer maxWrongAnswers;

	public Interchange() {
		answerChoices = new ArrayList<>();
	}

	public Interchange(Question question, Answer answer) {
		this();
		this.question = question;
		this.answer = answer;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public Integer getMaxWrongAnswers() {
		return maxWrongAnswers;
	}

	public void setMaxWrongAnswers(Integer maxWrongAnswers) {
		this.maxWrongAnswers = maxWrongAnswers;
	}

	public List<Answer> getAnswerChoices() {
		return answerChoices;
	}

	public Integer getCorrectAnswerIndex() {
		return answerChoices.indexOf(answer);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Interchange that = (Interchange) o;
		return Objects.equals(question, that.question) && Objects.equals(answer, that.answer) && Objects.equals(
				answerChoices, that.answerChoices) && Objects.equals(maxWrongAnswers, that.maxWrongAnswers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(question, answer, answerChoices, maxWrongAnswers);
	}
}
