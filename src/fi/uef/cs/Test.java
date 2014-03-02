package fi.uef.cs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String word1 = "automobile";
		String word2 = "car";

		// get similarity
		double wordsimilarityValue = 0;
		double wordsimilarityValue1=0;
		double wordsimilarityValue2=0;
		double wordsimilarityValue3=0;
		double wordsimilarityValue4=0;
		WordnetSimilarity wSimilarity=new WordnetSimilarity();
		try {
			wordsimilarityValue = wSimilarity.getWordnetSimilarity(word1, word2, "n");
			wordsimilarityValue1 = wSimilarity.getWordnetSimilarity("cat", "dog", "n");
			wordsimilarityValue2=wSimilarity.getWordnetSimilarityAllByMeasure(word1, word2, "n", "1");
			wordsimilarityValue3=wSimilarity.getWordnetSimilarityByMeasure(word1, word2, "n", "3");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("The WordSimilarity of " + word1 + " and " + word2
				+ " is: " + wordsimilarityValue);
		System.out.println("The WordSimilarity1 of " + "cat" + " and " + "dog"
				+ " is: " + wordsimilarityValue1);
		System.out.println("The WordSimilarity2 of " + word1 + " and " + word2
				+ " is: " + wordsimilarityValue2);
		System.out.println("The WordSimilarity3 of " + word1 + " and " + word2
				+ " is: " + wordsimilarityValue3);
		
		//find the gloss in the wordnet
		HashMap<Integer, String> wordglossHashMap = new HashMap<Integer, String>();
		try {
			wordglossHashMap = WordnetSimilarity.getWordGloss(word1, "n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set set = wordglossHashMap.entrySet();
		Map.Entry<Integer, String> entry = null;
		Iterator it = set.iterator();

		System.out.println(word1 + " has the following sences: ");
		while (it.hasNext()) {
			entry = (Map.Entry<Integer, String>) it.next();

			System.out.println(entry.getKey() + "  " + entry.getValue());

		}

	}

}
