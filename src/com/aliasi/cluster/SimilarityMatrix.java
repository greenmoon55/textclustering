package com.aliasi.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.FileInputStream;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.Path;
import fi.uef.cs.WordnetSimilarity;

/**
 * 
 * @author Zhao Qinpei
 * 1. read data, which is short text, i.e., one or more than two words as one text/concept?
 *    there are N texts. make sure pre-processing the data first
 * 2. calculate pairwise similarity between each text/concept
 * Input: text data size of N
 * Output: a similarity matrix N*N, "better to be a txt file"
 * purpose: the similarity matrix is calculated to be further used in spectral clustering 
 * in Matlab. 
 */

public class SimilarityMatrix {
	static WordnetSimilarity wSimilarity=new WordnetSimilarity();
	
	public static void main(String[] args)
	{
		/*
		// change to reading file
		System.out.println("Please input words list and separate them by \",\": ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String inputString = in.readLine();
		
		// store the strings
		Set<String> inputSet = new HashSet<String>();
		for (String s : inputString.split(","))
			inputSet.add(s);
		*/
		
		 
		
		 String path = "D:/Past_Finland/Work/textclustering/file4.txt";
		 String outPath = "D:/Past_Finland/Work/textclustering/simiMatrix.txt";
		 ArrayList<String> list = readTxtFile(path);
		 
		 int length = list.size();
		 double [][] SM = new double[length][length];
		 
		 for(int i = 0; i < length; i++)
		 {
			 String t1 = list.get(i);
			 for(int j = 0; j < length; j++)
			 {
				 String t2 = list.get(j);
				 SM[i][j] = similarity2String(t1, t2);
			 }
		 }
		 
		 // write the similarity matrix into txt file
		 writeTxtFile(outPath, SM);
		
	}
	
	//calculate the similarity between two strings, e.g., t1 = "car accident" & t2 = "bar coffee drink"
	public static double similarity2String(String t1, String t2){

		
		//transfer two strings into two arrays
		ArrayList<String> strList1 = new ArrayList<String>();
		for (String s : t1.split(" "))
			strList1.add(s);
		
		ArrayList<String> strList2 = new ArrayList<String>();
		for (String t : t2.split(" "))
			strList2.add(t);
		
		//now calculate the similarity (pairwise maximum)
		String word1, word2;
		double maxSim = 0.0;
		
		
		for( int i = 0; i < strList1.size(); i++ )
		{
			word1 = strList1.get(i);
			for( int j = 0; j < strList2.size(); j++ )
			{
				word2  = strList2.get(j);

				double temp = wSimilarity.getWordnetSimilarity(word1, word2, "n", 1);
				if( temp > maxSim)
					maxSim = temp;				
			}
		}
		
		return maxSim;		
	}
	
	//write txt file
	public static void writeTxtFile(String filePath, double SM [][]){
		
		try{
			File file = new File(filePath);
			FileWriter writer = new FileWriter(file);
			for( int i = 0; i < SM.length; i++)
			{
				for( int j = 0; j < SM[i].length; j++)
				{
					writer.write(SM[i][j]+"\t");
				}
				writer.write("\n");
			}
			writer.close();

		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	// read txt file
	public static ArrayList<String> readTxtFile(String filePath){
		
		ArrayList<String> list = new ArrayList<String>();
		try{
			
			File file = new File(filePath);
			if(file.isFile() && file.exists()){
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine())!= null){
					//System.out.println(lineTxt);
					list.add(lineTxt);			
				}
				read.close();
			}
			else{
				System.out.println("Cannot find the file!");
			}
			
		}catch (Exception e){
			System.out.println("Something wrong with reading the file");
			e.printStackTrace();
		}
		return list;
	}
	
	

}

