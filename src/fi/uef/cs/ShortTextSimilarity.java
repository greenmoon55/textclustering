package fi.uef.cs;

import java.util.*;

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
	
	private double getMaxSimilarity(String word, List<String> wordArray,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		double maxSimilarity = 0;
		for (String w : wordArray) {
			maxSimilarity = Math.max(maxSimilarity, this.similarityMetric
					.getSimilarity(w, word, method, type, firstSenseOnly));
		}
		return maxSimilarity;
	}

	public double getSimilarity(List<String> wordArray1, List<String> wordArray2,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		if (wordArray1.size() < wordArray2.size()) {
			List<String> temp = wordArray1;
			wordArray1 = wordArray2;
			wordArray2 = temp;
		}
		double result = 0;
		for (String word : wordArray1) {
			result += getMaxSimilarity(word, wordArray2, method, type,
					firstSenseOnly);
		}
		return result/wordArray1.size();
	}
	
	public List<HashMap<List<List<String>>, Double>> HierachicalClustering(ArrayList<List<String>> data, SimilarityMetric.Method method) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = getSimilarity(data.get(i), data.get(j), method, "n", false);
			}
		}
		
		System.out.println("");
		return null;
	}
	
}
