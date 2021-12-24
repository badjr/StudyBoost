package com.badjr.studyboost;

import com.badjr.studyboost.dao.AnswerFlatFileDao;
import com.badjr.studyboost.dao.Dao;
import com.badjr.studyboost.dao.InterchangeFlatFileDao;
import com.badjr.studyboost.dao.InterchangeInMemoryDao;
import com.badjr.studyboost.engine.StudyEngineConfiguration;
import com.badjr.studyboost.engine.StudyEngineConfigurationBuilder;
import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class StudyBoost {
	public static void main(String[] args) {

		CommandLine commandLine = getCommandLine(args);

		StudyEngineConfiguration studyEngineConfiguration = createStudyEngineConfiguration(commandLine);

		StudyBoostCli studyBoostCli = new StudyBoostCli(studyEngineConfiguration);

		studyBoostCli.start();

	}

	private static StudyEngineConfiguration createStudyEngineConfiguration(CommandLine commandLine) {
		String[] pathToFiles = commandLine.getOptionValues("pathToInputFiles");

		Dao<Interchange> interchangeDao;
		if (pathToFiles != null) {
			interchangeDao = new InterchangeFlatFileDao(pathToFiles);
		}
		else {
			interchangeDao = new InterchangeInMemoryDao();
		}

		Boolean reverseQuestionsAndAnswers =
				commandLine.hasOption("reverseQuestionsAndAnswers") && Boolean.parseBoolean(commandLine.getOptionValue("reverseQuestionsAndAnswers"));

		StudyEngineConfigurationBuilder studyEngineConfigurationBuilder = StudyEngineConfigurationBuilder
				.aStudyEngineConfiguration()
				.withInterchangeDao(interchangeDao)
				.withReverseQuestionsAndAnswers(reverseQuestionsAndAnswers);

		if (commandLine.hasOption("pathToAdditionalAnswerOptionsFiles")) {
			Dao<Answer> additionalAnswerOptionsDao = new AnswerFlatFileDao(commandLine.getOptionValues("pathToAdditionalAnswerOptionsFiles"));
			studyEngineConfigurationBuilder.withAdditionalAnswerOptionsDao(additionalAnswerOptionsDao);
		}

		return studyEngineConfigurationBuilder.build();
	}

	private static CommandLine getCommandLine(String[] args) {
		Options options = new Options();

		Option pathToFilesOption = new Option("i", "pathToInputFiles", true, "Input file(s) with study questions/answers.");
		pathToFilesOption.setRequired(false);
		pathToFilesOption.setArgs(Option.UNLIMITED_VALUES);

		Option pathToAdditionalAnswerOptionsFilesOption = new Option("a", "pathToAdditionalAnswerOptionsFiles", true, "File(s) with additional answer options.");
		pathToAdditionalAnswerOptionsFilesOption.setRequired(false);
		pathToAdditionalAnswerOptionsFilesOption.setArgs(Option.UNLIMITED_VALUES);

		Option reverseQuestionAndAnswerOption = new Option("r", "reverseQuestionsAndAnswers", true, "Reverse questions and answers.");
		reverseQuestionAndAnswerOption.setRequired(false);

		options.addOption(pathToFilesOption);
		options.addOption(pathToAdditionalAnswerOptionsFilesOption);
		options.addOption(reverseQuestionAndAnswerOption);

		CommandLineParser commandLineParser = new DefaultParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		CommandLine commandLine = null;
		try {
			commandLine = commandLineParser.parse(options, args);
		}
		catch (ParseException e) {
			System.out.println(e.getMessage());
			helpFormatter.printHelp("StudyBoost*.jar", options);
			System.exit(1);
		}
		return commandLine;
	}

}
