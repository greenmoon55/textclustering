package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.aliasi.cluster.Dendrogram;

import fi.uef.cs.HierachicalClustering;
import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.UnorderedPair;
import fi.uef.cs.SimilarityMetric.Method;

public class TestHierachicalClusteringForStringList {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(new FileReader("stringlists_full.txt"));
		ArrayList<List<String>> data = new ArrayList<List<String>>();
		while (in.hasNext()) {
			data.add(Arrays.asList(in.nextLine().split(",")));
		}
		in.close();
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		HashMap<UnorderedPair<List<String>>, Double> similarityMap = shortTextSimilarity.getSimilarityMapForStringList(data, Method.Jiang);
		Dendrogram<List<String>> dendro = shortTextSimilarity.getDendrogramForStringList(data, Method.Jiang, similarityMap);
		HierachicalClustering hc = new HierachicalClustering();
		List<Double> sswList = hc.getSSWListForStringList(dendro, similarityMap);
		List<Double> ssbList = hc.getSSBListForStringList("n", dendro, similarityMap);
		double min = 1;
		for (int i = 0; i < 150; i++) {
			Set<Set<List<String>>> partitions = dendro.partitionK(i + 1);
			Double ssw = sswList.get(i);
			Double ssb = ssbList.get(i);
			double val = ssw/ssb;
			if (val < min) {
				min = val;
				System.out.println(" ");
				System.out.println(i + " clusters");
				System.out.println(partitions);
				System.out.println("ssw:" + ssw);
				System.out.println("ssb:" + ssb);
				System.out.println(ssw/ssb);
			}
			
		}
		

	}
}
