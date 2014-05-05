package fi.uef.cs;

import java.util.HashMap;
import java.io.*;
import java.net.URL;
import edu.sussex.nlp.jws.*;
import edu.mit.jwi.*;
import edu.mit.jwi.item.*;

public class WordnetSimilarity {

	static final String dir = "/usr/local/WordNet-3.0";
	static final String version = "3.0";

	JWS ws = new JWS(dir, "3.0");
	WuAndPalmer wup = ws.getWuAndPalmer();
	JiangAndConrath jiang = ws.getJiangAndConrath();
	
	public double getWordnetSimilarity(String word1, String word2, String type,
			SimilarityMetric.Method method) {
		return getWordnetSimilarity(word1, word2, type, method, false);
	}

	public double getWordnetSimilarity(String word1, String word2, String type,
			SimilarityMetric.Method method, boolean firstSenseOnly) {
		// 1 Jiang
		JiangAndConrath jiang = ws.getJiangAndConrath();
		// 2 Wu
		WuAndPalmer wu = ws.getWuAndPalmer();
		// 3 Lin
		Lin lin = ws.getLin();
		// 4 Path
		Path path = ws.getPath();
		double similarityValue = -1;
		if (firstSenseOnly) {
			if (method == SimilarityMetric.Method.Jiang) {
				similarityValue = jiang.jcn(word1, 1, word2, 1, type);
			} else if (method == SimilarityMetric.Method.Wu) {
				similarityValue = wup.wup(word1, 1, word2, 1, type);
			} else if (method == SimilarityMetric.Method.Lin) {
				similarityValue = lin.lin(word1, 1, word2, 1, type);
			} else if (method == SimilarityMetric.Method.Path) {
				similarityValue = path.path(word1, 1, word2, 1, type);
			}
		} else {
			if (method == SimilarityMetric.Method.Jiang) {
				similarityValue = jiang.max(word1, word2, type);
			} else if (method == SimilarityMetric.Method.Wu) {
				similarityValue = wu.max(word1, word2, type);
			} else if (method == SimilarityMetric.Method.Lin) {
				similarityValue = lin.max(word1, word2, type);
			} else if (method == SimilarityMetric.Method.Path) {
				similarityValue = path.max(word1, word2, type);
			} else {
				similarityValue = -1;
			}
		}
		return similarityValue;
	}


//	public static HashMap<Integer, String> getWordGloss(String keyword,
//			String type) throws IOException {
//		HashMap<Integer, String> glossMap = new HashMap<Integer, String>();
//		POS postype = null;
//
//		if (type.equalsIgnoreCase("n")) {
//			postype = POS.NOUN;
//		} else if (type.equalsIgnoreCase("v")) {
//			postype = POS.VERB;
//		} else if (type.equalsIgnoreCase("a")) {
//			postype = POS.ADJECTIVE;
//		} else if (type.equalsIgnoreCase("r")) {
//			postype = POS.ADVERB;
//		}
//
//		//String path = dir + File.separator + version + File.separator + "dict";
//		String path = dir;
//
//		URL url = new URL("file", null, path);
//
//		IDictionary dict = new Dictionary(url);
//		dict.open();
//
//		IIndexWord idxWord = dict.getIndexWord(keyword, postype);
//
//		IWordID wordID = null;
//
//		for (int i = 0;; i++) {
//			try {
//				wordID = idxWord.getWordIDs().get(i);
//				IWord word = dict.getWord(wordID);
//				glossMap.put(i, word.getSynset().getGloss());
//			} catch (ArrayIndexOutOfBoundsException e) {
//				break;
//			}
//		}
//		return glossMap;
//	}

}
