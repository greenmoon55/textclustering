package fi.uef.cs;

import java.io.IOException;
import java.util.*;

import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.LeafDendrogram;
import com.aliasi.cluster.LinkDendrogram;
import com.aliasi.cluster.AbstractHierarchicalClusterer;
import com.aliasi.util.Scored;
import com.aliasi.util.ScoredObject;

public class ShortTextSimilarity {
	private SimilarityMetric similarityMetric = new SimilarityMetric();
	
	class ShortTextComparison {
		public String str1, str2;
		public double similarity;
		ShortTextComparison(String str1, String str2, double similarity) {
			this.str1 = str1;
			this.str2 = str2;
			this.similarity = similarity;
		}
	}
	
	private double getMaxSimilarity(String word, List<String> wordArray,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		double maxSimilarity = 0;
		for (String w : wordArray) {
			maxSimilarity = Math.max(maxSimilarity, this.similarityMetric
					.getSimilarity(w, word, method, type, firstSenseOnly));
		}
		return maxSimilarity;
	}

	public double getSimilarity(List<String> wordArray1, List<String> wordArray2,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		if (wordArray1.size() < wordArray2.size()) {
			List<String> temp = wordArray1;
			wordArray1 = wordArray2;
			wordArray2 = temp;
		}
		double result = 0;
		for (String word : wordArray1) {
			result += getMaxSimilarity(word, wordArray2, method, type,
					firstSenseOnly);
		}
		return result/wordArray1.size();
	}
	
	static class PairScore<E> implements Scored {
        final Dendrogram<E> mDendrogram1;
        final Dendrogram<E> mDendrogram2;
        final double mScore;
        public PairScore(Dendrogram<E> dendrogram1, Dendrogram<E> dendrogram2,
                         double score) {
            mDendrogram1 = dendrogram1;
            mDendrogram2 = dendrogram2;
            mScore = score;
        }
        public double score() {
            return mScore;
        }
        @Override
        public String toString() {
            return "ps("
                + mDendrogram1
                + ","
                + mDendrogram2
                + ":"
                + mScore
                + ") ";
        }
    }
	
	public List<HashMap<List<List<String>>, Double>> HierachicalClustering(ArrayList<List<String>> data, SimilarityMetric.Method method) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = getSimilarity(data.get(i), data.get(j), method, "n", false);
			}
		}
		
		// 数组 elements 变成 LeafDendrogram数组 array
		LeafDendrogram<List<String>>[] leafs = (LeafDendrogram<List<String>>[]) new LeafDendrogram[data.size()];
		for (int i = 0; i < data.size(); i++) {
			leafs[i] = new LeafDendrogram<List<String>>(data.get(i));
		}

		// LeafDendrogram数组 变成 set 容器 装满系统树形的容器
		Set<Dendrogram<List<String>>> clusters = new HashSet<Dendrogram<List<String>>>(
				data.size());
		for (Dendrogram<List<String>> dendrogram : leafs) {
			clusters.add(dendrogram);
		}
		
		/*
		 * test for set clusters for (Dendrogram<String> dendrogram : clusters)
		 * { System.out.println("the each dendrogram is: "+dendrogram); }
		 */

		// pairscore 有一个构造方法，二个Dendrogram， 加一个score
		ArrayList<PairScore<List<String>>> pairScoreList = new ArrayList<PairScore<List<String>>>();
		double maxDistance = Double.MAX_VALUE;
		

		// 得到所有的pair 的distance， 放到pairscore 的list 里面
		for (int i = 0; i < data.size(); ++i) {
			for (int j = i + 1; j < data.size(); ++j) {
				double distanceIJ = 1 - similarityMatrix[i][j];
		
//					System.out.println(sI + "  " + sJ + " the distance is: "
//							+ distanceIJ);
				if (distanceIJ > maxDistance)
					continue;
				pairScoreList.add(new PairScore<List<String>>(leafs[i], leafs[j],
						distanceIJ));
			}
		}

		/*
		 * //打印 pairScore ps(chicken,car:50.0) ps(chicken,orange:40.0) for
		 * (PairScore<String> pairScore : pairScoreList) {
		 * System.out.println(pairScore); }
		 */

		// pairscore 从小到大排列ps(car,jeep:0.0) ps(cat,woman:4.76)
		PairScore<List<String>>[] pairScores = (PairScore<List<String>>[]) new PairScore[pairScoreList
				.size()];
		pairScoreList.toArray(pairScores);
		Arrays.sort(pairScores, ScoredObject.comparator()); // increasing order
															// of distance

		/*
		 * //测试 pairscore 从小到大排列
		 * 
		 * System.out.println("\nAfter sort"); for (int i = 0; i <
		 * pairScores.length; i++) { System.out.println(pairScores[i]); }
		 */
//			System.out.println("----------------------------");
		for (int i = 0; i < pairScores.length && clusters.size() > 1; ++i) {
			PairScore<List<String>> ps = pairScores[i];
			if (ps.score() > Double.MAX_VALUE)
				break;
			// ps.mDendrogram1 是组成ps 里的第一个LeafDendrogram dereference 返回它的parent
			Dendrogram<List<String>> d1 = ps.mDendrogram1.dereference();
			Dendrogram<List<String>> d2 = ps.mDendrogram2.dereference();

//				System.out.println(ps.mDendrogram1 + " d1 dereference: " + d1);
//				System.out.println(ps.mDendrogram2 + " d2 dereference: " + d2);
//				System.out.println("-------------------------------");

			if (d1.equals(d2)) {
				continue; // already linked
			}
			clusters.remove(d1);
			clusters.remove(d2);
			// 把d1， d2 的parent 设置成新生成的 有分支的系统树形 dlink， pairScores[i].mScore
			// 是distance数字
			LinkDendrogram<List<String>> dLink = new LinkDendrogram<List<String>>(d1, d2,
					pairScores[i].mScore);
			clusters.add(dLink);
		}

		// link up remaining unlinked dendros at +infinity distance
		Iterator<Dendrogram<List<String>>> it = clusters.iterator();
		Dendrogram<List<String>> dendro = it.next(); // skip first -
												// self,被排好的系统树形就一个，如果还有，连好的和没连号的连
		while (it.hasNext()) {
			dendro = new LinkDendrogram<List<String>>(dendro, it.next(),
					Double.POSITIVE_INFINITY);
		}
		
		for (int i = 0; i < 55; i++) {
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
		return null;
	}
	
}
