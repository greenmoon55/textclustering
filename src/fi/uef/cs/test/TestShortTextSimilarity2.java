package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.SimilarityMetric;
import fi.uef.cs.UnnormalizedSpectralClustering;

public class TestShortTextSimilarity2 {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		
		Scanner in = new Scanner(new FileReader("data-no-duplicates.txt"));
		ArrayList<List<String>> data = new ArrayList<List<String>>();
		while (in.hasNext()) {
			data.add(Arrays.asList(in.nextLine().split(",")));
		}
		
		//shortTextSimilarity.HierachicalClustering(data, SimilarityMetric.Method.Jiang);
		UnnormalizedSpectralClustering spectralClustering = new UnnormalizedSpectralClustering(25);
		spectralClustering.cluster(data, SimilarityMetric.Method.Jiang);
	}

}
