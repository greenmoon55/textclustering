package fi.uef.cs;

import java.io.IOException;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

public class StringSimilarity {
	AbstractStringMetric metric;
	float result;
	float resultfinal;

	//4. Levenshtein 5. q-grams 6. cosin 7. dice
	public float getStringSimilarity(String word1, String word2, String type,
			int method) throws IOException {
		
		if (method == 5) {
			metric = new Levenshtein();
			result = metric.getSimilarity(word1, word2);
			return result;
			
		} else if (method== 6) {
			metric = new QGramsDistance();
			result = metric.getSimilarity(word1, word2);
			return result;
			
		} else if (method == 7) {
			metric = new CosineSimilarity();
			result = metric.getSimilarity(word1, word2);
			return result;
			
		} else if (method == 8) {
			metric = new DiceSimilarity();
			result = metric.getSimilarity(word1, word2);
			resultfinal=((int)(result*100))/100;
			return result;
			
		}else {
			result=-1;
			return result;
		}

	}
}
