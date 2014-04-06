package com.aliasi.cluster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.aliasi.cluster.AbstractHierarchicalClusterer.PairScore;
import com.aliasi.util.ScoredObject;

import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.Lin;
import edu.sussex.nlp.jws.Path;
import edu.sussex.nlp.jws.WuAndPalmer;
import fi.uef.cs.StringSimilarity;
import fi.uef.cs.WordnetSimilarity;

public class Stop_criteria {
	WordnetSimilarity wSimilarity=new WordnetSimilarity();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Stop_criteria test = new Stop_criteria();
		
		
		System.out.println("Please input words list and seperate them by \",\": ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		//String inputString = in.readLine();
		
		String inputString = "car,orange,bike,bus,apple";
		// String inputString = "car,orange,bike,bus,apple,dog,cat";
		//String inputString = "car,orange,bike,bus,apple,dog,cat,pen,pencil";
//		String inputString = "cafe,restaurant,cafeteria,hotel,hostel,arena,stadium,theater,cinema";
		Set<String> inputSet = new HashSet<String>();
		for (String s : inputString.split(","))
			inputSet.add(s);
		
		System.out.println("Please input word type noun or verb n/v: ");
		in = new BufferedReader(new InputStreamReader(System.in));
		String type= in.readLine();
		
		System.out.println("1.Jiang 2.Wup 3.Lin 4.Path");
		System.out.println("Please input the number of similarity method: ");
		in = new BufferedReader(new InputStreamReader(System.in));
		String methodTemp = in.readLine();
		int method=Integer.parseInt(methodTemp);
		
		System.out.println("1.Calinski & Harabasz   CH=[SSB/(m-1)]/[SSW/(n-m)]");
		System.out.println("2.Hartigan   H=log(SSB/SSW)]");
		System.out.println("3.WB-index   WB=m*SSW/SSB");
		System.out.println("Please input the number of stop criteria: ");
		in = new BufferedReader(new InputStreamReader(System.in));
		String formulaTemp = in.readLine();
		int formula=Integer.parseInt(formulaTemp);
		
		System.out.println("Use normalized SSW and SSB (y/n):");
		in = new BufferedReader(new InputStreamReader(System.in));
		String normalized = in.readLine();
		
		System.out.println("Do you want to show the detailed process of stop criteria (y/n):");
		in = new BufferedReader(new InputStreamReader(System.in));
		String detail = in.readLine();
		
//		double distanceIJ = 0;
//		double similariyValue=test.getSimilarity("apple", "orange", method, type);
//		distanceIJ=1-similariyValue;
//		System.out.println("test number is: "+distanceIJ);

		// test.singlelinkclusterImp(inputSet,"n");
		// test.completelinkclusterImp(inputSet, "n");
		// test.getSSWList(inputSet, "n");
		// test.getSSBList(inputSet, "n");
		// test.getSSBbyLevel(inputSet, "n", 2);
//Argument3 use name  1.lin 2.wup 3.path 4.jiang method third argument, 
//formula method four argument1 use number1. log Log(SSB/SSW) 2. k * ssw / ssb 3. [trace B/(k-1)]/[trace W/(n-k)]
	
		
		/*1. type noun or verb
		 *2. method 1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein
		 *	
		 *3. formula  1.Calinski & Harabasz   CH=[SSB/(m-1)]/[SSW/(n-m)]
		 *            2.Hartigan   H=log(SSB/SSW)
		 *            3.WB-index   WB=m*SSW/SSB
		 *4. detail y or n        
		 */
		
//		List<Double> sswList =new ArrayList<Double>();
//		sswList=test.getSSWListDetail(inputSet, type, method);
//		System.out.println(sswList.isEmpty());
		
		test.stopCriteriaList(inputSet, type, method,formula,detail, normalized );
//		test.stopCriteriaList(inputSet, "n", "lin","1");
//		test.stopCriteriaList(inputSet, "n", "lin","3");

	}

