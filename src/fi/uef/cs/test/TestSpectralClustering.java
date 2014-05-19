package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;

import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.SimilarityMetric;
import fi.uef.cs.UnnormalizedSpectralClustering;
import fi.uef.cs.SimilarityMetric.Method;

public class TestSpectralClustering {

	public static void main(String[] args) throws FileNotFoundException {	
		Scanner in = new Scanner(new FileReader("stringlists.txt"));
		ArrayList<List<String>> data = new ArrayList<List<String>>();
		while (in.hasNext()) {
			data.add(Arrays.asList(in.nextLine().split(",")));
		}
		in.close();
		//shortTextSimilarity.HierachicalClustering(data, SimilarityMetric.Method.Jiang);
		UnnormalizedSpectralClustering spectralClustering = new UnnormalizedSpectralClustering(5);
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		Dataset[] clusters = spectralClustering.stringListClustering(data, shortTextSimilarity.getSimilarityMatrix(data, Method.Jiang));
		System.out.println(clusters[0].instance(0).classValue());
		ClusterEvaluation eval;
		/* Measuring the quality of the clusters (multiple measures) */
		eval = new SumOfSquaredErrors();
		System.out.println("Score according to SumOfSquaredErrors: "
				+ eval.score(clusters));
		eval = new SumOfCentroidSimilarities();
		System.out.println("Score according to SumOfCentroidSimilarities: "
				+ eval.score(clusters));
		eval = new SumOfAveragePairwiseSimilarities();
		System.out.println("Score according to SumOfAveragePairwiseSimilarities: "
				+ eval.score(clusters));
	}

}
