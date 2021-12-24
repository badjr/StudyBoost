package com.badjr.studyboost.dao;

import com.badjr.studyboost.model.Answer;
import com.badjr.studyboost.model.Interchange;
import com.badjr.studyboost.model.Question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterchangeFlatFileDao implements Dao<Interchange> {

	private String[] pathToFiles;
	private List<Interchange> interchanges;

	public InterchangeFlatFileDao(String... pathToFiles) {
		this.pathToFiles = pathToFiles;
		interchanges = new ArrayList<>();
	}

	@Override
	public Optional<Interchange> get(long id) {
		return Optional.empty();
	}

	@Override
	public List<Interchange> getAll() {
		if (interchanges == null || interchanges.isEmpty()) {
			readInterchangesFromFiles(pathToFiles);
			return interchanges;
		}
		else {
			return interchanges;
		}
	}

	@Override
	public void save(Interchange interchange) {

	}

	@Override
	public void saveAll(List<Interchange> t) {

	}

	@Override
	public void update(Interchange interchange, String[] params) {

	}

	@Override
	public void delete(Interchange interchange) {

	}

	private void readInterchangesFromFiles(String... pathToFiles) {
		for (String pathToFile : pathToFiles) {
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile))) {
				String line;
				bufferedReader.readLine();
				while ((line = bufferedReader.readLine()) != null) {
					String[] splitLine = splitLineByComma(line);
					Interchange interchange = new Interchange(new Question(removeDoubleQuotes(splitLine[0])), new Answer(removeDoubleQuotes(splitLine[1])));
					interchange.setMaxWrongAnswers(4);
					interchanges.add(interchange);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * From https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes/1757107#1757107
	 */
	private String[] splitLineByComma(String line) {

		String otherThanQuote = " [^\"] ";
		String quotedString = String.format(" \" %s* \" ", otherThanQuote);
		String regex = String.format("(?x) "+ // enable comments, ignore white spaces
						",                         "+ // match a comma
						"(?=                       "+ // start positive look ahead
						"  (?:                     "+ //   start non-capturing group 1
						"    %s*                   "+ //     match 'otherThanQuote' zero or more times
						"    %s                    "+ //     match 'quotedString'
						"  )*                      "+ //   end group 1 and repeat it zero or more times
						"  %s*                     "+ //   match 'otherThanQuote'
						"  $                       "+ // match the end of the string
						")                         ", // stop positive look ahead
				otherThanQuote, quotedString, otherThanQuote);

		return line.split(regex, -1);
	}

	private String removeDoubleQuotes(String string) {
		return string.replace("\"", "");
	}

	public String[] getPathToFiles() {
		return pathToFiles;
	}

}
