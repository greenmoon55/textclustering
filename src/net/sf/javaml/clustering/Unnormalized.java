/**
 * Spectral Clustering : Unnormalized
 * ----------------------------------
 * Implementation of the "Unnormalized Spectral Clustering" algorithm
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

public class Unnormalized implements Clusterer {

	private int numberOfClusters = 4;
	/* Will be used in clustering */
	private double maximum = 0;
	/* Index of distance measure */
	private int indexOfDistance = 1;

	public Unnormalized(int number, int dist) {
		numberOfClusters = number;
		indexOfDistance = dist;
	}

	public Dataset[] cluster(Dataset data) {
		/* Create required matrices */
		Matrix m = new Matrix(data.size(), data.size());
		Matrix d = new Matrix(data.size(), data.size());

		/* Distance between two data points */
		DistanceMeasure distance;
		switch (indexOfDistance) {
		case 0:
			distance = new CosineDistance();
			break;
		case 1:
			distance = new EuclideanDistance();
			break;
		case 2:
			distance = new ManhattanDistance();
			break;
		case 3:
			distance = new NormDistance();
			break;
		default:
			distance = new EuclideanDistance();
		}

		/* Create distance matrix (or weight matrix) */
		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				m.set(i, j, distance.measure(data.get(i), data.get(j)));
				if (m.get(i, j) > maximum)
					maximum = m.get(i, j);
			}
		}
		/* Calculate similarity matrix and degree matrix from the above distance matrix */
		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				double temp = Math.abs(maximum - m.get(i, j));
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
			Dataset dataset = FileHandler.loadDataset(new File("test.csv"),
					",");
			Clusterer km = new KMeans(numberOfClusters);
			Dataset[] clusters = km.cluster(dataset);
			return clusters;
		} catch (Exception exception) {
			return null;
		}
	}

}