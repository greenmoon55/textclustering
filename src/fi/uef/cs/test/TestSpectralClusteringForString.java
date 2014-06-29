package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.SimilarityMetric;
import fi.uef.cs.UnnormalizedSpectralClustering;
import fi.uef.cs.UnorderedPair;
import fi.uef.cs.SimilarityMetric.Method;

public class TestSpectralClusteringForString {

	public static void main(String[] args) throws FileNotFoundException {	
		Scanner in = new Scanner(new FileReader("strings_long.txt"));
		ArrayList<String> data = new ArrayList<String>();
		while (in.hasNext()) {
			data.add(in.nextLine());
		}
		in.close();
		//shortTextSimilarity.HierachicalClustering(data, SimilarityMetric.Method.Jiang);
		int k = 5;
		UnnormalizedSpectralClustering spectralClustering = new UnnormalizedSpectralClustering(k);
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		Method method = Method.Jiang;
		Dataset[] clusters = spectralClustering.stringClustering(data, shortTextSimilarity.getSimilarityMatrixForString(data, method));
		Set<Set<String>> clusterSet = new HashSet<Set<String>>();
		for (Dataset cluster: clusters) {
			Set<String> singleClusterSet = new HashSet<String>();
			for (Instance instance: cluster) {
				singleClusterSet.add((String) instance.classValue());
				System.out.print((String) instance.classValue() + " ");
			}
			clusterSet.add(singleClusterSet);
			System.out.println("");
		}
		HashMap<UnorderedPair<String>, Double> map = shortTextSimilarity.getSimilarityMap(data, method);
		double ssb = ShortTextSimilarity.getSSBForString(clusterSet, map);
		double ssw = ShortTextSimilarity.getSSWForString(clusterSet, map);
		System.out.println(ssb * k / ssw);
		ClusterEvaluation eval;
		/* Measuring the quality of the clusters (multiple measures) */
		eval = new CIndex();
		eval.score(clusters);
//		eval = new SumOfSquaredErrors();
//		System.out.println("Score according to SumOfSquaredErrors: "
//				+ eval.score(clusters));
//		eval = new SumOfCentroidSimilarities();
//		System.out.println("Score according to SumOfCentroidSimilarities: "
//				+ eval.score(clusters));
//		eval = new SumOfAveragePairwiseSimilarities();
//		System.out.println("Score according to SumOfAveragePairwiseSimilarities: "
//				+ eval.score(clusters));
	}

}
