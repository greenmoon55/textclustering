package fi.uef.cs;

public class ShortTextSimilarity {
	private SimilarityMetric similarityMetric = new SimilarityMetric();
	
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		String str1 = "train,railway,station";
		//String str2 = "Crews Track New Pulse Signals";
		String str2 = "cafe,pizza,restaurant";
		String[] str1Array = str1.split(",");
		String[] str2Array = str2.split(",");
		double result = shortTextSimilarity.getSimilarity(str1Array, str2Array, SimilarityMetric.Method.Jiang, "n", false);
		System.out.println(result);
	}

}
