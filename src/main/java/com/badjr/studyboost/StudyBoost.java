package com.badjr.studyboost;

import com.badjr.studyboost.dao.InterchangeInMemoryDao;
import com.badjr.studyboost.engine.StudyBoostEngine;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;

import java.util.ArrayList;
import java.util.List;

public class StudyBoost {
	public static void main(String[] args) {

		InterchangeInMemoryDao interchangeInMemoryDao = new InterchangeInMemoryDao();
		List<Interchange> interchanges = interchangeInMemoryDao.getAll();

		List<Answer> additionalAnswerChoices = createAdditionalAnswerChoices();

		StudyBoostEngine studyBoostEngine = new StudyBoostEngine();

		StudyBoostCli studyBoostCli = new StudyBoostCli(interchanges, additionalAnswerChoices, studyBoostEngine);

		studyBoostCli.start();

	}

	private static List<Answer> createAdditionalAnswerChoices() {
		List<Answer> additionalAnswerChoices = new ArrayList<>();
		String[] additionalAnswersText = {"grault", "garply", "waldo", "fred", "plugh", "xyzzy", "thud"};
		for (int i = 0; i < additionalAnswersText.length; i++) {
			Answer answer = new Answer();
			answer.setAnswerText(additionalAnswersText[i]);
			additionalAnswerChoices.add(answer);
		}
		return additionalAnswerChoices;
	}

}
