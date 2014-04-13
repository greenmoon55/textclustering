package fi.uef.cs;

public class ShortTextSimilarity {

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimilarityMetric similariyMetric = new SimilarityMetric();
		String str1 = "cafe,cafeteria,club,coffee,train,railway,station";
		//String str2 = "Crews Track New Pulse Signals";
		String str2 = "cafe,pizza,restaurant";
		String[] str1Array = str1.split(",");
		String[] str2Array = str2.split(",");
		double sum = 0;
		for (int i = 0; i < str1Array.length; i++) {
			for (int j = 0; j < str2Array.length; j++) {
				double similarity = similariyMetric.getSimilarity(str1Array[i], str2Array[j], SimilarityMetric.Method.Jiang, "n");
				System.out.println(str1Array[i] + " " + str2Array[j] + ": " + similarity);
			}
		}
		System.out.println(sum);
	}

}
