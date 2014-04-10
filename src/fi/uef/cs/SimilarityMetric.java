package fi.uef.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class SimilarityMetric {
	public enum Method {
		Jiang, Wu, Lin, Path
	}
	
	private static WordnetSimilarity wSimilarity = new WordnetSimilarity();

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		

		
		System.out.println("Please input word1: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String word1 = in.readLine();
		
		System.out.println("Please input word2: ");
		in = new BufferedReader(new InputStreamReader(System.in));
		String word2 = in.readLine();
		

		
		System.out.println("1.Jiang 2.Wu 3.Lin 4.Path 5. Levenshtein 6. q-grams 7. cosin 8. dice");
		System.out.println("Please input the number of similarity method: ");
		in = new BufferedReader(new InputStreamReader(System.in));
		String methodTemp = in.readLine();
		int method=Integer.parseInt(methodTemp);
		
		String type=null;
		if (method ==1 || method ==2 || method ==3 || method==4) {
			System.out.println("Please input word type noun or verb n/v: ");
			in = new BufferedReader(new InputStreamReader(System.in));
		    type= in.readLine();
			
		} 
		
		String similariString = getSimilarity(word1, word2, method, type);
		System.out.println("The similarity of "+word1 +" and " +word2+" is: "+similariString);
		
	}
	
	/**
	 * @param word1
	 * @param word2
	 * @param method
	 * @param type 1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein 6. q-grams 7. cosin 8. dice
	 * @return similarity compared with all senses of the two words
	 */
	public static String getSimilarity(String word1, String word2, int method, String type){
		return getSimilarity(word1, word2, method, type, false);
	}
	
	public static double getSimilarityDouble(String word1, String word2, int method, String type){
		double similarityValue = 0;
		try {
			if (method == 1 || method == 2 || method == 3 || method == 4) {
				similarityValue = wSimilarity.getWordnetSimilarity(word1,
						word2, type, method, false);
				if (similarityValue > 1) {
					similarityValue = 1;
				} else if (similarityValue < 0) {
					similarityValue = 0;
				}

			} else if (method == 5 || method == 6 || method == 7 || method == 8) {
				StringSimilarity stringSimilarity = new StringSimilarity();
				similarityValue = stringSimilarity.getStringSimilarity(word1,
						word2, type, method);

			} else {
				System.out.println("Sorry, this method doesn't exist!");
				similarityValue = -1;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return similarityValue;
	}
	
	/**
	 * @param word1
	 * @param word2
	 * @param method
	 * @param type 1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein 6. q-grams 7. cosin 8. dice
	 * @param firstSenseOnly
	 * @return
	 */
	public static String getSimilarity(String word1, String word2,
			int method, String type, boolean firstSenseOnly) {
		double similarityValue = 0;
		try {
			if (method == 1 || method == 2 || method == 3 || method == 4) {
				similarityValue = wSimilarity.getWordnetSimilarity(word1,
						word2, type, method, firstSenseOnly);
				if (similarityValue > 1) {
					similarityValue = 1;
				} else if (similarityValue < 0) {
					similarityValue = 0;
				}

			} else if (method == 5 || method == 6 || method == 7 || method == 8) {
				StringSimilarity stringSimilarity = new StringSimilarity();
				similarityValue = stringSimilarity.getStringSimilarity(word1,
						word2, type, method);

			} else {
				System.out.println("Sorry, this method doesn't exist!");
				similarityValue = -1;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		String similarityValue1 = df.format(similarityValue);
		return similarityValue1;
	}

}
