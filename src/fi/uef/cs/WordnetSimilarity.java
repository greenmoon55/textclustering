package fi.uef.cs;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.*;
import edu.sussex.nlp.jws.*;
import edu.mit.jwi.*;
import edu.mit.jwi.data.*;
import edu.mit.jwi.data.compare.*;
import edu.mit.jwi.data.parse.*;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.*;

public class WordnetSimilarity {

	static final String dir = "D:/Software/WordNet";
	static final String version = "2.1";

	File directory = new File("");
	// String dir= directory.getAbsolutePath()+"/WordNet";

	JWS ws = new JWS(dir, "2.1");
	WuAndPalmer wup = ws.getWuAndPalmer();
	JiangAndConrath jiang = ws.getJiangAndConrath();

	public double getWordnetSimilarity(String word1, String word2, String type,
			int method, boolean firstSenseOnly) {
		// 1 Jiang
		JiangAndConrath jiang = ws.getJiangAndConrath();
		// 2 Wu
		WuAndPalmer wu = ws.getWuAndPalmer();
		// 3 Lin
		Lin lin = ws.getLin();
		// 4 Path
		Path path = ws.getPath();
		if (firstSenseOnly) {
			if (method == 1) {
				double similarityValue = jiang.jcn(word1, 1, word2, 1,
						type);
				return similarityValue;
			} else if (method == 2) {
				double similarityValue = lin.lin(word1, 1, word2, 1, type);
				return similarityValue;
			} else if (method == 3) {
				double similarityValue = path.path(word1, 1, word2, 1, type);
				return similarityValue;
			} else if (method == 4) {
				double similarityValue = wup.wup(word1, 1, word2, 1, type);
				return similarityValue;
			} else {
				return 0;
			}
		} else {
			double similarityValue = 0;
			if (method == 1) {
				similarityValue = jiang.max(word1, word2, type);
				return similarityValue;
			} else if (method == 2) {
				similarityValue = wu.max(word1, word2, type);
				return similarityValue;
			} else if (method == 3) {
				similarityValue = lin.max(word1, word2, type);
				return similarityValue;
			} else if (method == 4) {
				similarityValue = path.max(word1, word2, type);
				return similarityValue;
			} else {
				similarityValue = -1;
				return similarityValue;
			}
		}
	}

	/*
	 * get the similarity by just comparing the all the meaning of two words by
	 * five different measure 1. JiangAndConrath 2. Lin 3. Path 4. WuAndPalmer
	 */
	public double getWordnetSimilarityByMeasure(String word1, String word2,
			String type, String measure) {

		if (measure.trim().equalsIgnoreCase("1")) {
			JiangAndConrath jiangAndConrath = ws.getJiangAndConrath();
			double similarityValue = jiangAndConrath.jcn(word1, 1, word2, 1,
					type);
			return similarityValue;
		} else if (measure.trim().equalsIgnoreCase("2")) {
			Lin lin = ws.getLin();
			double similarityValue = lin.lin(word1, 1, word2, 1, type);
			return similarityValue;
		} else if (measure.trim().equalsIgnoreCase("3")) {
			Path path = ws.getPath();
			double similarityValue = path.path(word1, 1, word2, 1, type);
			return similarityValue;
		} else if (measure.trim().equalsIgnoreCase("4")) {
			double similarityValue = wup.wup(word1, 1, word2, 1, type);
			return similarityValue;
		} else {
			return 0;
		}
	}

	/*
	 * get the similarity by just comparing the first the meaning of two words
	 * by five different measure 1. JiangAndConrath 2. Lin 3. Path 4.
	 * WuAndPalmer
	 */
	public double getWordnetSimilarityAllByMeasure(String word1, String word2,
			String type, String measure) {

		if (measure.trim().equalsIgnoreCase("1")) {
			JiangAndConrath jiangAndConrath = ws.getJiangAndConrath();
			double similarityValue = jiangAndConrath.max(word1, word2, type);
			return similarityValue;
		} else if (measure.trim().equalsIgnoreCase("2")) {
			Lin lin = ws.getLin();
			double similarityValue = lin.max(word1, word2, type);
			return similarityValue;
		} else if (measure.trim().equalsIgnoreCase("3")) {
			Path path = ws.getPath();
			double similarityValue = path.max(word1, word2, type);
			return similarityValue;

		} else if (measure.trim().equalsIgnoreCase("4")) {

			double similarityValue = wup.max(word1, word2, type);
			return similarityValue;
		} else {
			return 0;
		}
	}

	public <E> double getWordDistanceAll(String word1, String word2, String type) {

		// JiangAndConrath wup=ws.getJiangAndConrath();

		// TreeMap<String, Double> scores1 = jcn.jcn("apple", "banana", "n"); //
		// all

		// double similarityValue = wup.max(word1, word2, type);
		double wordsimilarityValue = wup.max(word1.toString(),
				word2.toString(), type);
		double worddistanceValue = 1 - wordsimilarityValue;
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		double distacneValue = Double.parseDouble(df
				.format(worddistanceValue * 100));
		return distacneValue;
	}

	public static HashMap<Integer, String> getWordGloss(String keyword,
			String type) throws IOException {
		HashMap<Integer, String> glossMap = new HashMap<Integer, String>();
		POS postype = null;

		String wordtype = null;
		if (type.equalsIgnoreCase("n")) {
			postype = POS.NOUN;
			wordtype = "NOUN";
		} else if (type.equalsIgnoreCase("v")) {
			postype = POS.VERB;
			wordtype = "VERB";
		} else if (type.equalsIgnoreCase("a")) {
			postype = POS.ADJECTIVE;
			wordtype = "ADJECTIVE";
		} else if (type.equalsIgnoreCase("r")) {
			postype = POS.ADVERB;
			wordtype = "ADVERB";
		}

		String path = dir + File.separator + version + File.separator + "dict";
		// String path = "D:/WordNet-3.0" + File.separator + "dict";

		URL url = new URL("file", null, path);

		IDictionary dict = new Dictionary(url);
		dict.open();

		IIndexWord idxWord = dict.getIndexWord(keyword, postype);

		IWordID wordID = null;

		for (int i = 0;; i++) {
			try {
				wordID = idxWord.getWordIDs().get(i);
				IWord word = dict.getWord(wordID);
				glossMap.put(i, word.getSynset().getGloss());
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}

		}
		return glossMap;
	}

}
