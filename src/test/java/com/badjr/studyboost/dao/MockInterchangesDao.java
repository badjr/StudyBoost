package com.badjr.studyboost.dao;

import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;
import com.badjr.studyboost.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockInterchangesDao implements Dao<Interchange> {

	List<Interchange> interchanges;

	public MockInterchangesDao(String[] questions, String[] answers, Integer maxWrongAnswers) {
		interchanges = createInterchanges(questions, answers, maxWrongAnswers);
	}

	@Override
	public Optional<Interchange> get(long id) {
		return Optional.empty();
	}

	@Override
	public List<Interchange> getAll() {
		return interchanges;
	}

	@Override
	public void save(Interchange interchange) {

	}

	@Override
	public void saveAll(List<Interchange> t) {

	}

	@Override
	public void update(Interchange interchange, String[] params) {

	}

	@Override
	public void delete(Interchange interchange) {

	}

	private List<Interchange> createInterchanges(String[] questions, String[] answers, Integer maxWrongAnswers) {
		List<Interchange> interchanges = new ArrayList<>();
		for (int i = 0; i < questions.length; i++) {
			Interchange interchange = new Interchange();
			interchange.setMaxWrongAnswers(maxWrongAnswers);
			Question question = new Question();
			question.setQuestionText(questions[i]);
			interchange.setQuestion(question);
			Answer answer = new Answer();
			answer.setAnswerText(answers[i]);
			interchange.setAnswer(answer);
			interchanges.add(interchange);
		}
		return interchanges;
	}

}
