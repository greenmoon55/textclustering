package com.aliasi.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.AbstractHierarchicalClusterer.PairScore;
import com.aliasi.util.BoundedPriorityQueue;
import com.aliasi.util.ObjectToSet;
import com.aliasi.util.ScoredObject;

import fi.uef.cs.WordnetSimilarity;

public class Clustering_own {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
        // parse out input set
		
		String inputStrings ="pen,pencil,bus,car,dog,cat";
        Set<String> inputSet = new HashSet<String>();
        //aa,aaa,bbb,bb,aabb,bbaa   当中没有空格
        //for (String s : args[0].split(",")) {
        for (String s : inputStrings.split(",")) {
            inputSet.add(s);
        //System.out.println(s);
        }
        Clustering_own clustering_own=new Clustering_own();
        Dendrogram<String> dendrogram_single= clustering_own.single_linkage_clustering(inputSet,"n",2);
//        (Set<String> elementSet,String type, int method)
        //1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein
        Dendrogram<String> dendrogram_complete= clustering_own.complete_linkage_clustering(inputSet,"n",2);
        
//        System.out.println(dendrogram_single.prettyPrint());
        System.out.println("\nSingle Link Clusterings llllllllllllllll");
        for (int k = 1; k <= dendrogram_single.size(); ++k) {
            Set<Set<String>> slKClustering = dendrogram_single.partitionK(k);
            System.out.println(k + "  " + slKClustering);
        }
        
        //partitionDistance is [[orange], [car, jeep, man, bicycle]] 在distacne 30内的聚类
        Set<Set<String>> slKClustering=dendrogram_single.partitionDistance(30);
        System.out.println(" partitionDistance within 30 is " + slKClustering);
        //LeafDendrogram 的属性
         //[a] dendrogram.memberSet()   
        //a dendrogram
        //System.out.println(dendrogram.memberSet());
        
//        System.out.println(dendrogram_complete.prettyPrint());
        System.out.println("\nComplete Link Clusterings");
        for (int k = 1; k <= dendrogram_complete.size(); ++k) {
            Set<Set<String>> completeKClustering = dendrogram_single.partitionK(k);
            System.out.println(k + "  " + completeKClustering);
        }
        
