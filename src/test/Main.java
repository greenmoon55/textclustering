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
import net.sf.javaml.clustering.SOM;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;


public class Main{

    /* Array voor het bijhouden van de mogelijke datasets. */
    private static String[] datasets = {"iris.csv","movement.data"};
    /* Array voor het bijhouden van de indices van de classlabels. */
    private static int[] index_classlabels = {4, -1};
    /* In bovenstaande array betekent '-1' dat voor de hiermee corresponderende
     * dataset (in de array 'datasets') geen index van het classlabel werd opgegeven.
     * De twee bovenvermelde arrays dienen steeds eenzelfde aantal elementen te
     * bevatten om een correcte werking te kunnen garanderen.
     */

    /* Array voor het bijhouden van de mogelijke clusteringsalgoritmes. */
    private static String[] algorithms = {"Density Based Spatial Clustering",
        "K-Means Clustering", "SOM (Self-Organizing Map)", "Normalized Spectral Clustering",
        "Unnormalized Spectral Clustering"};
    /* Array voor het bijhouden van de mogelijke afstandsmaten. */
    private static String[] distances = {"Cosine Distance", "Euclidean Distance",
        "Manhattan Distance", "Norm Distance"};



    public static void main(String[] args) throws Exception{
        /* Inputscanner aanmaken */
        Scanner scan = new Scanner(System.in);
        /* String aanmaken om input in bij te houden */
        String input = "";

        /* Opvragen van de dataset */
        System.out.println("Welke dataset wil u gebruiken?");
        for(int i=0; i < datasets.length; i++)
            System.out.println(i + " : " + datasets[i]);
        System.out.print("Geef het nummer van uw keuze in: ");
        input = scan.nextLine();

        /* Laden van de dataset */
        Dataset data;
        if(index_classlabels[Integer.parseInt(input)] < 0){
             data = FileHandler.loadDataset(new File(datasets[Integer.parseInt(input)]), ",");
        }else{
             data = FileHandler.loadDataset(new File(datasets[Integer.parseInt(input)]),index_classlabels[Integer.parseInt(input)],",");
        }

        /* Clusteralgoritme selecteren */
        System.out.println("Welk clusteralgoritme wenst u te gebruiken?");
        for(int i=0; i < algorithms.length; i++)
            System.out.println(i + " : " + algorithms[i]);
        System.out.print("Geef het nummer van uw keuze in: ");
        int choice = Integer.parseInt(scan.nextLine());


        /* Wanneer de gebruiker voor spectrale clustering kiest, moeten bijkomende parameters ingesteld worden.*/
        int number_of_clusters = 4;
        if(choice == 3 || choice == 4){
        System.out.println("Spectrale Clustering algoritmes dienen aangeroepen te worden " +
                "met het gewenste aantal clusters als parameter.");
        System.out.print("Hoeveel clusters wenst u te gebruiken? ");
        number_of_clusters = Integer.parseInt(scan.nextLine());
        System.out.println("Als laatste moet u de te gebruiken afstandsmaat selecteren.");
        for(int i=0; i < distances.length; i++)
            System.out.println(i + " : " + distances[i]);
        System.out.print("Geef het nummer van uw keuze in: ");
        input = scan.nextLine();
        }

        Clusterer cl;
        switch(choice){
            case 0: cl = new DensityBasedSpatialClustering(); break;
            case 1: cl = new KMeans(); break;
            case 2: cl = new SOM(); break;
            case 3: cl = new Normalized(number_of_clusters,Integer.parseInt(input)); break;
            case 4: cl = new Unnormalized(number_of_clusters,Integer.parseInt(input)); break;
            default:cl = new KMeans();
        }

        /* Het eigenlijke clusteren van de data */
        Dataset[] clusters = cl.cluster(data);
        /* Uitprinten van het gevonden aantal clusters */
        System.out.println("Aantal clusters: " + clusters.length);
        /* Aanmaken van object voor het evalueren van de clusters */
        ClusterEvaluation eval;
        /* Meten van de kwaliteit van de clusters (verschillende maatstaven) */
        eval = new SumOfSquaredErrors();
        System.out.println("Score volgens SumOfSquaredErrors: " + eval.score(clusters));
        eval = new SumOfCentroidSimilarities();
        System.out.println("Score volgens SumOfCentroidSimilarities: " + eval.score(clusters));
        eval = new SumOfAveragePairwiseSimilarities();
        System.out.println("Score volgens SumOfAveragePairwiseSimilarities: " + eval.score(clusters));
    }
}
