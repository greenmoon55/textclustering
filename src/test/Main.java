/**
 * Spectral Clustering : Main
 * --------------------------
 * Programma voor het testen van de verschillende clusteringsalgoritmen en
 * voor het uitproberen van verschillende parameterwaarden. Het is mogelijk om 
 * zelf het programma te completeren door extra parameterwaarden toe te voegen,
 * maar dan moet ook de rest van dit testprogramma conform gewijzigd worden.
 *
 * @author Uyttersprot Bram
 */

package test;

import java.io.File;
import java.util.Scanner;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.DensityBasedSpatialClustering;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.Normalized;
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.clustering.Unnormalized;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

public class Main {

	private static String[] datasets = { "iris.csv", "movement.data", "data.csv" };
	private static int[] index_classlabels = { 4, -1, -1 }; // index of class labels
	/*
	 * In the above array "-1" means that the corresponding dataset (in the
	 * array 'datasets') no index of the class label was specified.       
	 * The above two arrays must always have the same number of elements in order to
	 * guarantee it works properly.
	 */

	private static String[] algorithms = { "Density Based Spatial Clustering",
			"K-Means Clustering", "SOM (Self-Organizing Map)",
			"Normalized Spectral Clustering",
			"Unnormalized Spectral Clustering" };
	private static String[] distances = { "Cosine Distance",
			"Euclidean Distance", "Manhattan Distance", "Norm Distance" };

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		String input = "";

		/* Retrieve data */
		System.out.println("Which dataset do you use?");
		for (int i = 0; i < datasets.length; i++)
			System.out.println(i + " : " + datasets[i]);
		System.out.print("Enter the number of your choice: ");
		input = scan.nextLine();

		/* Load dataset */
		Dataset data;
		if (index_classlabels[Integer.parseInt(input)] < 0) {
			data = FileHandler.loadDataset(
					new File(datasets[Integer.parseInt(input)]), ",");
		} else {
			data = FileHandler.loadDataset(
					new File(datasets[Integer.parseInt(input)]),
					index_classlabels[Integer.parseInt(input)], ",");
		}

		/* Clustering algorithm selection */
		System.out.println("Which clustering algorithm would you use?");
		for (int i = 0; i < algorithms.length; i++)
			System.out.println(i + " : " + algorithms[i]);
		System.out.print("Enter the number of your choice: ");
		int choice = Integer.parseInt(scan.nextLine());

		/*
		 * When the user selects spectral clustering, additional parameters must
		 * be set
		 */
		int number_of_clusters = 4;
		if (choice == 3 || choice == 4) {
			System.out.println("Spectral Clustering algorithms should be invoked "
					+ "with the desired number of clusters as a parameter.");
			System.out.print("How many clusters do you want? ");
			number_of_clusters = Integer.parseInt(scan.nextLine());
			System.out.println("Finally you need to select the distance measure");
			for (int i = 0; i < distances.length; i++)
				System.out.println(i + " : " + distances[i]);
			System.out.print("Enter the number of your choice: ");
			input = scan.nextLine();
		}

		Clusterer cl;
		switch (choice) {
		case 0:
			cl = new DensityBasedSpatialClustering();
			break;
		case 1:
			cl = new KMeans();
			break;
		case 2:
			cl = new SOM();
			break;
		case 3:
			cl = new Normalized(number_of_clusters, Integer.parseInt(input));
			break;
		case 4:
			cl = new Unnormalized(number_of_clusters, Integer.parseInt(input));
			break;
		default:
			cl = new KMeans();
		}
		
		for (int i = 0; i < data.size(); i++) {
        	System.out.println(data.get(i));
        }

		/* The actual clustering of the data */
		Dataset[] clusters = cl.cluster(data);
		
		for (int i = 0; i < clusters.length; i++) {
			FileHandler.exportDataset(clusters[i],new File("output" + i + ".txt"));
		}
		/* Print the number of clusters found */
		System.out.println("Number of clusters: " + clusters.length);
		/* Create object for the evaluation of the clusters */
		ClusterEvaluation eval;
		/* Measuring the quality of the clusters (multiple measures) */
		eval = new SumOfSquaredErrors();
		System.out.println("Score according to SumOfSquaredErrors: "
				+ eval.score(clusters));
		eval = new SumOfCentroidSimilarities();
		System.out.println("Score according to SumOfCentroidSimilarities: "
				+ eval.score(clusters));
		eval = new SumOfAveragePairwiseSimilarities();
		System.out.println("Score according to SumOfAveragePairwiseSimilarities: "
				+ eval.score(clusters));
	}
}
