package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.aliasi.cluster.Dendrogram;

import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.SimilarityMetric.Method;

public class TestHierachicalClusteringForStringList {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(new FileReader("stringlists.txt"));
		ArrayList<List<String>> data = new ArrayList<List<String>>();
		while (in.hasNext()) {
			data.add(Arrays.asList(in.nextLine().split(",")));
		}
		in.close();
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		Dendrogram<List<String>> dendro = shortTextSimilarity.getDendrogramForStringList(data, Method.Jiang);
		for (int i = 0; i < 50; i++) {
			Set<Set<List<String>>> partitions = dendro.partitionK(i + 1);
			System.out.println(" ");
			System.out.println(i + " clusters");
			for (Set<List<String>> stringListSet: partitions) {
				System.out.println("set");
				for (List<String> stringList: stringListSet) {
					for (String string: stringList) {
						System.out.print(string + ",");
					}
					System.out.print(" ");
				}
				System.out.println(" ");
			}
		}
		

	}
}
