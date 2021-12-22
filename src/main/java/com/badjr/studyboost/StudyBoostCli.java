package com.badjr.studyboost;

import com.badjr.studyboost.engine.StudyBoostEngine;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;

import java.util.List;
import java.util.Scanner;

public class StudyBoostCli {

	private List<Interchange> interchanges;
	private List<Answer> additionalAnswerChoices;
	private StudyBoostEngine studyBoostEngine;

	public StudyBoostCli(List<Interchange> interchanges, StudyBoostEngine studyBoostEngine) {
		this.interchanges = interchanges;
		this.studyBoostEngine = studyBoostEngine;
	}

	public StudyBoostCli(List<Interchange> interchanges, List<Answer> additionalAnswerChoices, StudyBoostEngine studyBoostEngine) {
		this.interchanges = interchanges;
		this.additionalAnswerChoices = additionalAnswerChoices;
		this.studyBoostEngine = studyBoostEngine;
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
					studyBoostEngine.loadStudySession(interchanges, additionalAnswerChoices);
					startStudySession(scanner, studyBoostEngine);
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

	private void displayMainPrompt() {
		System.out.println(
                """
				(n) Start New Study Session
				(q) Quit
				""");
	}

	private void displayNextInterchange(Interchange interchange) {
		System.out.println(interchange.getQuestion().getQuestionText());

		List<Answer> answerChoices = (List<Answer>) interchange.getAnswerChoices();
		for (int i = 0; i < answerChoices.size(); i++) {
			System.out.println(i + ". " + answerChoices.get(i).getAnswerText());
		}
	}

}
