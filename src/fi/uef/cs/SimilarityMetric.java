package fi.uef.cs;

import java.io.IOException;

public class SimilarityMetric {
	public enum Method {
		Jiang, Wu, Lin, Path, Levenshtein, QGrams, Cosine, Dice
	}
	
	private static WordnetSimilarity wSimilarity = new WordnetSimilarity();
	
	/**
	 * @param word1
	 * @param word2
	 * @param method
	 * @param type 1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein 6. q-grams 7. cosin 8. dice
	 * @return similarity compared with all senses of the two words
	 */
	public double getSimilarity(String word1, String word2, SimilarityMetric.Method method, String type){
		return getSimilarity(word1, word2, method, type, false);
	}
		
	/**
	 * @param word1
	 * @param word2
	 * @param method
	 * @param type 1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein 6. q-grams 7. cosin 8. dice
	 * @param firstSenseOnly
	 * @return
	 */
	public double getSimilarity(String word1, String word2,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		double similarityValue = 0;
		try {
			if (method == Method.Jiang || method == Method.Wu || method == Method.Lin || method == Method.Path) {
				similarityValue = wSimilarity.getWordnetSimilarity(word1,
						word2, type, method, firstSenseOnly);
				if (similarityValue > 1) {
					similarityValue = 1;
				} else if (similarityValue < 0) {
					similarityValue = 0;
				}

			} else if (method == Method.Levenshtein || method == Method.QGrams
					|| method == Method.Cosine || method == Method.Dice) {
				similarityValue = StringSimilarity.getStringSimilarity(word1,
						word2, method);

			} else {
				System.out.println("Sorry, this method doesn't exist!");
				similarityValue = -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return similarityValue;
	}

}
