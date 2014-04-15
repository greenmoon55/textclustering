package fi.uef.cs;

import java.io.*;
import java.util.*;

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
	
	class ShortTextComparison {
		public String str1, str2;
		public double similarity;
		ShortTextComparison(String str1, String str2, double similarity) {
			this.str1 = str1;
			this.str2 = str2;
			this.similarity = similarity;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		
		Scanner in = new Scanner(new FileReader("data.txt"));
		ArrayList<String> data = new ArrayList<String>();
		while (in.hasNext()) {
			data.add(in.nextLine());
		}
		
		ArrayList<ShortTextComparison> comparisions = new ArrayList<ShortTextComparison>(data.size() * (data.size() - 1) / 2);
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				String[] str1Array = data.get(i).split(",");
				String[] str2Array = data.get(j).split(",");
				double similarity = shortTextSimilarity.getSimilarity(str1Array, str2Array,
						SimilarityMetric.Method.Jiang, "n", false);
				comparisions.add(shortTextSimilarity.new ShortTextComparison(data.get(i), data.get(j), similarity));
			}
		}
		
		Collections.sort(comparisions, new Comparator<ShortTextComparison>(){
			public int compare(ShortTextComparison c1, ShortTextComparison c2) {
				return Double.compare(c1.similarity, c2.similarity);
			}
		});	
		
		for (ShortTextComparison comparison: comparisions) {
			System.out.println(comparison.str1 + " " + comparison.str2 + ": " + comparison.similarity);
		}
		
		
//		String str1 = "train,railway,station";
//		String str2 = "cafe,pizza,restaurant";
//		String[] str1Array = str1.split(",");
//		String[] str2Array = str2.split(",");
//		double result = shortTextSimilarity.getSimilarity(str1Array, str2Array, SimilarityMetric.Method.Jiang, "n", false);
//		System.out.println(result);
	}

}
