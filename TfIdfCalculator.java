import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class TfIdfCalculator {

	// inner class for word and score (for use in pq)
	static class WordNode implements Comparable<WordNode> {
		String word;
		double score;

		WordNode(String w, Double s) {
			word = w;
			score = s;
		}

		// comparable
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

	// inverted index from words to documents
	static InvertedMap invertedMap = new InvertedMap();

	// map from document to total phrase count
	static HashMap<String, Integer> phraseCountMap = new HashMap<String, Integer>();

	// frequency map for every word in every page (rep. through document)
	static HashMap<String, HashMap<String, Integer>> wordFreqMap = new HashMap<String, HashMap<String, Integer>>();

	// size
	static int size;

	static void allDocumentAnalyzer() throws IOException {
		File allFiles = new File("."); // current directory
		File[] files = allFiles.listFiles(); // file array

		// recurse through all documents
		for (File doc : files) {

			// only goes with proper text files
			if (doc.getName().contains(".java") || doc.getName().contains(".class") || doc.getName().contains("Data")
					|| doc.getName().contains(".sh") || doc.isDirectory()) {
				continue;
			}

			// make readers for file
			FileReader fr = new FileReader(doc);
			BufferedReader br = new BufferedReader(fr);

			// make a list of words
			ArrayList<String> words = new ArrayList<String>();

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
					String currentWord = lineArray[i];

					currentWord = currentWord.trim();
					currentWord = refineWord(currentWord);

					if (currentWord.equals("") || currentWord.isEmpty()) {
						continue;
					}

					// add it to word list if exists
					words.add(currentWord);
				}
				nextLine = br.readLine();
			}

			br.close();

			if (words.size() == 0) {
				continue;
			}

			// updates size of corpus if has text
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

				// add this to record
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
		// all of the words for value retrieval
		Set<String> allWords = invertedMap.keySet();

		// for every word
		for (String word : allWords) {

			// get the total number of documents
			double totalSize = size;

			// get the collection containing that word
			Collection<File> containingWordSet = invertedMap.getValues(word);

			// makes a new one if null
			if (containingWordSet == null) {
				containingWordSet = new HashSet<File>();
			}

			// the number containing the word
			double numContainingWord = containingWordSet.size();

			// get the idf (log(total/(1 + |# contain word|)
			// normalized with 1
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

			// create node and add it to the priority queue
			WordNode w = new WordNode(word, wordScore);
			pq.add(w);
		}
	}

	static void printResults() throws IOException {
		File outfile = new File("Results.txt");
		FileWriter fw = new FileWriter(outfile);
		BufferedWriter bw = new BufferedWriter(fw);

		WordNode w = pq.poll();
		while (w != null) {
			bw.write(w.word + " " + w.score + "\n");
			w = pq.poll();
		}

		bw.close();
	}

	public static void main(String[] args) throws IOException {
		allDocumentAnalyzer();
		returnTfIdfResults();
		printResults();
	}
}