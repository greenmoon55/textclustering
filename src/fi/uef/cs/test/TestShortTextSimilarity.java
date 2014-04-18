package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.SimilarityMetric;

public class TestShortTextSimilarity {

	/**
	 * @param args
	 */
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
		// TODO Auto-generated method stub
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		TestShortTextSimilarity testshortTextSimilarity = new TestShortTextSimilarity(); 
		
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
				comparisions.add(testshortTextSimilarity.new ShortTextComparison(data.get(i), data.get(j), similarity));
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