	// lin, wup, path, jiang method third argument
	// stop certeria formula choose: 1. log Log(SSB/SSW) 2. k * ssw / ssb 3.
	// [trace B/(k-1)]/[trace W/(n-k)]
	/*1. type noun or verb
	 *2. method 1.lin 2.wup 3.path 4.jiang
	 *3. formula  1.Calinski & Harabasz   CH=[SSB/(m-1)]/[SSW/(n-m)]
	 *            2.Hartigan   H=log(SSB/SSW)
	 *            3.WB-index   WB=m*SSW/SSB
	 *4. detail y or n        
	 */
	public HashMap<Set<Set<String>>, Double> stopCriteriaList(
			Set<String> inputSet, String type, int method, int formula, String detail, String normalized)
			throws IOException {
		double value;
		int n=inputSet.size();
		HashMap<Set<Set<String>>, Double> valueList = new HashMap<Set<Set<String>>, Double>();

		List<Double> scList =new ArrayList<Double>();
		
		//1.Jiang 2.Wup 3.Lin 4.Path
		List<Double> sswList =new ArrayList<Double>();
		List<Double> ssbList =new ArrayList<Double>();
		if(detail.trim().equalsIgnoreCase("y")){
		 sswList = getSSWListDetail(inputSet, type, method, normalized);
		 ssbList = getSSBListDetail(inputSet, type, method, normalized);
		}else{
			sswList=getSSWList(inputSet, type, method,normalized);
			ssbList=getSSBList(inputSet, type, method,normalized);
		}

		Object[] sswArray = sswList.toArray();
		Object[] ssbArray = ssbList.toArray();
		
		
		Dendrogram<String> dendrogram = single_linkage_clustering_method(inputSet, type, method);
		
		String methodnameString=null;
		if(method==1){
			methodnameString="Similarity method:Jiang";
		}else if (method==2) {
			methodnameString="Similarity method:Wup";
		} else if(method ==3) {
			methodnameString="Similarity method:Lin";
		}else if (method==4) {
			methodnameString="Similarity method:Path";
		}else {
			System.out.println("No such similairty method.");
		
		}

		if (formula == 1) {
			System.out.println(" Calinski & Harabasz "+"CH=[SSB/(m-1)]/[SSW/(n-m)]  "+methodnameString );
			int k = 1;
			for (int i = 0; i < sswArray.length; i++) {
				Double ssw = (Double) sswArray[i];
				Double ssb = (Double) ssbArray[i];

				if ( ssw!=0) {
					double temp1=ssb/(k-1);
					double temp2=ssw/(n-k);
					value =temp1/temp2;

				} else {
					value = 0;}
				Set<Set<String>> slKClustering = dendrogram.partitionK(i + 1);
				if(k!=1)
				System.out.println(k+". "+slKClustering + "  stop criteria= [" + ssb +"/("+k+"-1)]/[" +ssw+"/("+n+"-"+k+")]= "+
						 + value);
				valueList.put(slKClustering, value);
				scList.add(value);
				k++;
			}
		} else if(formula==2)  {
			System.out.println("Hartigan   H=log(SSB/SSW)   "+methodnameString );
			for (int i = 0; i < sswArray.length; i++) {
				Double ssw = (Double) sswArray[i];
				Double ssb = (Double) ssbArray[i];
				
				if (  ssw!=0) {										
				 value =Math.log(ssb/ssw);
			
				}else{
					value=0;
				}
				Set<Set<String>> slKClustering = dendrogram.partitionK(i + 1);
				if(i!=0)
				System.out.println(i+1+".  "+slKClustering +"  stop criteria= "+ "log("+ssb+"/"+ssw+") =  "+value );
				valueList.put(slKClustering, value);
				scList.add(value);
			}
			

		}else if (formula==3) {
			System.out.println("WB-index   WB=m*SSW/SSB   "+methodnameString );
			int k = 1;
			for (int i = 0; i < sswArray.length; i++) {
				Double ssw = (Double) sswArray[i];
				Double ssb = (Double) ssbArray[i];

				if (ssb != 0 ) {
					
				 value = ssw / ssb;
//				 value = ssw / ssb;
			
				} else {
					value = 0;
				}
				Set<Set<String>> slKClustering = dendrogram.partitionK(i + 1);
				if(i!=0)
				System.out.println(k+". "+slKClustering + " stop criteria =" + k + "*" + ssw
						+ "/" + ssb + "= " + value);
				valueList.put(slKClustering, value);
				scList.add(value);
				k++;
			}
			
		}else {
			value=0;
			
		}
		
		int k = 0;
		for (int i = 0; i < sswArray.length; i++) 
		{
			k = i +1;
			System.out.println(k+" "+scList.get(i)+";");
		}
		
		
		return valueList;

	}
	
