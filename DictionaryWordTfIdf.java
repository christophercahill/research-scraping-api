import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class DictionaryWordTfIdf {
	// static class that contains the score and word (phrase)
	static class WordNode implements Comparable<WordNode> {
		String word;
		double score;

		WordNode(String w, Double s) {
			word = w;
			score = s;
		}

		// comparable built in
		@Override
		public int compareTo(WordNode o) {
			double otherScore = o.score;
			double thisScore = score;

			if (thisScore > otherScore) {
				return 1;
			} else if (thisScore == otherScore) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	// makes priority queue of highest values
	static PriorityQueue<WordNode> pq = new PriorityQueue<WordNode>();

	// set of pertinent names for c-level network
	static Set<String> names = new HashSet<String>();

	// inverted index from words to documents (in other words, a map from word
	// to documents it's in)
	static InvertedMap invertedMap = new InvertedMap();

	// map from document to total phrase count in document
	static HashMap<String, Integer> phraseCountMap = new HashMap<String, Integer>();

	// frequency map for every word in every page (rep. through document)
	// map from document to map from word to frequency
	static HashMap<String, HashMap<String, Integer>> wordFreqMap = new HashMap<String, HashMap<String, Integer>>();

	// total number
	static int size;

	// helper function that retrieves phrases
	static void allDocumentAnalyzer() throws IOException {
		File allFiles = new File("."); // current directory
		File[] files = allFiles.listFiles(); // file array

		// recurse through all documents
		for (File doc : files) {
			// other files we don't need
			if (doc.getName().contains(".java") || doc.getName().contains("words") || doc.getName().contains("names")
					|| doc.getName().contains("phrases") || doc.getName().contains(".class")
					|| doc.getName().contains("Data") || doc.getName().contains(".sh") || doc.isDirectory()
					|| !doc.getName().contains(".txt")) {
				continue;
			}

			String name = doc.getName();
			System.out.println(name);
			name = name.substring(0, name.length() - 11);
			System.out.println(name);

			if (!names.contains(name)) {
				continue;
			}

			// make readers
			FileReader fr = new FileReader(doc);
			BufferedReader br = new BufferedReader(fr);

			// phrase list
			ArrayList<String> words = new ArrayList<String>();

			// retrieve all text, trim, refine and add to phrase list
			String nextLine = br.readLine();
			while (nextLine != null) {
				nextLine = nextLine.replace("\n", " ");
				nextLine = nextLine.trim();

				if (nextLine.contains("no experience listed")) {
					break;
				}

				String[] lineArray = nextLine.split("\\s+");

				// recurse through every word to find phrases
				for (int i = 0; i < lineArray.length - 1; i++) {
					// get the current word and refine
					String currentWord = lineArray[i];

					currentWord = currentWord.trim();
					currentWord = refineWord(currentWord);

					if (currentWord.equals("") || currentWord.isEmpty()) {
						continue;
					}

					words.add(currentWord);
				}
				nextLine = br.readLine();
			}

			br.close();

			// continue if empty
			if (words.size() == 0) {
				continue;
			}

			// otherwise, increment number of files in corpus
			size++;

			// updating the phrase count map for tf
			String fileName = doc.getName();
			phraseCountMap.put(fileName, words.size());

			// recurse through every word
			for (String word : words) {
				// get map from string to freq
				HashMap<String, Integer> textFreqMap = wordFreqMap.get(fileName);

				// if it's null, make one
				if (textFreqMap == null) {
					textFreqMap = new HashMap<String, Integer>();
					// make freq as 1
					textFreqMap.put(word, 1);
					// put that in wordFreq
					wordFreqMap.put(fileName, textFreqMap);
				} else {
					// otherwise, get the current num
					Integer currentFreq = textFreqMap.get(word);

					// if it's null,
					if (currentFreq == null) {
						// the frequency is just 0
						currentFreq = 0;
					}

					// increment the frequency
					currentFreq++;

					// put it in the textFreqMap
					textFreqMap.put(word, currentFreq);

					// put that in the wordFreqMap
					wordFreqMap.put(fileName, textFreqMap);
				}

				// add this to record (map from phrases to docs with that
				// phrase)
				invertedMap.addValue(word, doc);
			}
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

	static void returnTfIdfResults() {
		// all of the phrases for value retrieval
		Set<String> specificWords = findKeywords();

		// for every phrase
		for (String word : specificWords) {
			// get the total number of documents
			double totalSize = size;

			// get the collection of documents containing that word
			Collection<File> containingWordSet = invertedMap.getValues(word);

			// makes a new one if null (for ease of code)
			if (containingWordSet == null) {
				containingWordSet = new HashSet<File>();
			}

			// the number containing the word
			double numContainingWord = containingWordSet.size();

			// get the idf (log(total/(1 + |# contain word|)
			// it is normalize with 1 to prevent division by 0
			double idf = Math.log(totalSize / (1 + numContainingWord));

			// System.out.println("------------------");
			// System.out.println(word + " totalSize " + totalSize);
			// System.out.println(word + " numContainingWord " +
			// numContainingWord);
			// System.out.println(word + " idf " + idf);
			// System.out.println("------------------");

			// set the wordscore to 0
			double wordScore = 0;

			// for all of the files with the word
			for (File file : containingWordSet) {
				String fileName = file.getName();

				// get the phrase count for this document
				Integer phraseCount = phraseCountMap.get(fileName);
				double numPhrases = phraseCount.doubleValue();

				// get the word frequency map for this page
				HashMap<String, Integer> docFreqMap = wordFreqMap.get(fileName);
				Integer freq = docFreqMap.get(word);

				// otherwise, get the tf
				double tf;
				if (freq == null) {
					// if it's not present, it's 0
					tf = 0;
				} else {
					// otherwise, it's the value
					tf = freq / numPhrases;
					// System.out.println(tf);
				}

				// multiply for this score
				double score = tf * idf;

				// add it to the page score
				wordScore += score;
			}

			// make a node with the sum of tf-idf for all relevant phrases and
			// add to pq
			WordNode w = new WordNode(word, wordScore);
			pq.add(w);
		}
	}

	private static Set<String> findKeywords() {
		Set<String> inputPhrases = new HashSet<String>();
		try {
			File input = new File("words.txt");
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			String nextLine = br.readLine();
			while (nextLine != null) {
				nextLine = nextLine.trim();
				String[] phraseArr = nextLine.split("\\s+");
				inputPhrases.add(phraseArr[0]);
				nextLine = br.readLine();

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return inputPhrases;
	}

	private static void nameGenerator() {
		try {
			File input = new File("names.txt");
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			String nextLine = br.readLine();
			while (nextLine != null) {
				nextLine = nextLine.trim();
				names.add(nextLine);
				nextLine = br.readLine();

			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void printResults() {
		WordNode w = pq.poll();
		while (w != null) {
			System.out.println(w.word + " " + w.score);
			w = pq.poll();
		}
	}

	public static void main(String[] args) throws IOException {
		nameGenerator();
		allDocumentAnalyzer();
		returnTfIdfResults();
		printResults();
	}
}