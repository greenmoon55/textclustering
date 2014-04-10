package fi.uef.cs;

public class ShortTextSimilarity {

	/**
	 * @param args
	 */
	
	private static double getSimilarityDouble(String str1, String str2, int method, String type) {
		double res = SimilarityMetric.getSimilarityDouble(str1, str2, method, type);
		System.out.println(str1 + " " + str2 + " " + res);
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimilarityMetric similariyMetric = new SimilarityMetric();
		String str1 = "Ships race to reach site where electronic pulses detected in Malaysia jet search";
		//String str2 = "Crews Track New Pulse Signals";
		String str2 = "NASA released a spectacular video on Sunday from its Solar Dynamics Observatory of a flare erupting from the sun";
		String[] str1Array = str1.split(" ");
		String[] str2Array = str2.split(" ");
		double sum = 0;
		for (int i = 0; i < str1Array.length; i++) {
			for (int j = 0; j < str2Array.length; j++) {
				sum += getSimilarityDouble(str1Array[i], str2Array[j], 4, "n");
			}
		}
		System.out.println(sum);
	}

}
