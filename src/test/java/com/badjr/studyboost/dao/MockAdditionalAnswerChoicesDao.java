package com.badjr.studyboost.dao;

import com.badjr.studyboost.model.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MockAdditionalAnswerChoicesDao implements Dao<Answer> {

	List<Answer> additionalAnswerChoices;

	public MockAdditionalAnswerChoicesDao(String[] additionalAnswersText) {
		this.additionalAnswerChoices = Arrays.stream(additionalAnswersText).map(answerText -> {
			Answer answer = new Answer();
			answer.setAnswerText(answerText);
			return answer;
		}).collect(Collectors.toList());
	}

	@Override
	public Optional<Answer> get(long id) {
		return Optional.empty();
	}

	@Override
	public List<Answer> getAll() {
		return additionalAnswerChoices;
	}

	@Override
	public void save(Answer answer) {

	}

	@Override
	public void saveAll(List<Answer> t) {

	}

	@Override
	public void update(Answer answer, String[] params) {

	}

	@Override
	public void delete(Answer answer) {

	}
}
