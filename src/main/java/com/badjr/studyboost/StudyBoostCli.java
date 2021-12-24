package com.badjr.studyboost;

import com.badjr.studyboost.dao.Dao;
import com.badjr.studyboost.dao.InterchangeFlatFileDao;
import com.badjr.studyboost.dao.InterchangeInMemoryDao;
import com.badjr.studyboost.engine.StudyBoostEngine;
import com.badjr.studyboost.engine.StudyEngineConfiguration;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class StudyBoostCli {

	private Dao<Interchange> interchangeDao;
	private StudyBoostEngine studyBoostEngine;
	private StudyEngineConfiguration studyEngineConfiguration;

	public StudyBoostCli(StudyEngineConfiguration studyEngineConfiguration) {
		studyBoostEngine = new StudyBoostEngine();
		this.studyEngineConfiguration = studyEngineConfiguration;
		this.interchangeDao = studyEngineConfiguration.getInterchangeDao();
	}

	public void start() {
		Scanner scanner = new Scanner(System.in);
		String userInput;
		while (true) {
			displayMainPrompt();

			userInput = scanner.nextLine();

			switch (userInput) {
				case "n" -> {
					System.out.println("\nStarting new study session.\n");
					studyBoostEngine.loadStudySession(studyEngineConfiguration);
					studyBoostEngine.shuffleInterchanges();
					startStudySession(scanner, studyBoostEngine);
				}
				case "s" -> {
					System.out.println("\nEnter path(s) to use for input files.\n");
					setUpInputFiles(scanner);
				}
				case "q" -> {
					scanner.close();
					System.out.println("\nExiting.\n");
					System.exit(0);
				}
				default -> System.out.println("\nInvalid command.\n");
			}

		}
	}

	private void startStudySession(Scanner scanner, StudyBoostEngine studyBoostEngine) {
		while (true) {
			Interchange currentInterchange = studyBoostEngine.getCurrentInterchange();
			if (currentInterchange == null) {
				System.out.println("\nNo more questions left.\n");
				break;
			}
			displayNextInterchange(currentInterchange);
			String userAnswer = scanner.nextLine();
			Integer userAnswerNumber;
			try {
				userAnswerNumber = Integer.parseInt(userAnswer);
			}
			catch (NumberFormatException e) {
				System.out.println("\nPlease enter a number.\n");
				continue;
			}
			if (userAnswerNumber.equals(currentInterchange.getCorrectAnswerIndex())) {
				System.out.println("\nCorrect!\n");
				studyBoostEngine.discardCurrentInterchangeForSession();
			}
			else {
				System.out.println("\nTry again.\n");
			}
		}
	}

	private void setUpInputFiles(Scanner scanner) {
		List<String> paths = new ArrayList<>();

		String userLine = scanner.nextLine();
		String[] userInputtedPaths = userLine.split("\s+");
		for (String userInputtedPath : userInputtedPaths) {
			try {
				Path path = Paths.get(userInputtedPath);
				if (Files.exists(path)) {
					paths.add(path.toString());
				}
				else {
					System.out.println("Could not find file: " + userInputtedPath);
				}
			}
			catch (InvalidPathException e) {
				System.out.println("Invalid path: " + userInputtedPath + "; " + e.getMessage());
			}
		}

		if (!paths.isEmpty()) {
			interchangeDao = new InterchangeFlatFileDao(paths.toArray(new String[0]));
		}
		else {
			interchangeDao = new InterchangeInMemoryDao();
		}
		studyEngineConfiguration.setInterchangeDao(interchangeDao);
	}

	private void displayMainPrompt() {
		if (interchangeDao instanceof InterchangeFlatFileDao) {
			String[] pathToFiles = ((InterchangeFlatFileDao) interchangeDao).getPathToFiles();
			System.out.println("Using input file" + (pathToFiles.length > 1 ? "s" : "") + ": " + Arrays.toString(pathToFiles));
		}
		else if (interchangeDao instanceof InterchangeInMemoryDao) {
			System.out.println("Using default questions.");
		}
		System.out.println(
				"""
				(n) Start New Study Session
				(s) Select Input File(s)
				(q) Quit
				""");
	}

	private void displayNextInterchange(Interchange interchange) {
		System.out.println(interchange.getQuestion().getQuestionText());

		List<Answer> answerChoices = interchange.getAnswerChoices();
		for (int i = 0; i < answerChoices.size(); i++) {
			System.out.println(i + ". " + answerChoices.get(i).getAnswerText());
		}
	}

}
