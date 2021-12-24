package com.badjr.studyboost.engine;

import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class StudyBoostEngine {
	private List<Interchange> interchanges;
	private List<Answer> additionalAnswerOptions;
	private Iterator<Interchange> iterator;
	private Interchange currentInterchange;
	private StudyEngineConfiguration studyEngineConfiguration;

	public StudyBoostEngine() {
		interchanges = new ArrayList<>();
	}

	public List<Interchange> getInterchanges() {
		return interchanges;
	}

	public void loadStudySession(StudyEngineConfiguration studyEngineConfiguration) {
		this.studyEngineConfiguration = studyEngineConfiguration;
		interchanges = studyEngineConfiguration.getInterchangeDao().getAll();
		if (studyEngineConfiguration.getAdditionalAnswerOptionsDao() != null) {
			additionalAnswerOptions = studyEngineConfiguration.getAdditionalAnswerOptionsDao().getAll();
		}
		populateAnswerChoices(interchanges, additionalAnswerOptions);
		currentInterchange = getNextInterchange();
	}

	private void populateAnswerChoices(List<Interchange> interchanges, List<Answer> additionalAnswerChoices) {
		List<Answer> answerChoices = interchanges
				.stream()
				.map(this::getAnswer)
				.collect(Collectors.toList());

		if (additionalAnswerChoices != null) {
			answerChoices.addAll(additionalAnswerChoices);
		}

		populateAnswerChoicesRandomly(interchanges, answerChoices);
	}

	private Answer getAnswer(Interchange interchange) {
		if (studyEngineConfiguration.getReverseQuestionsAndAnswers() != null
				&& studyEngineConfiguration.getReverseQuestionsAndAnswers()) {
			String originalQuestionText = interchange.getQuestion().getQuestionText();
			interchange.getQuestion().setQuestionText(interchange.getAnswer().getAnswerText());
			interchange.getAnswer().setAnswerText(originalQuestionText);
		}
		return interchange.getAnswer();
	}

	private void addCorrectAnswer(Interchange interchange) {
		interchange.getAnswerChoices().add(interchange.getAnswer());
	}

	private void populateAnswerChoicesRandomly(List<Interchange> interchanges, List<Answer> answerChoices) {
		for (Interchange interchange : interchanges) {
			addCorrectAnswer(interchange);
			Collections.shuffle(answerChoices, new SecureRandom());
			if (interchange.getMaxWrongAnswers() != null && interchange.getMaxWrongAnswers() > 0 && interchange.getMaxWrongAnswers() < interchanges.size()) {
				int answersAdded = 0;
				for (Answer answerChoice : answerChoices) {
					if (!interchange.getAnswer().equals(answerChoice)) {
						interchange.getAnswerChoices().add(answerChoice);
						answersAdded++;
					}
					if (answersAdded == interchange.getMaxWrongAnswers()) {
						break;
					}
				}
			}
			else {
				for (Answer shuffledAnswer : answerChoices) {
					if (!interchange.getAnswer().equals(shuffledAnswer)) {
						interchange.getAnswerChoices().add(shuffledAnswer);
					}
				}
			}
			Collections.shuffle(interchange.getAnswerChoices(), new SecureRandom());
		}
	}

	public Interchange getCurrentInterchange() {
		return currentInterchange;
	}

	public Interchange getNextInterchange() {
		if (iterator == null) {
			iterator = interchanges.iterator();
		}

		if (iterator.hasNext()) {
			currentInterchange = iterator.next();
			return currentInterchange;
		}
		else if (interchanges.size() > 0) {
			currentInterchange = interchanges.iterator().next();
			return currentInterchange;
		}
		else {
			return null;
		}
	}

	public void discardCurrentInterchangeForSession() {
		if (currentInterchange == null) {
			return;
		}
		if (iterator == null) {
			iterator = interchanges.iterator();
		}

		if (currentInterchange != null) {
			iterator.remove();
			currentInterchange = getNextInterchange();
		}
	}

	public void shuffleInterchanges() {
		if (interchanges != null && !interchanges.isEmpty()) {
			Collections.shuffle(interchanges);
			iterator = interchanges.iterator();
			getNextInterchange();
		}
	}
}
