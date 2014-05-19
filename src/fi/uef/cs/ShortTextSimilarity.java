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
	
	public double[][] getSimilarityMatrixForString(List<String> data, SimilarityMetric.Method method) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = similarityMetric.getSimilarity(
						data.get(i), data.get(j), method, "n", false);
			}
		}
		return similarityMatrix;
	}
	
	public static String[] setToArray(Set<String> set) {
		// 当参数数组的长度小于list的元素个数时，会自动扩充数组的长度以适应list的长度
		String[] strArray = (String[]) set.toArray(new String[0]);
		return strArray;
	}
	
	public static double getSSWForString(Set<Set<String>> slKClustering, HashMap<UnorderedPair<String>, Double> similarityMap) {
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
						double similarityValue = similarityMap.get(new UnorderedPair<String>(sI, sJ));
						distanceIJ = 1 - similarityValue;

						if (distanceIJ > maxdistance) {
							maxdistance = distanceIJ;
						}						
					}
				}
			} else {
				numSingleObject += 1/arrayStrings.length;
			}
			
			if (maxdistance > maxdistanceAll)
				maxdistanceAll = maxdistance;				
		}		
		return maxdistanceAll + numSingleObject;
	}
	
	public static double getSSWForStringList(Set<Set<List<String>>> slKClustering, HashMap<UnorderedPair<List<String>>, Double> similarityMap) {
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
						double similarityValue = similarityMap.get(new UnorderedPair<List<String>>(sI, sJ));
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
		return maxdistanceAll + numSingleObject;
	}
	
	public double getSSBForString(Set<Set<String>> slKClustering, HashMap<UnorderedPair<String>, Double> similarityMap) {
		double ssbTotal = 0;
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
						
						double similarityValue = similarityMap.get(new UnorderedPair<String>(temp1String, temp2String));
						double distanceIJ = 1 - similarityValue;

						distancePairList.add(distanceIJ);
					}
				}
				Collections.sort(distancePairList);
				Object[] distancePairArray = distancePairList.toArray();
				Double ssbDistance = (Double) distancePairArray[0];
				int sI_size = sI.size();
				int sJ_size = sJ.size();
				if (sI_size > 1 || sJ_size > 1) {
					ssbTotal += ssbDistance;
					normalizationFactor += 1;
				}
			}
		}
		return ssbTotal;
	}
	
	public double getSSBForStringList(Set<Set<List<String>>> slKClustering, HashMap<UnorderedPair<List<String>>, Double> similarityMap) {
		List<Double> ssb = new ArrayList<Double>();
			double ssbTotal = 0;
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
							
							double similarityValue = similarityMap.get(new UnorderedPair<List<String>>(temp1String, temp2String));
							double distanceIJ = 1 - similarityValue;

							distancePairList.add(distanceIJ);
						}
					}
					//ascending order
					Collections.sort(distancePairList);
					Object[] distancePairArray = distancePairList.toArray();
					Double ssbDistance = (Double) distancePairArray[0];
					int sI_size = sI.size();
					int sJ_size = sJ.size();
					if (sI_size > 1 || sJ_size > 1) {
						ssbTotal += ssbDistance;
						normalizationFactor += 1;
					}
				}

			}
		return ssbTotal;
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
