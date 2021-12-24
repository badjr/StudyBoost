package com.badjr.studyboost.engine;

import com.badjr.studyboost.dao.Dao;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;

public class StudyEngineConfiguration {
	private Dao<Interchange> interchangeDao;
	private Dao<Answer> additionalAnswerOptionsDao;
	private Boolean reverseQuestionsAndAnswers;

	public Dao<Interchange> getInterchangeDao() {
		return interchangeDao;
	}

	public void setInterchangeDao(Dao<Interchange> interchangeDao) {
		this.interchangeDao = interchangeDao;
	}

	public Dao<Answer> getAdditionalAnswerOptionsDao() {
		return additionalAnswerOptionsDao;
	}

	public void setAdditionalAnswerOptionsDao(Dao<Answer> additionalAnswerOptionsDao) {
		this.additionalAnswerOptionsDao = additionalAnswerOptionsDao;
	}

	public Boolean getReverseQuestionsAndAnswers() {
		return reverseQuestionsAndAnswers;
	}

	public void setReverseQuestionsAndAnswers(Boolean reverseQuestionsAndAnswers) {
		this.reverseQuestionsAndAnswers = reverseQuestionsAndAnswers;
	}

}
