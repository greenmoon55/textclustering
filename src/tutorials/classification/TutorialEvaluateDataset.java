/**
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2012, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 * 
 */
package tutorials.classification;

import java.io.File;
import java.util.Map;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

/**
 * This tutorial show how to use the EvaluateDataset class to test the
 * performance of a classifier on a data set.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialEvaluateDataset {
    /**
     * Shows the default usage of the KNN algorithm.
     */
    public static void main(String[] args) throws Exception { /* Load a data set */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /*
         * Contruct a KNN classifier that uses 5 neighbors to make a decision.
         */
        Classifier knn = new KNearestNeighbors(5);
        knn.buildClassifier(data);

        /*
         * Load a data set for evaluation, this can be a different one, but for
         * this example we use the same one.
         */
        Dataset dataForClassification = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");

        Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(knn, dataForClassification);
        for (Object o : pm.keySet())
            System.out.println(o + ": " + pm.get(o).getAccuracy());

    }

}
