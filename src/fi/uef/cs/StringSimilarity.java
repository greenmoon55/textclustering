package fi.uef.cs;

import java.io.IOException;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

public class StringSimilarity {
	
	public static float getStringSimilarity(String word1, String word2, SimilarityMetric.Method method) throws IOException {
		AbstractStringMetric metric;
		float result;
		
		if (method == SimilarityMetric.Method.Levenshtein) {
			metric = new Levenshtein();
			result = metric.getSimilarity(word1, word2);
			return result;

		} else if (method == SimilarityMetric.Method.QGrams) {
			metric = new QGramsDistance();
			result = metric.getSimilarity(word1, word2);
			return result;

		} else if (method == SimilarityMetric.Method.Cosine) {
			metric = new CosineSimilarity();
			result = metric.getSimilarity(word1, word2);
			return result;

		} else if (method == SimilarityMetric.Method.Dice) {
			metric = new DiceSimilarity();
			result = metric.getSimilarity(word1, word2);
			//resultfinal = ((int) (result * 100)) / 100;
			return result;

		} else {
			result = -1;
			return result;
		}

	}
}