	public List<Double> getSSBList(Set<String> inputSet, String type,
			int method, String normalized) throws IOException {
//		Clustering_own clustering_own = new Clustering_own();
//		WordnetSimilarity wordnetSimilarity = new WordnetSimilarity();

		// Dendrogram<String> dendrogram = clustering_own
		// .single_linkage_clustering(inputSet, type);
		
		Dendrogram<String> dendrogram = single_linkage_clustering_method(inputSet, type, method);

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
							
							double similarityValue = getSimilarity(temp1String, temp2String, method, type); 
							double distanceIJ = (1-similarityValue);

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

	public List<Double> getSSBListDetail(Set<String> inputSet, String type,
			int method, String normalized) throws IOException {
//		Clustering_own clustering_own = new Clustering_own();
//		WordnetSimilarity wordnetSimilarity = new WordnetSimilarity();

		// Dendrogram<String> dendrogram = clustering_own
		// .single_linkage_clustering(inputSet, type);
		
		Dendrogram<String> dendrogram = single_linkage_clustering_method(inputSet, type, method);

		List<Double> ssb = new ArrayList<Double>();
		for (int k = 1; k <= dendrogram.size(); ++k) {
			double ssbTotal = 0;
			Set<Set<String>> slKClustering = dendrogram.partitionK(k);
			Object[] objArray = slKClustering.toArray();

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
							
							double similarityValue = getSimilarity(temp1String, temp2String, method, type); 
							double distanceIJ = 1-similarityValue;

							distancePairList.add(distanceIJ);
						}
					}
					Collections.sort(distancePairList);
					Object[] distancePairArray = distancePairList.toArray();
					Double ssbDistance = (Double) distancePairArray[0];
					
					System.out.println(sI + " " + sJ + " ssb distance is: "
							+ ssbDistance);
					int sI_size = sI.size();
					int sJ_size = sJ.size();
					if (sI_size > 1 && sJ_size > 1) {
						ssbTotal += ssbDistance;
						normalizationFactor += 1;
					}
				}

			}
