package com.badjr.studyboost;

import com.badjr.studyboost.engine.StudyBoostEngine;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;
import com.badjr.studyboost.model.Question;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StudyBoostEngineTest {

	@Test
	void testLoadStudySession() {
		//Arrange
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz"};
		String[] answers = {"bar", "baz", "qux"};
		List<Interchange> interchanges = createInterchanges(questions, answers, null);

		//Act
		studyBoostEngine.loadStudySession(interchanges);

		//Assert
		assertEquals(3, studyBoostEngine.getInterchanges().size());
		for (Interchange interchange : studyBoostEngine.getInterchanges()) {
			assertEquals(3, interchange.getAnswerChoices().size());
			assertEquals(interchange.getAnswer(), ((List<Answer>) interchange.getAnswerChoices()).get(interchange.getCorrectAnswerIndex()));
		}


	}

	@Test
	public void testLoadStudySessionWithMaxWrongAnswers() {
		//Arrange
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"bar", "baz", "qux", "quux", "corge"};
		List<Interchange> interchanges = createInterchanges(questions, answers, 3);

		//Act
		studyBoostEngine.loadStudySession(interchanges);

		//Assert
		assertEquals(5, studyBoostEngine.getInterchanges().size());
		for (Interchange interchange : studyBoostEngine.getInterchanges()) {
			assertEquals(4, interchange.getAnswerChoices().size());
		}


	}

	@Test
	public void testGetNextInterchange() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"bar", "baz", "qux", "quux", "corge"};
		List<Interchange> interchanges = createInterchanges(questions, answers, 3);

		studyBoostEngine.loadStudySession(interchanges);

		Interchange interchange = studyBoostEngine.getCurrentInterchange();
		assertEquals("foo", interchange.getQuestion().getQuestionText());
		interchange = studyBoostEngine.getNextInterchange();
		assertEquals("bar", interchange.getQuestion().getQuestionText());
		interchange = studyBoostEngine.getNextInterchange();
		assertEquals("baz", interchange.getQuestion().getQuestionText());
		interchange = studyBoostEngine.getNextInterchange();
		assertEquals("qux", interchange.getQuestion().getQuestionText());
		interchange = studyBoostEngine.getNextInterchange();
		assertEquals("quux", interchange.getQuestion().getQuestionText());
		interchange = studyBoostEngine.getNextInterchange();
		assertEquals("foo", interchange.getQuestion().getQuestionText());

	}

	@Test
	public void testGetNextInterchange_whenEmpty() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {};
		String[] answers = {};
		List<Interchange> interchanges = createInterchanges(questions, answers, 3);

		studyBoostEngine.loadStudySession(interchanges);

		Interchange interchange = studyBoostEngine.getNextInterchange();
		assertNull(interchange);

	}

	@Test
	void testDiscardInterchangeForSession() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"bar", "baz", "qux", "quux", "corge"};
		List<Interchange> interchanges = createInterchanges(questions, answers, 3);

		studyBoostEngine.loadStudySession(interchanges);

		assertEquals("foo", studyBoostEngine.getCurrentInterchange().getQuestion().getQuestionText());
		studyBoostEngine.discardCurrentInterchangeForSession();

		assertEquals("bar", studyBoostEngine.getCurrentInterchange().getQuestion().getQuestionText());

		assertEquals(4, studyBoostEngine.getInterchanges().size());

	}

	@Test
	void testDiscardInterchangeForSession_whenEmpty() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {};
		String[] answers = {};
		List<Interchange> interchanges = createInterchanges(questions, answers, 3);

		studyBoostEngine.loadStudySession(interchanges);

		studyBoostEngine.discardCurrentInterchangeForSession();

		assertEquals(0, studyBoostEngine.getInterchanges().size());

	}

	@Test
	void testDiscardInterchangeForSession_whenOneLeft() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo"};
		String[] answers = {"bar"};
		List<Interchange> interchanges = createInterchanges(questions, answers, 3);

		studyBoostEngine.loadStudySession(interchanges);

		assertEquals("foo", studyBoostEngine.getCurrentInterchange().getQuestion().getQuestionText());

		studyBoostEngine.discardCurrentInterchangeForSession();

		assertEquals(0, studyBoostEngine.getInterchanges().size());
		assertNull(studyBoostEngine.getCurrentInterchange());

	}

	@Test
	void testPopulateAdditionalAnswerChoices() {

		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"bar", "baz", "qux", "quux", "corge"};
		String[] additionalAnswersText = {"grault", "garply", "waldo", "fred", "plugh", "xyzzy", "thud"};
		List<Answer> additionalAnswerChoices = Arrays.stream(additionalAnswersText).map(answerText -> {
			Answer answer = new Answer();
			answer.setAnswerText(answerText);
			return answer;
		}).collect(Collectors.toList());
		List<Interchange> interchanges = createInterchanges(questions, answers, 4);

		studyBoostEngine.loadStudySession(interchanges, additionalAnswerChoices);
		for (Interchange interchange : studyBoostEngine.getInterchanges()) {
			assertEquals(interchange.getAnswer(), ((List<Answer>) interchange.getAnswerChoices()).get(interchange.getCorrectAnswerIndex()));
		}

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