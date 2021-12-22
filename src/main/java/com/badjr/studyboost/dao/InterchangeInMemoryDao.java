package com.badjr.studyboost.dao;

import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;
import com.badjr.studyboost.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterchangeInMemoryDao implements Dao<Interchange> {

	List<Interchange> interchanges;

	public InterchangeInMemoryDao() {
		interchanges = createDefaultInterchanges();
	}

	@Override
	public Optional<Interchange> get(long id) {
		return Optional.of(interchanges.get((int) id));
	}

	@Override
	public List<Interchange> getAll() {
		return interchanges;
	}

	@Override
	public void save(Interchange interchange) {
		this.interchanges.add(interchange);
	}

	@Override
	public void saveAll(List<Interchange> interchanges) {
		this.interchanges.addAll(interchanges);
	}

	@Override
	public void update(Interchange interchange, String[] params) {

	}

	@Override
	public void delete(Interchange interchange) {

	}

	private static List<Interchange> createDefaultInterchanges() {
		List<Interchange> interchanges = new ArrayList<>();

		String[] questionItems = {"foo", "bar", "baz", "qux", "quux"};
		String[] answerItems = {"bar", "baz", "qux", "quux", "corge"};

		for (int i = 0; i < questionItems.length; i++) {
			Question question = new Question();
			question.setQuestionText("What comes after " + questionItems[i] + "?");
			Answer answer = new Answer();
			answer.setAnswerText(answerItems[i]);
			Interchange interchange = new Interchange();
			interchange.setQuestion(question);
			interchange.setAnswer(answer);
			interchange.setMaxWrongAnswers(4);
			interchanges.add(interchange);
		}

		return interchanges;
	}

}
