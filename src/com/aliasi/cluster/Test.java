package com.aliasi.cluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Set<String> inputSet = new HashSet<String>();
	      for (String s : args[0].split(",")) 
	            inputSet.add(s);
	      
	     Clustering_own clustering_own=new Clustering_own();
	//     Dendrogram<String> dendrogram=clustering_own.single_linkage_clustering(inputSet, "n");
	     
	        System.out.println("\nSingle Link Dendrogram");
//	        System.out.println(dendrogram.prettyPrint());
	        
	}

}
