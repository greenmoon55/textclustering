package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.aliasi.cluster.Dendrogram;

import fi.uef.cs.HierachicalClustering;
import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.SimilarityMetric.Method;
import fi.uef.cs.UnorderedPair;

public class TestHierachicalClusteringForString {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(new FileReader("strings_long.txt"));
		ArrayList<String> data = new ArrayList<String>();
		while (in.hasNext()) {
			data.add(in.nextLine());
		}
		in.close();
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		HashMap<UnorderedPair<String>, Double> similarityMap = shortTextSimilarity.getSimilarityMap(data, Method.Path);
		Dendrogram<String> dendro = shortTextSimilarity.getDendrogramForString(data, similarityMap);
		HierachicalClustering hc = new HierachicalClustering();
		List<Double> sswList = hc.getSSWListForString(new HashSet<String>(data), dendro, similarityMap);
		List<Double> ssbList = hc.getSSBListForString(new HashSet<String>(data), "n", dendro, similarityMap);
		for (int i = 0; i < 15; i++) {
			int k = i + 1;
			Set<Set<String>> partitions = dendro.partitionK(i + 1);
			System.out.println(" ");
			System.out.println((i + 1) + " clusters");
			System.out.println(partitions);
			Double ssw = sswList.get(i);
			//System.out.println("ssw:" + ssw);
			Double ssb = ssbList.get(i);
			//System.out.println("ssb:" + ssb);
			System.out.println(k*ssw/ssb);
		}
	}
}
