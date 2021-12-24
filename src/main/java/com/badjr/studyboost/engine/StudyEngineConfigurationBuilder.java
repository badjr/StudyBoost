package com.badjr.studyboost.engine;

import com.badjr.studyboost.dao.Dao;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;

public final class StudyEngineConfigurationBuilder {
	private Dao<Interchange> interchangeDao;
	private Dao<Answer> additionalAnswerOptionsDao;
	private Boolean reverseQuestionsAndAnswers;

	private StudyEngineConfigurationBuilder() {
	}

	public static StudyEngineConfigurationBuilder aStudyEngineConfiguration() {
		return new StudyEngineConfigurationBuilder();
	}

	public StudyEngineConfigurationBuilder withInterchangeDao(Dao<Interchange> interchangeDao) {
		this.interchangeDao = interchangeDao;
		return this;
	}

	public StudyEngineConfigurationBuilder withAdditionalAnswerOptionsDao(Dao<Answer> additionalAnswerOptionsDao) {
		this.additionalAnswerOptionsDao = additionalAnswerOptionsDao;
		return this;
	}

	public StudyEngineConfigurationBuilder withReverseQuestionsAndAnswers(Boolean reverseQuestionsAndAnswers) {
		this.reverseQuestionsAndAnswers = reverseQuestionsAndAnswers;
		return this;
	}

	public StudyEngineConfiguration build() {
		StudyEngineConfiguration studyEngineConfiguration = new StudyEngineConfiguration();
		studyEngineConfiguration.setInterchangeDao(interchangeDao);
		studyEngineConfiguration.setAdditionalAnswerOptionsDao(additionalAnswerOptionsDao);
		studyEngineConfiguration.setReverseQuestionsAndAnswers(reverseQuestionsAndAnswers);
		return studyEngineConfiguration;
	}
}
