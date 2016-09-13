import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreFinder {
	// list of inner classes with details and description of work
	static ArrayList<ExperienceDetails> experienceDetails = new ArrayList<ExperienceDetails>();
	static ArrayList<ExperienceDescription> experienceDescription = new ArrayList<ExperienceDescription>();

	// list of words from tfidf and papers
	static Map<String, Double> words = new HashMap<String, Double>();

	// list of phrases from tfidf and papers like saunders/tambe
	static Map<String, Double> phrases = new HashMap<String, Double>();

	// list of all files
	static ArrayList<File> files;

	// current lines being read
	static BufferedReader br;
	static String firstLine;
	static String secondLine;
	static String thirdLine;

	// inner class with details of position experience, as shown
	static class ExperienceDetails {
		String position;
		String company;
		int start;
		int end;

		public ExperienceDetails(String position, String company, int startYear, int endYear) {
			this.position = position;
			this.company = company;
			this.start = startYear;
			this.end = endYear;
		}
	}

	// inner class with text
	static class ExperienceDescription {
		String text;

		public ExperienceDescription(String desc) {
			this.text = desc;
		}
	}

	// score map
	static class ExperienceScore {
		int lowestYear;
		Map<Integer, Double> scoreMap = new HashMap<Integer, Double>();
	}

	public static void main(String[] args) throws IOException {
		// set up files
		setUp();

		// get words
		wordSetUp();

		// get phrases
		phrasesSetUp();

		// recurse through files
		for (File f : files) {

			if (!f.getName().contains(".txt") || f.getName().contains("Data") || f.getName().contains("_parsed.txt")
					|| f.getName().contains("position") || f.getName().contains("score") || f.isDirectory()) {
				continue;
			}

			// System.out.println("Name " + f.getName());

			// initializes file reader
			FileReader fr;
			try {
				// set up file reader/buffered reader
				fr = new FileReader(f);
				br = new BufferedReader(fr);

				// first line is experience
				br.readLine();

				// retrieve all information
				findNextExperience();

				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// make a new score file
			String fileName = f.getName();
			fileName = fileName.substring(0, fileName.length() - 4);
			fileName = fileName + "_weighted_score.txt";

			// make a new Score dir
			File dir = new File("WeightedScore");
			if (!dir.exists()) {
				try {
					dir.mkdir();
				} catch (Exception e) {
					System.out.println(e);
				}
			}

			// make outfile
			File outfile = new File("WeightedScore/" + fileName);

			// set up writer
			FileWriter fw = new FileWriter(outfile);
			BufferedWriter bw = new BufferedWriter(fw);

			// recurse through all experiences by getting number of exp.
			int detailsLength = experienceDetails.size();

			// System.out.println(detailsLength);
			// System.out.println(descriptionLength);

			// get the lowest year (the start year)
			ExperienceScore sc = new ExperienceScore();
			sc.lowestYear = Integer.MAX_VALUE;

			// loop of experiences
			for (int i = 0; i < detailsLength; i++) {
				// get current details and description
				ExperienceDetails details = experienceDetails.get(i);
				ExperienceDescription description = experienceDescription.get(i + 1);

				// obtain text
				String text = description.text;

				// if it contains a word/phrase
				if (phraseChecker(text) > 0) {
					double score = phraseChecker(text);

					// check and see if it's the lowest year (update)
					if (details.start < sc.lowestYear) {
						sc.lowestYear = details.start;
					}

					// get the duration in years
					int duration = details.end - details.start;

					// get the starting year
					int currentYear = details.start;

					// for every year on the job, add to the experience score
					for (int j = 0; j <= duration; j++) {
						// adds one year for every year after too
						int time = 2015 - currentYear;
						for (int k = 0; k <= time; k++) {
							// set the score as 1 or increment it by 1
							Double yearScore = sc.scoreMap.get(currentYear + k);
							if (yearScore == null) {
								sc.scoreMap.put(currentYear + k, score);
							} else {
								yearScore = yearScore + score;
								sc.scoreMap.put(currentYear + k, yearScore);
							}
						}
						// increment the current year
						currentYear++;
					}
				}
			}

			// get the lowest year
			int startYear = sc.lowestYear;
			// for all years, print out the score
			int duration = 2015 - startYear;
			for (int i = 0; i <= duration; i++) {
				bw.write((startYear + i) + " " + sc.scoreMap.get(startYear + i) + "\n");
			}
			bw.close();
			// reset experiences
			experienceDescription = new ArrayList<ExperienceDescription>();
			experienceDetails = new ArrayList<ExperienceDetails>();
		}
	}

	private static void findNextExperience() throws IOException {
		thirdLine = br.readLine();
		secondLine = "";
		firstLine = "";

		// experiences is empty
		String experiences = "";

		// recurse through until Skills or no skills found
		while (thirdLine != null && !thirdLine.trim().equals("Skills")
				&& !firstLine.toLowerCase().equals("no skills found")) {

			// line for determining if it's an endline (has a date)
			if ((thirdLine.contains(" Present") || thirdLine.contains("year") || thirdLine.contains("January")
					|| thirdLine.contains("February") || thirdLine.contains("March") || thirdLine.contains("April")
					|| thirdLine.contains("May") || thirdLine.contains("June") || thirdLine.contains("July")
					|| thirdLine.contains("August") || thirdLine.contains("September") || thirdLine.contains("October")
					|| thirdLine.contains("November") || thirdLine.contains("December")) && thirdLine.contains("–")
					&& thirdLine.split("–")[0].trim().length() > 0 && thirdLine.length() < 200) {

				try {
					String[] date = thirdLine.split("–");
					String startDate = date[0];
					String endDate = date[1];
					startDate = startDate.trim();
					endDate = endDate.trim();
					String[] endArr = endDate.split("\\(");
					endDate = endArr[0];
					endDate = endDate.trim();

					// get the dates from text
					if (startDate.length() != 4) {
						startDate = startDate.substring(startDate.length() - 4);
					}

					if (endDate.contains("Present")) {
						endDate = "2015";
					} else if (endDate.length() != 4) {
						endDate = endDate.substring(endDate.length() - 4);
					}

					// get the years
					Integer s = Integer.parseInt(startDate);
					Integer e = Integer.parseInt(endDate);

					// add the details
					ExperienceDetails details = new ExperienceDetails(firstLine.trim(), secondLine.trim(), s, e);
					experienceDetails.add(details);
				} catch (Exception e) {
					// otherwise, add to the experiences and keep going
					experiences += firstLine;

					firstLine = secondLine;
					secondLine = thirdLine;
					thirdLine = br.readLine();
					continue;
				}

				// if it reaches here, it's in the if statement
				// add the text that's been retrieved so far
				ExperienceDescription desc = new ExperienceDescription(experiences);
				experienceDescription.add(desc);
				// reset the string
				experiences = "";

				// if so, third line is date
				// second line is company
				// first line is position
				thirdLine = br.readLine();
				secondLine = "";
				firstLine = "";
			}

			// otherwise keep going
			experiences += firstLine;

			firstLine = secondLine;
			secondLine = thirdLine;
			thirdLine = br.readLine();
		}
		// add the last one, too
		ExperienceDescription desc = new ExperienceDescription(experiences);
		experienceDescription.add(desc);
	}

	// helper function to get files
	private static void setUp() {
		// makes list of files
		files = new ArrayList<File>();
		// gets file directory with experiences
		File currentDir = new File(".");
		// puts in the list
		File[] arrayOfFiles = currentDir.listFiles();
		for (int i = 0; i < arrayOfFiles.length; i++) {
			files.add(arrayOfFiles[i]);
		}
	}

	// helper function to get word list
	private static void wordSetUp() {
		File wordFile = new File("words.txt");
		try {
			FileReader fr = new FileReader(wordFile);
			BufferedReader br = new BufferedReader(fr);
			String nextLine = br.readLine();
			while (nextLine != null) {
				// get the next word and trim
				nextLine = nextLine.trim();
				String[] wordScoreArr = nextLine.split("\\s+");
				String word = wordScoreArr[0];
				String scoreWord = wordScoreArr[1];
				double score = Double.parseDouble(scoreWord);
				// add if it's a real word
				if (!word.isEmpty() && !word.equals("")) {
					words.put(word, score);
				}
				nextLine = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// helper function to get phrase list
	private static void phrasesSetUp() {
		File wordFile = new File("phrases.txt");
		try {
			FileReader fr = new FileReader(wordFile);
			BufferedReader br = new BufferedReader(fr);
			String nextLine = br.readLine();
			while (nextLine != null) {
				// get the next word and trim
				nextLine = nextLine.trim();
				String[] phraseScoreArr = nextLine.split("\\s+");
				String phrase = phraseScoreArr[0];
				String scoreWord = phraseScoreArr[1];
				double score = Double.parseDouble(scoreWord);

				// get the phrase and trim
				phrase = phrase.trim();
				// if it's a real phrase, add
				if (!phrase.isEmpty() && !phrase.equals("")) {
					phrases.put(phrase, score);
				}
				nextLine = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String refineWord(String currentWord) {
		// makes string builder of word
		StringBuilder builder = new StringBuilder(currentWord);
		StringBuilder newWord = new StringBuilder();

		// goes through; if it's not a letter, doesn't add to the new word
		for (int i = 0; i < builder.length(); i++) {
			char currentChar = builder.charAt(i);
			if (Character.isLetter(currentChar)) {
				newWord.append(currentChar);
			}
		}

		// returns lower case
		return newWord.toString().toLowerCase().trim();
	}

	// helper function to see if relevant word/phrase appears in text
	private static double phraseChecker(String text) {
		double score = 0;

		String[] textArray = text.split("\\s+");

		// recurse through every word
		for (int i = 0; i < textArray.length - 1; i++) {
			// get the current two words and refine
			String firstWord = textArray[i];
			String secondWord = textArray[i + 1];

			firstWord = firstWord.trim();
			secondWord = secondWord.trim();

			firstWord = refineWord(firstWord);
			secondWord = refineWord(secondWord);

			for (String word : words.keySet()) {
				if (word.equals(firstWord)) {
					score = score + words.get(firstWord);
				}

				if (word.equals(secondWord)) {
					score = score + words.get(secondWord);
				}
			}

			if (firstWord.isEmpty() || secondWord.isEmpty() || firstWord.equals("") || secondWord.equals("")) {
				continue;
			}

			String currentWord = firstWord + "-" + secondWord;
			currentWord = currentWord.trim();

			for (String phrase : phrases.keySet()) {
				phrase = phrase.trim().toLowerCase();
				if (phrase.equals(currentWord)) {
					score = score + phrases.get(currentWord);
				}
			}
		}

		return score;
	}
}