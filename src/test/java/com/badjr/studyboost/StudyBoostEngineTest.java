package com.badjr.studyboost;

import com.badjr.studyboost.dao.Dao;
import com.badjr.studyboost.dao.MockAdditionalAnswerChoicesDao;
import com.badjr.studyboost.dao.MockInterchangesDao;
import com.badjr.studyboost.engine.StudyBoostEngine;
import com.badjr.studyboost.engine.StudyEngineConfiguration;
import com.badjr.studyboost.engine.StudyEngineConfigurationBuilder;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;
import com.badjr.studyboost.model.Question;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StudyBoostEngineTest {

	@Test
	void testLoadStudySession() {
		//Arrange
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz"};
		String[] answers = {"bar", "baz", "qux"};
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, null);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.build();

		//Act
		studyBoostEngine.loadStudySession(studyEngineConfiguration);

		//Assert
		assertEquals(3, studyBoostEngine.getInterchanges().size());
		for (Interchange interchange : studyBoostEngine.getInterchanges()) {
			assertEquals(3, interchange.getAnswerChoices().size());
			assertEquals(interchange.getAnswer(), interchange.getAnswerChoices().get(interchange.getCorrectAnswerIndex()));
		}


	}

	@Test
	public void testLoadStudySessionWithMaxWrongAnswers() {
		//Arrange
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"bar", "baz", "qux", "quux", "corge"};
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, 3);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.build();

		//Act
		studyBoostEngine.loadStudySession(studyEngineConfiguration);

		//Assert
		assertEquals(5, studyBoostEngine.getInterchanges().size());
		for (Interchange interchange : studyBoostEngine.getInterchanges()) {
			assertEquals(4, interchange.getAnswerChoices().size());
		}

	}

	@Test
	void testLoadStudySession_withQuestionsAndAnswersReversed() {
		//Arrange
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"grault", "garply", "waldo", "fred", "plugh"};
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, 3);

		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.withReverseQuestionsAndAnswers(true)
						.build();

		//Act
		studyBoostEngine.loadStudySession(studyEngineConfiguration);

		//Assert
		assertEquals(5, studyBoostEngine.getInterchanges().size());
		for (Interchange interchange : studyBoostEngine.getInterchanges()) {
			assertEquals(4, interchange.getAnswerChoices().size());
			assertEquals(interchange.getAnswer(), interchange.getAnswerChoices().get(interchange.getCorrectAnswerIndex()));
		}
		List<String> actualQuestions = mockInterchangeDao.getAll().stream().map(Interchange::getQuestion).map(Question::getQuestionText).toList();
		List<String> expectedQuestions = Arrays.asList("grault", "garply", "waldo", "fred", "plugh");
		assertEquals(actualQuestions.size(), expectedQuestions.size());
		assertThat(actualQuestions.toArray(), Matchers.arrayContainingInAnyOrder(expectedQuestions.toArray()));

		List<String> actualAnswers = mockInterchangeDao.getAll().stream().map(Interchange::getAnswer).map(Answer::getAnswerText).toList();
		List<String> expectedAnswers = Arrays.asList("foo", "bar", "baz", "qux", "quux");
		assertEquals(actualAnswers.size(), expectedAnswers.size());
		assertThat(actualAnswers.toArray(), Matchers.arrayContainingInAnyOrder(expectedAnswers.toArray()));
	}

	@Test
	public void testGetNextInterchange() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"bar", "baz", "qux", "quux", "corge"};
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, 3);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.build();

		studyBoostEngine.loadStudySession(studyEngineConfiguration);

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
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, 3);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.build();

		studyBoostEngine.loadStudySession(studyEngineConfiguration);

		Interchange interchange = studyBoostEngine.getNextInterchange();
		assertNull(interchange);

	}

	@Test
	void testDiscardInterchangeForSession() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo", "bar", "baz", "qux", "quux"};
		String[] answers = {"bar", "baz", "qux", "quux", "corge"};
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, 3);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.build();

		studyBoostEngine.loadStudySession(studyEngineConfiguration);

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
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, 3);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.build();

		studyBoostEngine.loadStudySession(studyEngineConfiguration);

		studyBoostEngine.discardCurrentInterchangeForSession();

		assertEquals(0, studyBoostEngine.getInterchanges().size());

	}

	@Test
	void testDiscardInterchangeForSession_whenOneLeft() {
		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		String[] questions = {"foo"};
		String[] answers = {"bar"};
		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, 3);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.build();

		studyBoostEngine.loadStudySession(studyEngineConfiguration);

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

		Dao<Interchange> mockInterchangeDao = new MockInterchangesDao(questions, answers, null);
		Dao<Answer> additionalAnswerOptionsDao = new MockAdditionalAnswerChoicesDao(additionalAnswersText);
		StudyEngineConfiguration studyEngineConfiguration =
				StudyEngineConfigurationBuilder
						.aStudyEngineConfiguration()
						.withInterchangeDao(mockInterchangeDao)
						.withAdditionalAnswerOptionsDao(additionalAnswerOptionsDao)
						.build();

		studyBoostEngine.loadStudySession(studyEngineConfiguration);
		for (Interchange interchange : studyBoostEngine.getInterchanges()) {
			assertEquals(interchange.getAnswer(), interchange.getAnswerChoices().get(interchange.getCorrectAnswerIndex()));
			assertEquals(12, interchange.getAnswerChoices().size());
		}

	}

}