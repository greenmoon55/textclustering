package fi.uef.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class SimilarityMetric {
	public enum Method {
		Jiang, Wu, Lin, Path, Levenshtein, QGrams, Cosine, Dice
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
		int methodNumber = Integer.parseInt(methodTemp);
		
		String type = null;
		if (methodNumber == 1 || methodNumber == 2 || methodNumber == 3 || methodNumber == 4) {
			System.out.println("Please input word type noun or verb n/v: ");
			in = new BufferedReader(new InputStreamReader(System.in));
			type = in.readLine();
		}
		
		Method method = null;
		switch (methodNumber) {
		case 1:
			method = Method.Jiang;
			break;
		case 2:
			method = Method.Wu;
			break;
		case 3:
			method = Method.Lin;
			break;
		case 4:
			method = Method.Path;
			break;
		case 5:
			method = Method.Levenshtein;
			break;
		case 6:
			method = Method.QGrams;
			break;
		case 7:
			method = Method.Cosine;
			break;
		case 8:
			method = Method.Dice;
			break;
		default:
			return;
		}
		
		SimilarityMetric similarityMetric = new SimilarityMetric();
		
		String similariString = similarityMetric.getSimilarity(word1, word2, method, type);
		System.out.println("The similarity of "+word1 +" and " +word2+" is: "+similariString);
		
	}
	
	/**
	 * @param word1
	 * @param word2
	 * @param method
	 * @param type 1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein 6. q-grams 7. cosin 8. dice
	 * @return similarity compared with all senses of the two words
	 */
	public String getSimilarity(String word1, String word2, SimilarityMetric.Method method, String type){
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
	public String getSimilarity(String word1, String word2,
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
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		String similarityValue1 = df.format(similarityValue);
		return similarityValue1;
	}

}