        //partitionDistance is [[orange], [car, jeep, man, bicycle]] 在distacne 30内的聚类
        Set<Set<String>> completeKClustering=dendrogram_single.partitionDistance(30);
        System.out.println(" partitionDistance is " + completeKClustering);
        
	}
	


	
	/* 
	 * type: n noun   v verb
	 * method: 1.Jiang 2.Wu 3.Lin 4.Path 5.Levenshtein
	 */	
	public Dendrogram<String> single_linkage_clustering(Set<String> elementSet,String type, int method) throws IOException {
		WordnetSimilarity wSimilarity=new WordnetSimilarity();
		
		//Double maxDistance=Double.MAX_VALUE;
		
		//空字符串的情况，报错
		
		if (elementSet.size() == 0) {
			String msg = "Require non-empty set to form dendrogram."
					+ " Found elementSet.size()=" + elementSet.size();
			throw new IllegalArgumentException(msg);
		}
		
		//建立一个LeafDendrogram 对象，包含一个对象
		 if (elementSet.size() == 1){
	            return new LeafDendrogram<String>(elementSet.iterator().next());
		 }
		 
		 //Object[] elements = toElements(elementSet);
		 //set 容器elementSet 变成数组 elements
		 String[] elements = new String[elementSet.size()];		 
		 int setToArrayIndex=0;
		 for (Iterator iterator = elementSet.iterator(); iterator.hasNext();) {
			elements[setToArrayIndex] = (String) iterator.next();
			setToArrayIndex++;
		}
		 
		 /*	test for array elements	 
		 for (int i = 0; i < elements.length; i++) {
				System.out.println(elements[i]);
			}*/
		 
		//
		 
	
		//数组 elements 变成 LeafDendrogram数组 array
	     LeafDendrogram<String>[] leafs = (LeafDendrogram<String>[]) new LeafDendrogram[elements.length]; 
		for (int i = 0; i < elements.length; i++) {
			leafs[i]= new LeafDendrogram<String>(elements[i]);
		//	System.out.println("The leafs are: "+leafs[i]);
			
		}
		
		//LeafDendrogram数组 变成 set 容器 装满系统树形的容器
		Set<Dendrogram<String>> clusters =new HashSet<Dendrogram<String>>(elements.length);
		for (Dendrogram<String> dendrogram : leafs) {
			clusters.add(dendrogram);
		}
		/* test for set clusters
		for (Dendrogram<String> dendrogram : clusters) {
			System.out.println("the each dendrogram is: "+dendrogram);
		}
		 */

		 //pairscore 有一个构造方法，二个Dendrogram， 加一个score
		 ArrayList<PairScore<String>> pairScoreList = new ArrayList<PairScore<String>>();
		 int len = elements.length;
		 double maxDistance = Double.MAX_VALUE;
		 WordnetSimilarity wordnetSimilarity=new WordnetSimilarity();
		 
		 
		 //method
		 
		 //得到所有的pair 的distance， 放到pairscore 的list 里面
		 for (int i = 0; i < len; ++i) {
			 String sI=elements[i];
			 Dendrogram<String> dendroI = leafs[i];
			 for (int j = i + 1; j < len; ++j) {
				 String sJ=elements[j];
//				 double distanceIJ=wordnetSimilarity.getWordDistanceAll(sI, sJ, type);
				 double similarity = wSimilarity.getWordnetSimilarity(sI, sJ, type, method);
				 double distanceIJ = 1- similarity;
				 if (distanceIJ > maxDistance) continue;
				 Dendrogram<String> dendroJ=leafs[j];
				 pairScoreList.add(new PairScore<String>(dendroI,dendroJ,distanceIJ));
			 }	 
		 }
		 		 
	/*	 //打印   pairScore  ps(chicken,car:50.0)    ps(chicken,orange:40.0) 
		 for (PairScore<String> pairScore : pairScoreList) {
			System.out.println(pairScore);
		}
	*/
		 
		 //pairscore 从小到大排列ps(car,jeep:0.0)  ps(cat,woman:4.76)
		 PairScore<String>[] pairScores=(PairScore<String>[]) new PairScore[pairScoreList.size()];
		 pairScoreList.toArray(pairScores);
		 Arrays.sort(pairScores,ScoredObject.comparator());  // increasing order of distance

		//测试  pairscore 从小到大排列 
			System.out.println("single linkage pairscore"); 		 
		 for (int i = 0; i < pairScores.length; i++) {
			 System.out.println(""); 
			System.out.println(pairScores[i]);
		}
		 

//		 System.out.println("----------------------------"); 
        for (int i = 0;  i < pairScores.length && clusters.size() > 1;  ++i) {
            PairScore<String> ps = pairScores[i];
            if (ps.score() > Double.MAX_VALUE) break;
            //ps.mDendrogram1 是组成ps 里的第一个LeafDendrogram  dereference 返回它的parent
            Dendrogram<String> d1 = ps.mDendrogram1.dereference();
            Dendrogram<String> d2 = ps.mDendrogram2.dereference();
       
//            System.out.println(ps.mDendrogram1+ " d1 dereference: "+d1);
//            System.out.println(ps.mDendrogram2+" d2 dereference: "+d2);
//            System.out.println("-------------------------------");
            
            if (d1.equals(d2)) {
                continue; // already linked
            }
            clusters.remove(d1);
            clusters.remove(d2);
            //把d1， d2 的parent 设置成新生成的 有分支的系统树形 dlink， pairScores[i].mScore 是distance数字
            LinkDendrogram<String> dLink = new LinkDendrogram<String>(d1,d2,pairScores[i].mScore);
            clusters.add(dLink);
        }
		
        // link up remaining unlinked dendros at +infinity distance
        Iterator<Dendrogram<String>> it = clusters.iterator();
        Dendrogram<String> dendro = it.next(); // skip first - self,被排好的系统树形就一个，如果还有，连好的和没连号的连
        while (it.hasNext())
            dendro = new LinkDendrogram<String>(dendro,it.next(),
                                           Double.POSITIVE_INFINITY);
        return dendro;
		//return null;
	}
	
	public Dendrogram<String> complete_linkage_clustering(Set<String> elementSet,String type, int method) throws IOException {
		//空字符串的情况，报错
		WordnetSimilarity wSimilarity=new WordnetSimilarity();
		if (elementSet.size() == 0) {
			String msg = "Require non-empty set to form dendrogram."
					+ " Found elementSet.size()=" + elementSet.size();
			throw new IllegalArgumentException(msg);
		}
		
		//建立一个LeafDendrogram 对象，包含一个对象
		 if (elementSet.size() == 1){
	            return new LeafDendrogram<String>(elementSet.iterator().next());
		 }
		 
      // create queue (reverse because lower is better for distances)， 区别
        BoundedPriorityQueue<PairScore<String>> queue
            = new BoundedPriorityQueue<PairScore<String>>(ScoredObject.reverseComparator(),Integer.MAX_VALUE);
        
        //数组， key 是 系统树形， pairScore  区别
        ObjectToSet<Dendrogram<String>,PairScore<String>> index = new ObjectToSet<Dendrogram<String>,PairScore<String>>();
        
   	 	 //Object[] elements = toElements(elementSet);
		 //set 容器elementSet 变成数组 elements
		 String[] elements = new String[elementSet.size()];		 
		 int setToArrayIndex=0;
		 for (Iterator iterator = elementSet.iterator(); iterator.hasNext();) {
			elements[setToArrayIndex] = (String) iterator.next();
			setToArrayIndex++;
		}
		 
		//数组 elements 变成 LeafDendrogram数组 array
	    LeafDendrogram<String>[] leafs = (LeafDendrogram<String>[]) new LeafDendrogram[elements.length]; 
		for (int i = 0; i < elements.length; i++) {
			leafs[i]= new LeafDendrogram<String>(elements[i]);
		//	System.out.println("The leafs are: "+leafs[i]);
			
		}
		
		 double maxDistance = Double.MAX_VALUE;
		 WordnetSimilarity wordnetSimilarity=new WordnetSimilarity();
		 
        for (int i = 0; i < elements.length; ++i) {
            String sI = elements[i];
            LeafDendrogram<String> dI = leafs[i];
            for (int j = i + 1; j < elements.length; ++j) {
                String sJ = elements[j];
                //double score = distance().distance(eI,eJ);
     //           double score=wordnetSimilarity.getWordDistanceAll(sI, sJ, type);
                
                double similarity = wSimilarity.getWordnetSimilarity(sI, sJ, type, method);
                double score =1- similarity;
                if (score > maxDistance) continue;
                LeafDendrogram<String> dJ = leafs[j];
                PairScore<String> psIJ = new PairScore<String>(dI,dJ,score);
                queue.offer(psIJ);
                index.addMember(dI,psIJ);
                index.addMember(dJ,psIJ);
            }
        }
	        
        while (queue.size() > 0) {
            PairScore<String> next = queue.poll();
            Dendrogram<String> dendro1 = next.mDendrogram1.dereference();
            Dendrogram<String> dendro2 = next.mDendrogram2.dereference();
            double dist12 = next.score();
            LinkDendrogram<String> dendro12
                = new LinkDendrogram<String>(dendro1,dendro2,dist12);

            // remove & store distances to dendro1
            HashMap<Dendrogram<String>,Double> distanceBuf
                = new HashMap<Dendrogram<String>,Double>();
            Set<PairScore<String>> ps3Set = index.remove(dendro1);
            queue.removeAll(ps3Set);
            for (PairScore<String> ps3 : ps3Set) {
                Dendrogram<String> dendro3
                    = ps3.mDendrogram1 == dendro1
                    ? ps3.mDendrogram2
                    : ps3.mDendrogram1;
                index.get(dendro3).remove(ps3);
                double dist1_3 = ps3.score();
                distanceBuf.put(dendro3,Double.valueOf(dist1_3));
            }

            // remove & iterate over distances to dendro2
            ps3Set = index.remove(dendro2);
            queue.removeAll(ps3Set);
            for (PairScore<String> ps3 : ps3Set) {
                Dendrogram<String> dendro3
                    = ps3.mDendrogram1 == dendro2
                    ? ps3.mDendrogram2
                    : ps3.mDendrogram1;
                index.get(dendro3).remove(ps3);
                Double dist1_3D = distanceBuf.get(dendro3);
                if (dist1_3D == null) continue; // dist(dendro2,dendro3) too large
                double dist1_3 = dist1_3D.doubleValue();
                double dist2_3 = ps3.score();
                double dist12_3 = Math.max(dist1_3,dist2_3);
                PairScore<String> ps = new PairScore<String>(dendro12,dendro3,dist12_3);
                queue.offer(ps);
                index.addMember(dendro12,ps);
                index.addMember(dendro3,ps);
            }
            // dendro3 must be linked above threshold
            // by both dendro1 and dendro2
            if (queue.isEmpty()) return dendro12;
        }
		
     // share following code with Single Link
        Iterator<Dendrogram<String>> it = index.keySet().iterator();
        Dendrogram<String> dendro = it.next(); // skip first element -- self
        while (it.hasNext())
            dendro = new LinkDendrogram<String>(dendro,it.next(),
                                           Double.POSITIVE_INFINITY);
        return dendro;
		
	
	} 
	


}
