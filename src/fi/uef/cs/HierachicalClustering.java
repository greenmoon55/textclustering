package fi.uef.cs;

import java.io.IOException;
import java.util.*;

import com.aliasi.cluster.*;
import com.aliasi.util.ScoredObject;

import fi.uef.cs.ShortTextSimilarity.PairScore;

public class HierachicalClustering {
	private static SimilarityMetric similarityMetric = new SimilarityMetric();
	private static ShortTextSimilarity shortTextSimilarity = new ShortTextSimilarity();
	public static <T> Dendrogram<T> getDendrogram(ArrayList<T> data, SimilarityMetric.Method method, double[][] similarityMatrix) {
		// 数组 elements 变成 LeafDendrogram数组 array
		LeafDendrogram<T>[] leafs = (LeafDendrogram<T>[]) new LeafDendrogram[data.size()];
		for (int i = 0; i < data.size(); i++) {
			leafs[i] = new LeafDendrogram<T>(data.get(i));
		}

		// LeafDendrogram数组 变成 set 容器 装满系统树形的容器
		Set<Dendrogram<T>> clusters = new HashSet<Dendrogram<T>>(data.size());
		for (Dendrogram<T> dendrogram : leafs) {
			clusters.add(dendrogram);
		}
		
		/*
		 * test for set clusters for (Dendrogram<String> dendrogram : clusters)
		 * { System.out.println("the each dendrogram is: "+dendrogram); }
		 */

		// pairscore 有一个构造方法，二个Dendrogram， 加一个score
		ArrayList<PairScore<T>> pairScoreList = new ArrayList<PairScore<T>>();
		double maxDistance = Double.MAX_VALUE;
		

		// 得到所有的pair 的distance， 放到pairscore 的list 里面
		for (int i = 0; i < data.size(); ++i) {
			for (int j = i + 1; j < data.size(); ++j) {
				double distanceIJ = 1 - similarityMatrix[i][j];
		
//					System.out.println(sI + "  " + sJ + " the distance is: "
//							+ distanceIJ);
				if (distanceIJ > maxDistance)
					continue;
				pairScoreList.add(new PairScore<T>(leafs[i], leafs[j],
						distanceIJ));
			}
		}

		/*
		 * //打印 pairScore ps(chicken,car:50.0) ps(chicken,orange:40.0) for
		 * (PairScore<String> pairScore : pairScoreList) {
		 * System.out.println(pairScore); }
		 */

		// pairscore 从小到大排列ps(car,jeep:0.0) ps(cat,woman:4.76)
		PairScore<T>[] pairScores = (PairScore<T>[]) new PairScore[pairScoreList.size()];
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
			PairScore<T> ps = pairScores[i];
			if (ps.score() > Double.MAX_VALUE)
				break;
			// ps.mDendrogram1 是组成ps 里的第一个LeafDendrogram dereference 返回它的parent
			Dendrogram<T> d1 = ps.mDendrogram1.dereference();
			Dendrogram<T> d2 = ps.mDendrogram2.dereference();

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
			LinkDendrogram<T> dLink = new LinkDendrogram<T>(d1, d2,
					pairScores[i].mScore);
			clusters.add(dLink);
		}

		// link up remaining unlinked dendros at +infinity distance
		Iterator<Dendrogram<T>> it = clusters.iterator();
		Dendrogram<T> dendro = it.next(); // skip first -
												// self,被排好的系统树形就一个，如果还有，连好的和没连号的连
		while (it.hasNext()) {
			dendro = new LinkDendrogram<T>(dendro, it.next(),
					Double.POSITIVE_INFINITY);
		}
		