//			if((normalized.trim().equalsIgnoreCase("y")) && (normalizationFactor != 0) )
//				ssbTotal /= normalizationFactor;

			// System.out.println(k + "  " + slKClustering + ". ssw is: "+
			// totaldistance);
			System.out.println(slKClustering + " ssbTotal distance is: "
					+ ssbTotal);
			ssb.add(ssbTotal);
			System.out.println("----------------------------------------------------------");
		}

		return ssb;
		// System.out.println(k + "  " + slKClustering);
	}



	public List<Double> getSSWListDetail(Set<String> inputSet, String type,
			int method, String normalized) throws IOException {
	

		// Dendrogram<String> dendrogram = clustering_own
		// .single_linkage_clustering(inputSet, type);

		Dendrogram<String> dendrogram = single_linkage_clustering_method(inputSet, type, method);

		System.out.println("\nSingle Link Clusterings");

		List<Double> ssw = new ArrayList<Double>();
		for (int k = 1; k <= dendrogram.size(); ++k) {
			double totaldistance = 0;
			Set<Set<String>> slKClustering = dendrogram.partitionK(k);
			int normalizationFactor = 0;
			for (Set<String> set : slKClustering) {
				int setSize = set.size();
				if (setSize > 1) {
					String[] arrayStrings = setToArray(set);

					// �õ����е�pair ��distance�� �ŵ�pairscore ��list ����
					for (int i = 0; i < arrayStrings.length; i++) {
						String sI = arrayStrings[i];

						for (int j = i + 1; j < arrayStrings.length; j++) {
							String sJ = arrayStrings[j];
							// double distanceIJ=
							// wordnetSimilarity.getWordnetSimilarityAll(sI,
							// sJ,type);
							double similarityValue = getSimilarity(sI, sJ, method, type); 
							double distanceIJ=1- similarityValue;
							System.out.println(sI + "  " + sJ
									+ " the distance is: " + distanceIJ);
							totaldistance += distanceIJ;
							normalizationFactor += 1;
						}
					}
				} else {
					totaldistance += 0;
				}
				
				
			}
			if((normalized.trim().equalsIgnoreCase("y")) && (normalizationFactor != 0) )
				totaldistance /= normalizationFactor;
			
			// System.out.println(k + "  " + slKClustering);
			ssw.add(totaldistance);
			System.out.println(k + "  " + slKClustering + ". ssw is: "
					+ totaldistance);
			System.out
					.println("-----------------------------------------------------------------------------------------------------");
		}

		return ssw;
		// System.out.println(k + "  " + slKClustering);
	}
	
	public List<Double> getSSWList(Set<String> inputSet, String type,
			int method, String normalized) throws IOException {
	

		// Dendrogram<String> dendrogram = clustering_own
		// .single_linkage_clustering(inputSet, type);

		Dendrogram<String> dendrogram = single_linkage_clustering_method(inputSet, type, method);

//		System.out.println("\nSingle Link Clusterings");

		List<Double> ssw = new ArrayList<Double>();
		for (int k = 1; k <= dendrogram.size(); ++k) {
			double totaldistance = 0;
			double totaldistance_temp = 0;
			Set<Set<String>> slKClustering = dendrogram.partitionK(k);
			int normalizationFactor = 0;
			double maxdistanceAll = 0;
			int numSingleObject = 0;
			for (Set<String> set : slKClustering) {
				int setSize = set.size();
//				normalizationFactor = 0;
				double maxdistance = 0;
				double distanceIJ;
				String[] arrayStrings = setToArray(set);
				if (setSize > 1) {
					
					totaldistance_temp = 0;
					// �õ����е�pair ��distance�� �ŵ�pairscore ��list ����
					for (int i = 0; i < arrayStrings.length; i++) {
						String sI = arrayStrings[i];

						for (int j = i + 1; j < arrayStrings.length; j++) {
							String sJ = arrayStrings[j];
							// double distanceIJ=
							// wordnetSimilarity.getWordnetSimilarityAll(sI,
							// sJ,type);
							double similarityValue = getSimilarity(sI, sJ, method, type); 
							distanceIJ=(1- similarityValue);
//							System.out.println(sI + "  " + sJ
//									+ " the distance is: " + distanceIJ);
//							totaldistance += distanceIJ;
							if ( distanceIJ > maxdistance )
								maxdistance = distanceIJ;
							
							
						}
					}
					normalizationFactor += 1;
//					totaldistance += totaldistance_temp;
//					totaldistance_temp /= normalizationFactor;
//					totaldistance += totaldistance_temp;
				} else {
					totaldistance += 0;
					numSingleObject += 1/arrayStrings.length;
				}
				
				if ( maxdistance > maxdistanceAll)
					maxdistanceAll = maxdistance;
				totaldistance += maxdistance;
				
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





	public String[] setToArray(Set<String> set) {

		// ����������ĳ���С��list��Ԫ�ظ���ʱ�����Զ���������ĳ�������Ӧlist�ĳ���
		String[] strArray = (String[]) set.toArray(new String[0]);
		return strArray;

	}
	
	public Dendrogram<String> single_linkage_clustering_method(
			Set<String> elementSet, String type, int method)
			throws IOException {
		// Double maxDistance=Double.MAX_VALUE;

		// ���ַ�����������
		if (elementSet.size() == 0) {
			String msg = "Require non-empty set to form dendrogram."
					+ " Found elementSet.size()=" + elementSet.size();
			throw new IllegalArgumentException(msg);
		}

		// ����һ��LeafDendrogram ���󣬰�һ������
		if (elementSet.size() == 1) {
			return new LeafDendrogram<String>(elementSet.iterator().next());
		}

		// Object[] elements = toElements(elementSet);
		// set ����elementSet ������� elements
		String[] elements = new String[elementSet.size()];
		int setToArrayIndex = 0;
		for (Iterator iterator = elementSet.iterator(); iterator.hasNext();) {
			elements[setToArrayIndex] = (String) iterator.next();
			setToArrayIndex++;
		}

		/*
		 * test for array elements for (int i = 0; i < elements.length; i++) {
		 * System.out.println(elements[i]); }
		 */

		//

		// ���� elements ��� LeafDendrogram���� array
		LeafDendrogram<String>[] leafs = (LeafDendrogram<String>[]) new LeafDendrogram[elements.length];
		for (int i = 0; i < elements.length; i++) {
			leafs[i] = new LeafDendrogram<String>(elements[i]);
			// System.out.println("The leafs are: "+leafs[i]);

		}

		// LeafDendrogram���� ��� set ���� װ��ϵͳ���ε�����
		Set<Dendrogram<String>> clusters = new HashSet<Dendrogram<String>>(
				elements.length);
		for (Dendrogram<String> dendrogram : leafs) {
			clusters.add(dendrogram);
		}
		/*
		 * test for set clusters for (Dendrogram<String> dendrogram : clusters)
		 * { System.out.println("the each dendrogram is: "+dendrogram); }
		 */

		// pairscore ��һ�����췽��������Dendrogram�� ��һ��score
		ArrayList<PairScore<String>> pairScoreList = new ArrayList<PairScore<String>>();
		int len = elements.length;
		double maxDistance = Double.MAX_VALUE;
	

		// �õ����е�pair ��distance�� �ŵ�pairscore ��list ����
		for (int i = 0; i < len; ++i) {
			String sI = elements[i];
			Dendrogram<String> dendroI = leafs[i];
			for (int j = i + 1; j < len; ++j) {
				String sJ = elements[j];
				// double distanceIJ=
				// wordnetSimilarity.getWordnetSimilarityAll(sI, sJ,type);
				// double distanceIJ=wordnetSimilarity.getWuWordDistanceAll(sI,
				// sJ, type);
	//1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein    
	//String similariString = getSimilarity(word1, word2, method, type);
				double distanceIJ = 0;
				double similariyValue=getSimilarity(sI, sJ, method, type);
				distanceIJ=1-similariyValue;
		

//				System.out.println(sI + "  " + sJ + " the distance is: "
//						+ distanceIJ);
				if (distanceIJ > maxDistance)
					continue;
				Dendrogram<String> dendroJ = leafs[j];
				pairScoreList.add(new PairScore<String>(dendroI, dendroJ,
						distanceIJ));
			}
		}

		/*
		 * //��ӡ pairScore ps(chicken,car:50.0) ps(chicken,orange:40.0) for
		 * (PairScore<String> pairScore : pairScoreList) {
		 * System.out.println(pairScore); }
		 */

		// pairscore ��С��������ps(car,jeep:0.0) ps(cat,woman:4.76)
		PairScore<String>[] pairScores = (PairScore<String>[]) new PairScore[pairScoreList
				.size()];
		pairScoreList.toArray(pairScores);
		Arrays.sort(pairScores, ScoredObject.comparator()); // increasing order
															// of distance

		/*
		 * //���� pairscore ��С��������
		 * 
		 * System.out.println("\nAfter sort"); for (int i = 0; i <
		 * pairScores.length; i++) { System.out.println(pairScores[i]); }
		 */
//		System.out.println("----------------------------");
		for (int i = 0; i < pairScores.length && clusters.size() > 1; ++i) {
			PairScore<String> ps = pairScores[i];
			if (ps.score() > Double.MAX_VALUE)
				break;
			// ps.mDendrogram1 �����ps ��ĵ�һ��LeafDendrogram dereference �������parent
			Dendrogram<String> d1 = ps.mDendrogram1.dereference();
			Dendrogram<String> d2 = ps.mDendrogram2.dereference();

//			System.out.println(ps.mDendrogram1 + " d1 dereference: " + d1);
//			System.out.println(ps.mDendrogram2 + " d2 dereference: " + d2);
//			System.out.println("-------------------------------");

			if (d1.equals(d2)) {
				continue; // already linked
			}
			clusters.remove(d1);
			clusters.remove(d2);
			// ��d1�� d2 ��parent ���ó�����ɵ� �з�֧��ϵͳ���� dlink�� pairScores[i].mScore
			// ��distance����
			LinkDendrogram<String> dLink = new LinkDendrogram<String>(d1, d2,
					pairScores[i].mScore);
			clusters.add(dLink);
		}

		// link up remaining unlinked dendros at +infinity distance
		Iterator<Dendrogram<String>> it = clusters.iterator();
		Dendrogram<String> dendro = it.next(); // skip first -
												// self,���źõ�ϵͳ���ξ�һ��������У����õĺ�û���ŵ���
		while (it.hasNext())
			dendro = new LinkDendrogram<String>(dendro, it.next(),
					Double.POSITIVE_INFINITY);
		return dendro;
		// return null;
	}
	
	public Double getSimilarity(String word1, String word2, int method, String type){
		double similarityValue = 0;
		//1.Jiang 2.Wu 3.Lin 4.Path 
		if (method ==1 || method ==2 || method ==3 || method==4) {
		
			similarityValue = wSimilarity.getWordnetSimilarity(word1, word2, type, method);
			if (similarityValue>1){
				similarityValue=1;
			}else if (similarityValue<0){
				similarityValue=0;
			}

		} else{
			System.out.println("Sorry, no this method!!!");
			similarityValue=-1;
		}
		java.text.DecimalFormat   df=new   java.text.DecimalFormat("#0.00"); 
		String similarityValue1 = df.format(similarityValue);
		double similarityDouble=Double.parseDouble(similarityValue1.replace(",", "."));
		return similarityDouble;
	}

}


