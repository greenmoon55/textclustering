package fi.uef.cs;

public class ShortTextSimilarity {
	private SimilarityMetric similarityMetric = new SimilarityMetric();
	
	class ShortTextComparison {
		public String str1, str2;
		public double similarity;
		ShortTextComparison(String str1, String str2, double similarity) {
			this.str1 = str1;
			this.str2 = str2;
			this.similarity = similarity;
		}
	}
	
	private double getMaxSimilarity(String word, String[] wordArray,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		double maxSimilarity = 0;
		for (String w : wordArray) {
			maxSimilarity = Math.max(maxSimilarity, this.similarityMetric
					.getSimilarity(w, word, method, type, firstSenseOnly));
		}
		return maxSimilarity;
	}

	public double getSimilarity(String[] wordArray1, String[] wordArray2,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		if (wordArray1.length < wordArray2.length) {
			String[] temp = wordArray1;
			wordArray1 = wordArray2;
			wordArray2 = temp;
		}
		double result = 0;
		for (String word : wordArray1) {
			result += getMaxSimilarity(word, wordArray2, method, type,
					firstSenseOnly);
		}
		return result/wordArray1.length;
	}
	
	
	
}
