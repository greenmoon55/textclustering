package fi.uef.cs.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.aliasi.cluster.Dendrogram;

import fi.uef.cs.ShortTextSimilarity;
import fi.uef.cs.SimilarityMetric.Method;

public class TestHierachicalClusteringForStrings {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(new FileReader("strings.txt"));
		ArrayList<String> data = new ArrayList<String>();
		while (in.hasNext()) {
			data.add(in.nextLine());
		}
		in.close();
		ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
		Dendrogram<String> dendro = shortTextSimilarity.getDendrogramForString(data, Method.Jiang);
		for (int i = 0; i < 5; i++) {
			Set<Set<String>> partitions = dendro.partitionK(i + 1);
			System.out.println(" ");
			System.out.println(i + " clusters");
			for (Set<String> stringListSet: partitions) {
				System.out.println("set");
				for (String string: stringListSet) {
					System.out.print(string + ",");
				}
				System.out.println(" ");
			}
		}
	}
}
