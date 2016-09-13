import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CompanyScorer {

	// map from person name to word count
	static Map<String, Integer> individualWordCount = new HashMap<String, Integer>();

	// map from company name to set of people
	static Map<String, Set<String>> companyEmployees = new HashMap<String, Set<String>>();

	// set of keywords from phrases by keyword
	static ArrayList<String> phraseList = new ArrayList<String>();

	// set of keywords from phrases by keyword
	static ArrayList<String> wordList = new ArrayList<String>();

	// set of network names
	static Set<String> names = new HashSet<String>();

	// set of network names
	static Set<String> generalWordSet = new HashSet<String>();

	static void uniqueWordChecker() throws IOException {
		File allFiles = new File("."); // current directory
		File[] files = allFiles.listFiles(); // file array

		// recurse through all documents
		for (File doc : files) {
			if (doc.getName().contains(".java") || doc.getName().contains(".class") || doc.getName().contains("Data")
					|| doc.getName().contains(".sh") || doc.isDirectory()) {
				continue;
			}

			if (doc.getName().contains("phrases_by")) {
				continue;
			}

			if (!doc.getName().contains("_parsed.txt")) {
				continue;
			}

			FileReader fr = new FileReader(doc);
			BufferedReader br = new BufferedReader(fr);

			ArrayList<String> phrases = new ArrayList<String>();

			// retrieve all text
			String nextLine = br.readLine();
			while (nextLine != null) {
				nextLine = nextLine.replace("\n", " ");
				nextLine = nextLine.trim();

				if (nextLine.contains("no experience listed")) {
					break;
				}

				String[] lineArray = nextLine.split("\\s+");

				// recurse through every word
				for (int i = 0; i < lineArray.length - 1; i++) {
					// get the current word and refine
					String firstWord = lineArray[i];
					String secondWord = lineArray[i + 1];

					firstWord = firstWord.trim();
					secondWord = secondWord.trim();

					firstWord = refineWord(firstWord);
					secondWord = refineWord(secondWord);

					// System.out.println(firstWord);
					// System.out.println(secondWord);

					if (firstWord.equals("") || secondWord.equals("") || firstWord.isEmpty() || secondWord.isEmpty()) {
						continue;
					}

					String currentWord = firstWord + "-" + secondWord;
					phrases.add(currentWord);
				}
				nextLine = br.readLine();
			}

			br.close();

			int score = 0;

			for (String phrase : phrases) {
				phrase = phrase.trim().toLowerCase();
				String[] wordArray = phrase.split("-");
				String firstWord = wordArray[0];
				String secondWord = wordArray[1];

				firstWord = firstWord.trim();
				secondWord = secondWord.trim();

				for (String word : wordList) {
					if (word.equals(firstWord)) {
                        System.out.println(firstWord);
						score++;
                    } else if(word.equals(secondWord)){
                        System.out.println(secondWord);
                        score++;
                    }
				}

				for (String keyword : phraseList) {
					keyword = keyword.trim().toLowerCase();
					if (phrase.equals(keyword)) {
                        System.out.println(keyword);
						score++;
					}
				}
			}
			String name = doc.getName();
			CharSequence justName = name.subSequence(0, name.length() - 11);
			individualWordCount.put(justName.toString(), score);
		}
	}

	// takes out non-letter characters and makes lowercase
	static String refineWord(String currentWord) {
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

	static void keywordRetriever() throws IOException {
		File phrasesDoc = new File("phrases.txt");
		File wordDoc = new File("words.txt");

		FileReader phrasesFr = new FileReader(phrasesDoc);
		BufferedReader phrasesBr = new BufferedReader(phrasesFr);

		FileReader wordsFr = new FileReader(wordDoc);
		BufferedReader wordsBr = new BufferedReader(wordsFr);

		String nextLine = phrasesBr.readLine();
		while (nextLine != null) {
			nextLine = nextLine.trim();
			String[] lineArray = nextLine.split("\\s+");
			String keyword = lineArray[0];
			phraseList.add(keyword);
			nextLine = phrasesBr.readLine();
		}
		phrasesBr.close();

		nextLine = wordsBr.readLine();
        
		while (nextLine != null) {
			nextLine = nextLine.trim();
			String[] lineArray = nextLine.split("\\s+");
			String keyword = lineArray[0];
			wordList.add(keyword);
			nextLine = wordsBr.readLine();
		}
		wordsBr.close();
	}

	public static void main(String[] args) {
		try {
			keywordRetriever();
			uniqueWordChecker();
			networkNameFinder();

			ArrayList<String> zeroCount = new ArrayList<String>();
			ArrayList<String> somethingCount = new ArrayList<String>();
			ArrayList<String> nullCount = new ArrayList<String>();

			for (String name : names) {
				if (individualWordCount.get(name) == null) {
					nullCount.add(name);
					continue;
				}
				if (individualWordCount.get(name) == 0) {
					zeroCount.add(name);
				} else {
					somethingCount.add(name);
				}
			}

			System.out.println("Null " + nullCount.size());
			System.out.println("Zero " + zeroCount.size());
			System.out.println("Positive Score " + somethingCount.size());
			
			zeroChecker(zeroCount);
			
			File namesFile = new File("names_without_keywords.txt");
			FileWriter fw = new FileWriter(namesFile); 
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(String name : zeroCount){
				bw.write(name + "\n");
			}
			
			for(String name : nullCount){
				bw.write(name + "\n");
			}
			
			bw.close();
			System.out.println(generalWordSet.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void zeroChecker(ArrayList<String> names) throws IOException {
		File allFiles = new File("."); // current directory
		File[] files = allFiles.listFiles(); // file array

		// recurse through all documents
		for (File doc : files) {
			if (doc.getName().contains(".java") || doc.getName().contains(".class") || doc.getName().contains("Data")
					|| doc.getName().contains(".sh") || doc.isDirectory()) {
				continue;
			}

			if (doc.getName().contains("phrases_by")) {
				continue;
			}

			if (!doc.getName().contains("_parsed.txt")) {
				continue;
			}

			String name = doc.getName();
			CharSequence justName = name.subSequence(0, name.length() - 11);
			name = justName.toString();

			if (!names.contains(name)) {
				continue;
			}

			FileReader fr = new FileReader(doc);
			BufferedReader br = new BufferedReader(fr);
			String text = "";

			// retrieve all text
			String nextLine = br.readLine();
			while (nextLine != null) {
				text += nextLine;
				nextLine = br.readLine();
			}
			br.close();

			if (text.contains("data")) {
				generalWordSet.add(name);
			} else if (text.contains("analytic")) {
				generalWordSet.add(name);
			} else if (text.contains("programming")){
				generalWordSet.add(name);
			} else if (text.contains("coding")){
				generalWordSet.add(name);
			}
		}

	}

	private static void networkNameFinder() throws IOException {
		File doc = new File("UpdatedNetworkMovements.txt");
		FileReader fr = new FileReader(doc);
		BufferedReader br = new BufferedReader(fr);

		// retrieve all text
		String nextLine = br.readLine();
		while (nextLine != null) {
			nextLine = nextLine.replace("\n", " ");
			nextLine = nextLine.trim();

			String[] lineArray = nextLine.split("\\$");
			String name = lineArray[4];
			name = name.trim();
			names.add(name);
			nextLine = br.readLine();
		}

		System.out.println("NUMBER OF TOTAL NAMES " + names.size());

		br.close();
	}
}
