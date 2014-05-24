package fi.uef.cs;
import Jama.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

public class UnnormalizedSpectralClustering {

	
	/**
	 * Spectral Clustering : Unnormalized
	 * ----------------------------------
	 * Implementation of the "Unnormalized Spectral Clustering" algorithm
	 *
	 * @author Uyttersprot Bram
	 */


	private int numberOfClusters = 4;
	/* Will be used in clustering */
	private double maximum = 0;
	/* Index of distance measure */

	public UnnormalizedSpectralClustering(int number) {
		numberOfClusters = number;
	}
	
	public Dataset[] stringClustering(ArrayList<String> data, double[][] similarityMatrix) {
		/* Create required matrices */
		Matrix m = new Matrix(data.size(), data.size());
		Matrix d = new Matrix(data.size(), data.size());

		ShortTextSimilarity similarity = new ShortTextSimilarity();
				
//		/* Create distance matrix (or weight matrix) */
//		for (int i = 0; i < m.getRowDimension(); i++) {
//			for (int j = 0; j < m.getColumnDimension(); j++) {
//				m.set(i, j, similarity.getSimilarity(data.get(i), data.get(j), method, "n", false));
//				if (m.get(i, j) > maximum)
//					maximum = m.get(i, j);
//			}
//		}
//		/* Calculate similarity matrix and degree matrix from the above distance matrix */
//		for (int i = 0; i < m.getRowDimension(); i++) {
//			for (int j = 0; j < m.getColumnDimension(); j++) {
//				double temp = Math.abs(maximum - m.get(i, j));
//				m.set(i, j, temp);
//				d.set(i, i, d.get(i, i) + temp);
//			}
//		}
		
		/* Calculate similarity matrix and degree matrix from the above distance matrix */
		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				double temp = similarityMatrix[i][j];
				m.set(i, j, temp);
				d.set(i, i, d.get(i, i) + temp);
			}
		}
		/* Calculate Laplace matrix L = D - W */
		d.minusEquals(m);
		/* Calculate desired eigenvectors */
		EigenvalueDecomposition e = d.eig();
		Matrix V = e.getV();
		V = V.getMatrix(0, V.getRowDimension() - 1, 0, numberOfClusters - 1);

		/* Temporarily saving the eigenvectors to a file test.data */
		try {
			FileWriter w = new FileWriter(new File("test.csv"));
			/* Write eigenvectors one by one */
			for (int i = 0; i < V.getRowDimension(); i++) {
				String tempString = data.get(i);
				w.write(tempString + ", ");
				for (int j = 0; j < V.getColumnDimension(); j++) {
					w.write("" + V.get(i, j) + "");
					if (j != (V.getColumnDimension() - 1))
						w.write(", ");
				}
				w.write("\n");
			}
			w.flush();
			w.close();
		} catch (Exception ex) {
		}

		/*
		 * Process the newly-created file by KMeans algorithm
		 */
		try {
			Dataset dataset = FileHandler.loadDataset(new File("test.csv"), 0,
					",");
			Clusterer km = new KMeans(numberOfClusters);
			System.out.println("kmeans start");
			Dataset[] clusters = km.cluster(dataset);
			int index = 0;
			for (int i = 0; i < clusters.length; i++) {
				if (clusters[i].size() == 0) continue;
				FileHandler.exportDataset(clusters[i], new File("output" + ++index + ".csv"), false, ",");
			}
			return clusters;
		} catch (Exception exception) {
			return null;
		}
		
	}

	public Dataset[] stringListClustering(ArrayList<List<String>> data, double[][] similarityMatrix) {
		/* Create required matrices */
		Matrix m = new Matrix(data.size(), data.size());
		Matrix d = new Matrix(data.size(), data.size());
	
//		/* Create distance matrix (or weight matrix) */
//		for (int i = 0; i < m.getRowDimension(); i++) {
//			for (int j = 0; j < m.getColumnDimension(); j++) {
//				m.set(i, j, similarity.getSimilarity(data.get(i), data.get(j), method, "n", false));
//				if (m.get(i, j) > maximum)
//					maximum = m.get(i, j);
//			}
//		}
//		/* Calculate similarity matrix and degree matrix from the above distance matrix */
//		for (int i = 0; i < m.getRowDimension(); i++) {
//			for (int j = 0; j < m.getColumnDimension(); j++) {
//				double temp = Math.abs(maximum - m.get(i, j));
//				m.set(i, j, temp);
//				d.set(i, i, d.get(i, i) + temp);
//			}
//		}
		
		/* Calculate similarity matrix and degree matrix from the above distance matrix */
		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				double temp = similarityMatrix[i][j];
				m.set(i, j, temp);
				d.set(i, i, d.get(i, i) + temp);
			}
		}
		/* Calculate Laplace matrix L = D - W */
		d.minusEquals(m);
		/* Calculate desired eigenvectors */
		EigenvalueDecomposition e = d.eig();
		Matrix V = e.getV();
		V = V.getMatrix(0, V.getRowDimension() - 1, 0, numberOfClusters - 1);

		/* Temporarily saving the eigenvectors to a file test.data */
		try {
			FileWriter w = new FileWriter(new File("test.csv"));
			/* Write eigenvectors one by one */
			for (int i = 0; i < V.getRowDimension(); i++) {
				List<String> stringList = data.get(i);
				String tempString = "";
				for (String str : stringList) {
					tempString += str + " ";
				}
				w.write(tempString + ", ");
				for (int j = 0; j < V.getColumnDimension(); j++) {
					w.write("" + V.get(i, j) + "");
					if (j != (V.getColumnDimension() - 1))
						w.write(", ");
				}
				w.write("\n");
			}
			w.flush();
			w.close();
		} catch (Exception ex) {
		}

		/*
		 * Process the newly-created file by KMeans algorithm
		 */
		try {
			Dataset dataset = FileHandler.loadDataset(new File("test.csv"), 0,
					",");
			Clusterer km = new KMeans(numberOfClusters);
			System.out.println("kmeans start");
			Dataset[] clusters = km.cluster(dataset);
			int index = 0;
			for (int i = 0; i < clusters.length; i++) {
				if (clusters[i].size() == 0) continue;
				FileHandler.exportDataset(clusters[i], new File("output" + ++index + ".csv"), false, ",");
			}
			return clusters;
		} catch (Exception exception) {
			return null;
		}
		
	}
}
