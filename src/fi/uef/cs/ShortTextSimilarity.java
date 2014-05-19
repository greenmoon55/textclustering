package fi.uef.cs;

import java.util.*;

import com.aliasi.cluster.Dendrogram;
import com.aliasi.util.Scored;

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
	
	public Dendrogram<String> getDendrogramForString(ArrayList<String> data, HashMap<UnorderedPair<String>, Double> similarityMap) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = similarityMap.get(new UnorderedPair<String>(data.get(i), data.get(j)));
			}
		}
		return HierachicalClustering.getDendrogram(data, similarityMatrix);
	}
	
	public Dendrogram<List<String>> getDendrogramForStringList(ArrayList<List<String>> data, HashMap<UnorderedPair<List<String>>, Double> similarityMap) {
		//double[][] similarityMatrix = getSimilarityMatrix(data, method);
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = similarityMap.get(new UnorderedPair<List<String>>(data.get(i), data.get(j)));
			}
		}
		return HierachicalClustering.getDendrogram(data, similarityMatrix);
	}
	
	public HashMap<UnorderedPair<String>, Double> getSimilarityMap(ArrayList<String> data,
			SimilarityMetric.Method method) {
		HashMap<UnorderedPair<String>, Double> map = new HashMap<UnorderedPair<String>, Double>();
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				double value = this.similarityMetric.getSimilarity(data.get(i),
						data.get(j), method, "n", false);
				map.put(new UnorderedPair<String>(data.get(i), data.get(j)), value);
			}
		}
		return map;
	}
	
	public HashMap<UnorderedPair<List<String>>, Double> getSimilarityMapForStringList(ArrayList<List<String>> data,
			SimilarityMetric.Method method) {
		HashMap<UnorderedPair<List<String>>, Double> map = new HashMap<UnorderedPair<List<String>>, Double>();
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				double value = getSimilarity(data.get(i),
						data.get(j), method, "n", false);
				map.put(new UnorderedPair<List<String>>(data.get(i), data.get(j)), value);
			}
		}
		return map;
	}
	
	public double[][] getSimilarityMatrix(ArrayList<List<String>> data, SimilarityMetric.Method method) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = getSimilarity(
						data.get(i), data.get(j), method, "n", false);
			}
		}
		return similarityMatrix;
	}
	
	public double getSSWForString(Set<String> inputSet, Set<Set<String>> slKClustering, HashMap<UnorderedPair<String>, Double> similarityMap) {
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
							double similarityValue = similarityMap.get(new UnorderedPair<String>(sI, sJ));
							distanceIJ = 1 - similarityValue;
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
			ssw.add(maxdistanceAll + numSingleObject);
			//System.out.println(maxdistanceAll);
//			System.out.println(k + "  " + slKClustering + ". ssw is: "
//					+ totaldistance);
//			System.out
//					.println("-----------------------------------------------------------------------------------------------------");
		}

		return ssw;
		// System.out.println(k + "  " + slKClustering);
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
}
