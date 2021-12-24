package com.badjr.studyboost.dao;

import com.badjr.studyboost.model.Answer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnswerFlatFileDao implements Dao<Answer> {

	private String[] pathToFiles;
	private List<Answer> answers;

	public AnswerFlatFileDao(String... pathToFiles) {
		this.pathToFiles = pathToFiles;
		answers = new ArrayList<>();
	}

	@Override
	public Optional<Answer> get(long id) {
		return Optional.empty();
	}

	@Override
	public List<Answer> getAll() {
		if (answers == null || answers.isEmpty()) {
			for (String pathToFile : pathToFiles) {
				try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile))) {
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						Answer answer = new Answer(line);
						answers.add(answer);
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return answers;
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
