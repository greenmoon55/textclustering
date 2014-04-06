/**
 * Spectral Clustering : Normalized
 * --------------------------------
 * Implementatie van het "Normalized Spectral Clustering"-algoritme.
 *
 * @author Uyttersprot Bram
 */

package net.sf.javaml.clustering;
import Jama.*;

import java.io.*;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.CosineDistance;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.distance.ManhattanDistance;
import net.sf.javaml.distance.NormDistance;
import net.sf.javaml.tools.data.FileHandler;


public class Normalized implements Clusterer{

    /* Veld om het aantal clusters bij te houden.*/
    private int numberOfClusters = 4;
    /* Variabele die tijdens het cluseren zijn nut zal bewijzen. */
    private double maximum = 0;
    /* Index bijhouden van de te gebruiken afstandsmaat. */
    private int indexOfDistance = 1;

    /* Constructor verwacht 2 argumenten: het aantal clusters en de index van de te gebruiken afstandsmaat.*/
    public Normalized(int number, int dist){
        numberOfClusters = number;
        indexOfDistance = dist;
    }

    
    public Dataset[] cluster(Dataset data){
        /* Benodigde matrices aanmaken */
        Matrix m = new Matrix(data.size(),data.size());
        Matrix d = new Matrix(data.size(),data.size());

        /* Afstandsmaat tussen datapunten*/
        DistanceMeasure distance;
        switch(indexOfDistance){
            case 0: distance = new CosineDistance(); break;
            case 1: distance = new EuclideanDistance(); break;
            case 2: distance = new ManhattanDistance(); break;
            case 3: distance = new NormDistance(); break;
            default:distance = new EuclideanDistance();
        }

        /* Gewichtsmatrix aanmaken */
        for(int i=0; i < m.getRowDimension(); i++){
            for(int j=0; j < m.getColumnDimension(); j++){
                m.set(i, j, distance.measure(data.get(i), data.get(j)));
                if(m.get(i, j) > maximum) maximum = m.get(i, j);
            }
        }
        /* Similariteitsmatrix en degree matrix hieruit berekenen. */
        for(int i=0; i < m.getRowDimension(); i++){
            for(int j=0; j < m.getColumnDimension(); j++){
                double temp = Math.abs(maximum - m.get(i, j));
                m.set(i, j, temp);
                d.set(i, i, d.get(i, i) + temp);
            }
        }

        /* Laplace matrix berekenen L = D - W */
        Matrix l = d.minus(m);
        /* Symmetrische Laplace matrix hieruit berekenen */
        d = d.inverse();
        for(int i=0; i < d.getRowDimension(); i++)
            d.set(i, i, Math.sqrt(d.get(i, i)));
        l = l.times(d); d = d.times(l);

        /* Gewenste eigenvectoren berekenen. */
        EigenvalueDecomposition e = d.eig();
        Matrix V = e.getV();
        V = V.getMatrix(0, V.getRowDimension() - 1, 0, numberOfClusters-1);

        Matrix T = new Matrix(V.getRowDimension(),V.getColumnDimension());
        for(int i=0; i < V.getRowDimension(); i++){
            Matrix P = V.getMatrix(i, i, 0, V.getColumnDimension() - 1);
            double norm = P.normF();
            for(int j=0; j < V.getColumnDimension(); j++){
                T.set(i, j, V.get(i, j)/norm);
            }
        }
        
        /* Tijdelijk wegschrijven van de eigenvectoren naar een bestand temp.data */
        try{
            FileWriter w = new FileWriter(new File("temp.data"));
            /* Eigenvectoren één voor één wegschrijven */
            for(int i=0; i < T.getRowDimension(); i++){
                for(int j=0; j < T.getColumnDimension(); j++){
                    w.write("" + T.get(i, j) + "");
                    if(j != (T.getColumnDimension() - 1))
                        w.write(", ");
                }
                w.write("\n");
            }
            w.flush();
            /* FileWriter sluiten*/
            w.close();

        }catch(Exception ex){
            // leeg
        }

        /* Net aangemaakt bestand inlezen om te laten verwerken door het KMeans-algoritme */
        try{
            Dataset dataset = FileHandler.loadDataset(new File("temp.data"),",");
            Clusterer km = new KMeans(numberOfClusters);
            Dataset[] clusters = km.cluster(dataset);
            return clusters;
        }catch(Exception exception){
            return null;
        }
    }

}