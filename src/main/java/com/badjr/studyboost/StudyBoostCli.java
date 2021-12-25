package com.badjr.studyboost;

import com.badjr.studyboost.dao.AnswerFlatFileDao;
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
	private Dao<Answer> answerDao;
	private StudyBoostEngine studyBoostEngine;
	private StudyEngineConfiguration studyEngineConfiguration;
	private int totalInterchanges;

	public StudyBoostCli(StudyEngineConfiguration studyEngineConfiguration) {
		studyBoostEngine = new StudyBoostEngine();
		this.studyEngineConfiguration = studyEngineConfiguration;
		this.interchangeDao = studyEngineConfiguration.getInterchangeDao();
		this.answerDao = studyEngineConfiguration.getAdditionalAnswerOptionsDao();
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
					totalInterchanges = studyBoostEngine.getInterchanges().size();
					studyBoostEngine.shuffleInterchanges();
					startStudySession(scanner, studyBoostEngine);
				}
				case "c" -> displayConfiguration();
				case "m" -> {
					String interchangesSource = getInterchangesSource();
					if (!interchangesSource.isEmpty()) {
						System.out.println(interchangesSource);
					}
					System.out.print("Enter input files, \"clear\" to clear, or <Enter> for no change: ");
					setUpInputFiles(scanner);
					System.out.println();

					String additionalAnswersSource = getAdditionalAnswersSource();
					if (!additionalAnswersSource.isEmpty()) {
						System.out.println(additionalAnswersSource);
					}
					System.out.print("Enter input files for additional answer options, \"clear\" to clear, or <Enter> for no change: ");
					setUpAdditionalAnswersInputFiles(scanner);
					System.out.println();

					if (studyEngineConfiguration.getReverseQuestionsAndAnswers()) {
						System.out.println("Questions and answers reversed: on");
					}
					else {
						System.out.println("Questions and answers reversed: off");
					}
					System.out.print("Reverse questions and answers? Enter (y)es, (n)o, or <Enter> for no change: ");
					setUpReverseQuestionsAndAnswers(scanner);
					System.out.println();

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
		if (userLine.length() > 0) {
			if ("clear".equals(userLine)) {
				interchangeDao = new InterchangeInMemoryDao();
				studyEngineConfiguration.setInterchangeDao(interchangeDao);
				System.out.println(getInterchangesSource());
			}
			else {
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
					studyEngineConfiguration.setInterchangeDao(interchangeDao);
					System.out.println(getInterchangesSource());
				}
			}
		}
		System.out.println(getInterchangesSource());
	}

	private void setUpAdditionalAnswersInputFiles(Scanner scanner) {
		List<String> paths = new ArrayList<>();

		String userLine = scanner.nextLine();
		if (userLine.length() > 0) {
			if ("clear".equals(userLine)) {
				answerDao = null;
				studyEngineConfiguration.setAdditionalAnswerOptionsDao(null);
				System.out.println("Now using no files for additional answer options.");
			}
			else {
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
					answerDao = new AnswerFlatFileDao(paths.toArray(new String[0]));
					studyEngineConfiguration.setAdditionalAnswerOptionsDao(answerDao);
					System.out.println(getAdditionalAnswersSource());
				}
			}
		}
		System.out.println(getAdditionalAnswersSource());
	}

	private void setUpReverseQuestionsAndAnswers(Scanner scanner) {
		String userInput;

		while (true) {
			userInput = scanner.nextLine();
			if (userInput.trim().length() > 0) {
				if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
					studyEngineConfiguration.setReverseQuestionsAndAnswers(true);
					System.out.println("Questions and answers will be reversed.");
					break;
				}
				else if (userInput.equalsIgnoreCase("n") || userInput.equalsIgnoreCase("no")) {
					studyEngineConfiguration.setReverseQuestionsAndAnswers(false);
					System.out.println("Questions and answers will not be reversed.");
					break;
				}
				else {
					System.out.println("Please enter (y)es, (n)o, or <Enter>.");
				}
			}
			else {
				System.out.printf("Questions and answers reversed: %s\n", studyEngineConfiguration.getReverseQuestionsAndAnswers() ? "on" : "off");
				break;
			}
		}

	}

	private void displayConfiguration() {
		System.out.println();
		System.out.println("Configuration:");
		String interchangesSource = getInterchangesSource();
		String additionalAnswersSource = getAdditionalAnswersSource();
		if (!interchangesSource.isEmpty()) {
			System.out.println("> " + interchangesSource);
		}
		if (!additionalAnswersSource.isEmpty()) {
			System.out.println("> " + additionalAnswersSource);
		}
		System.out.printf(
			"""
			> Questions and answers reversed: %b
			""", studyEngineConfiguration.getReverseQuestionsAndAnswers()
		);
		System.out.println();
	}

	private void displayMainPrompt() {
		System.out.println(getInterchangesSource());
		System.out.println(
				"""
				(n) Start New Study Session
				(c) Display Configuration
				(m) Modify Configuration
				(q) Quit
				""");
	}

	private String getInterchangesSource() {
		String interchangesSource;
		if (interchangeDao instanceof InterchangeFlatFileDao) {
			String[] pathToFiles = ((InterchangeFlatFileDao) interchangeDao).getPathToFiles();
			interchangesSource = "Using input file" + (pathToFiles.length > 1 ? "s" : "") + ": " + Arrays.toString(pathToFiles);
		}
		else if (interchangeDao instanceof InterchangeInMemoryDao) {
			interchangesSource = "No input files configured. Using default questions.";
		}
		else {
			interchangesSource = "";
		}
		return interchangesSource;
	}
	
	private String getAdditionalAnswersSource() {
		String additionalAnswerOptionsSource;
		if (answerDao instanceof AnswerFlatFileDao) {
			String[] pathToFiles = ((AnswerFlatFileDao) answerDao).getPathToFiles();
			additionalAnswerOptionsSource = "Additional answer options pulled from the following file" + (pathToFiles.length > 1 ? "s" : "") + ": " + Arrays.toString(pathToFiles);
		}
		else {
			additionalAnswerOptionsSource = "No files configured for additional answer options.";
		}
		return additionalAnswerOptionsSource;
	}

	private void displayNextInterchange(Interchange interchange) {
		System.out.printf("(%d of %d): ", (totalInterchanges - studyBoostEngine.getInterchanges().size() + 1), totalInterchanges);
		System.out.println(interchange.getQuestion().getQuestionText());

		List<Answer> answerChoices = interchange.getAnswerChoices();
		for (int i = 0; i < answerChoices.size(); i++) {
			System.out.println(i + ". " + answerChoices.get(i).getAnswerText());
		}
	}

}
