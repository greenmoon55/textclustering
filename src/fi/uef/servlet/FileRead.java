package fi.uef.servlet;

import java.io.*;
import java.util.*;

import fi.uef.cs.WordnetSimilarity;

class FileRead {
	public static void main(String args[]) {
		List<String> array1=new ArrayList<String>();
		try {
			String encoding = "utf-8";
			File file = new File("D:/hao/×ÀÃæ/lists/keyword_en.txt");
		array1=new ArrayList<String>();
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
					new FileInputStream(file), encoding);
					BufferedReader bufferedReader = new BufferedReader(read);
					String lineTXT = null;
					while ((lineTXT = bufferedReader.readLine()) != null) {
						array1.add(lineTXT);
					}

					read.close();
			}
		} catch (Exception e) {// Catch exception if any
			System.out.println("Error: " + e.getMessage());
		}
		
		for (int i = 0; i < array1.size(); i++) {
			System.out.println(array1.get(i));
			
		}
		
        HashMap<Double, String> hm = new HashMap<Double, String>();
        WordnetSimilarity wordnetSimilarity= new WordnetSimilarity();
        Double similarityValue=null;
        String wordtemp="";
        
        for(int i=0; i<array1.size();i++){
        	wordtemp=array1.get(i);
        	try {
				similarityValue=wordnetSimilarity.getWordnetSimilarityAll("pizza",wordtemp,"n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	hm.put(new Double(similarityValue), wordtemp);
        }
       
        Set set = hm.entrySet();
        System.out.println(set.size());
        Iterator it = set.iterator();
        Map.Entry<Double, String> entry = null;
        while(it.hasNext()) {
        	entry=(Map.Entry<Double, String>) it.next();
           	System.out.println(entry.getKey()+"    "+entry.getValue()+"<br>");
        }
	}
}