		return dendro;
	}
	
	public String[] setToArray(Set<String> set) {
		// 当参数数组的长度小于list的元素个数时，会自动扩充数组的长度以适应list的长度
		String[] strArray = (String[]) set.toArray(new String[0]);
		return strArray;
	}
	
	public List<Double> getSSWListForString(Set<String> inputSet, String type,
			SimilarityMetric.Method method, Dendrogram<String> dendrogram) {

		List<Double> ssw = new ArrayList<Double>();
		for (int k = 1; k <= dendrogram.size(); ++k) {
			Set<Set<String>> slKClustering = dendrogram.partitionK(k);
			int normalizationFactor = 0;
			double maxdistanceAll = 0;
			int numSingleObject = 0;
			for (Set<String> set : slKClustering) {
				int setSize = set.size();
				double maxdistance = 0;
				double distanceIJ;
				String[] arrayStrings = setToArray(set);
				if (setSize > 1) {
					// 得到所有的pair 的distance， 放到pairscore 的list 里面
					for (int i = 0; i < arrayStrings.length; i++) {
						String sI = arrayStrings[i];

						for (int j = i + 1; j < arrayStrings.length; j++) {
							String sJ = arrayStrings[j];
							// double distanceIJ=
							// wordnetSimilarity.getWordnetSimilarityAll(sI,
							// sJ,type);
							double similarityValue = similarityMetric.getSimilarity(sI, sJ, method, type); 
							distanceIJ= 1 - similarityValue;
//							System.out.println(sI + "  " + sJ
//									+ " the distance is: " + distanceIJ);
//							totaldistance += distanceIJ;
							if (distanceIJ > maxdistance) {
								maxdistance = distanceIJ;
							}
							
						}
					}
					normalizationFactor += 1;
//					totaldistance += totaldistance_temp;
//					totaldistance_temp /= normalizationFactor;
//					totaldistance += totaldistance_temp;
				} else {
					numSingleObject += 1/arrayStrings.length;
				}
				
				if (maxdistance > maxdistanceAll)
					maxdistanceAll = maxdistance;				
			}
			
//			if ((normalized.trim().equalsIgnoreCase("y")) && (normalizationFactor != 0) )
//				totaldistance /= normalizationFactor;
			// System.out.println(k + "  " + slKClustering);
			ssw.add(maxdistanceAll+ numSingleObject);
			//System.out.println(maxdistanceAll);
//			System.out.println(k + "  " + slKClustering + ". ssw is: "
//					+ totaldistance);
//			System.out
//					.println("-----------------------------------------------------------------------------------------------------");
		}

		return ssw;
		// System.out.println(k + "  " + slKClustering);
	}
	
	public List<Double> getSSWListForStringList(String type,
			SimilarityMetric.Method method, Dendrogram<List<String>> dendrogram) {

		List<Double> ssw = new ArrayList<Double>();
		for (int k = 1; k <= dendrogram.size(); ++k) {
			Set<Set<List<String>>> slKClustering = dendrogram.partitionK(k);
			double maxdistanceAll = 0;
			int numSingleObject = 0;
			for (Set<List<String>> set : slKClustering) {
				int setSize = set.size();
				double maxdistance = 0;
				double distanceIJ;
				List<List<String>> list = new ArrayList<List<String>>(set);
				if (setSize > 1) {
					// 得到所有的pair 的distance， 放到pairscore 的list 里面
					for (int i = 0; i < setSize; i++) {
						List<String> sI = list.get(i);
						for (int j = i + 1; j < setSize; j++) {
							List<String> sJ = list.get(j);
							double similarityValue = shortTextSimilarity.getSimilarity(sI, sJ, method, type, false); 
							distanceIJ = 1 - similarityValue;
							if (distanceIJ > maxdistance) {
								maxdistance = distanceIJ;
							}
							
						}
					}
				} else {
					numSingleObject += 1/list.size();
				}
				
				if (maxdistance > maxdistanceAll)
					maxdistanceAll = maxdistance;				
			}
			ssw.add(maxdistanceAll+ numSingleObject);
		}
		return ssw;
	}
	
	public List<Double> getSSBListForString(Set<String> inputSet, String type,
			SimilarityMetric.Method method, String normalized, Dendrogram<String> dendrogram) {
//		Clustering_own clustering_own = new Clustering_own();
//		WordnetSimilarity wordnetSimilarity = new WordnetSimilarity();

		// Dendrogram<String> dendrogram = clustering_own
		// .single_linkage_clustering(inputSet, type);
		
		System.out.println("GETSSBList");
		System.out.println(dendrogram);

		List<Double> ssb = new ArrayList<Double>();
		for (int k = 1; k <= dendrogram.size(); ++k) {
			double ssbTotal = 0;
			Set<Set<String>> slKClustering = dendrogram.partitionK(k);
			Object[] objArray = slKClustering.toArray();
			double ssbDistanceMin = 1000000.0;

			int normalizationFactor = 0;
			for (int i = 0; i < objArray.length; i++) {
				Set<String> sI = (Set<String>) objArray[i];
				String[] sIArray = setToArray(sI);
				for (int j = i + 1; j < objArray.length; j++) {
					Set<String> sJ = (Set<String>) objArray[j];
					String[] sJArray = setToArray(sJ);
					List<Double> distancePairList = new ArrayList<Double>();
					for (int l = 0; l < sIArray.length; l++) {
						String temp1String = sIArray[l];
						for (int m = 0; m < sJArray.length; m++) {
							String temp2String = sJArray[m];
							
							double similarityValue = this.similarityMetric.getSimilarity(temp1String, temp2String, method, type); 
							double distanceIJ = 1 - similarityValue;

							distancePairList.add(distanceIJ);
						}
					}
					//ascending order
					Collections.sort(distancePairList);
					Object[] distancePairArray = distancePairList.toArray();
					Double ssbDistance = (Double) distancePairArray[0];
//					Double ssbDistance = 0.0;
					
					//if ( ssbDistance < ssbDistanceMin)
					//	ssbDistanceMin = ssbDistance;
					
/*					int jj = 0;
					for (int l = 0; l < sIArray.length; l++) 
						for (int m = 0; m < sJArray.length; m++)
						{
							ssbDistance += (Double) distancePairArray[jj];
							jj++;
						}*/
					//System.out.println(sI + " " + sJ + " ssb distance is: "
					//		+ ssbDistance);
					int sI_size = sI.size();
					int sJ_size = sJ.size();
					if (sI_size > 1 || sJ_size > 1) {
						ssbTotal += ssbDistance;
						normalizationFactor += 1;
					}
				}

			}

			// System.out.println(k + "  " + slKClustering + ". ssw is: "+
			// totaldistance);
		//	System.out.println(slKClustering + " ssbTotal distancelllllllllll is: "
		//			+ ssbTotal);
			if((normalized.trim().equalsIgnoreCase("y")) && (normalizationFactor != 0) )
				ssbTotal /= normalizationFactor;
			ssb.add(ssbTotal);
			//System.out.println(ssbDistanceMin);
		}

		return ssb;
		// System.out.println(k + "  " + slKClustering);
	}
	
	public List<Double> getSSBListForStringList(String type,
			SimilarityMetric.Method method, String normalized, Dendrogram<List<String>> dendrogram) {
		System.out.println("GETSSBList");
		System.out.println(dendrogram);

		List<Double> ssb = new ArrayList<Double>();
		for (int k = 1; k <= dendrogram.size(); ++k) {
			double ssbTotal = 0;
			Set<Set<List<String>>> slKClustering = dendrogram.partitionK(k);
			Object[] objArray = slKClustering.toArray();
			double ssbDistanceMin = 1000000.0;

			int normalizationFactor = 0;
			for (int i = 0; i < objArray.length; i++) {
				Set<List<String>> sI = (Set<List<String>>) objArray[i];
				List<List<String>> sIArray = new ArrayList<List<String>>(sI);
				for (int j = i + 1; j < objArray.length; j++) {
					Set<List<String>> sJ = (Set<List<String>>) objArray[j];
					List<List<String>> sJArray = new ArrayList<List<String>>(sJ);
					List<Double> distancePairList = new ArrayList<Double>();
					for (int l = 0; l < sIArray.size(); l++) {
						List<String> temp1String = sIArray.get(l);
						for (int m = 0; m < sJArray.size(); m++) {
							List<String> temp2String = sJArray.get(m);
							
							double similarityValue = shortTextSimilarity.getSimilarity(temp1String, temp2String, method, type, false); 
							double distanceIJ = 1 - similarityValue;

							distancePairList.add(distanceIJ);
						}
					}
					//ascending order
					Collections.sort(distancePairList);
					Object[] distancePairArray = distancePairList.toArray();
					Double ssbDistance = (Double) distancePairArray[0];
//					Double ssbDistance = 0.0;
					
					//if ( ssbDistance < ssbDistanceMin)
					//	ssbDistanceMin = ssbDistance;
					
/*					int jj = 0;
					for (int l = 0; l < sIArray.length; l++) 
						for (int m = 0; m < sJArray.length; m++)
						{
							ssbDistance += (Double) distancePairArray[jj];
							jj++;
						}*/
					//System.out.println(sI + " " + sJ + " ssb distance is: "
					//		+ ssbDistance);
					int sI_size = sI.size();
					int sJ_size = sJ.size();
					if (sI_size > 1 || sJ_size > 1) {
						ssbTotal += ssbDistance;
						normalizationFactor += 1;
					}
				}

			}

			// System.out.println(k + "  " + slKClustering + ". ssw is: "+
			// totaldistance);
		//	System.out.println(slKClustering + " ssbTotal distancelllllllllll is: "
		//			+ ssbTotal);
			if((normalized.trim().equalsIgnoreCase("y")) && (normalizationFactor != 0) )
				ssbTotal /= normalizationFactor;
			ssb.add(ssbTotal);
			//System.out.println(ssbDistanceMin);
		}

		return ssb;
		// System.out.println(k + "  " + slKClustering);
	}
}
