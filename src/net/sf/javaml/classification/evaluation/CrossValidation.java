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
package net.sf.javaml.classification.evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

/**
 * Implementation of the cross-validation evaluation technique.
 * 
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class CrossValidation {

    private Classifier classifier;

    public CrossValidation(Classifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Performs cross validation with the specified parameters.
     * 
     * @param data
     *            the data set to use in the cross validation. This data set is
     *            split in the appropriate number of folds.
     * @param numFolds
     *            the number of folds to create
     * @param rg
     *            random generator to create the folds
     * @return the results of the cross-validation.
     */
    public Map<Object, PerformanceMeasure> crossValidation(Dataset data, int numFolds, Random rg) {
        // TODO use EvaluateDataset
        Dataset[] folds = data.folds(numFolds, rg);
        Map<Object, PerformanceMeasure> out = new HashMap<Object, PerformanceMeasure>();
        for (Object o : data.classes()) {
            out.put(o, new PerformanceMeasure());
        }
        for (int i = 0; i < numFolds; i++) {
            Dataset validation = folds[i];
            Dataset training = new DefaultDataset();
            for (int j = 0; j < numFolds; j++) {
                if (j != i)
                    training.addAll(folds[j]);

            }

            classifier.buildClassifier(training);

            for (Instance instance : validation) {
                Object prediction = classifier.classify(instance);
                if (instance.classValue().equals(prediction)) {// prediction
                    // ==class
                    for (Object o : out.keySet()) {
                        if (o.equals(instance.classValue())) {
                            out.get(o).tp++;
                        } else {
                            out.get(o).tn++;
                        }

                    }
                } else {// prediction != class
                    for (Object o : out.keySet()) {
                        /* prediction is positive class */
                        if (prediction.equals(o)) {
                            out.get(o).fp++;
                        }
                        /* instance is positive class */
                        else if (o.equals(instance.classValue())) {
                            out.get(o).fn++;
                        }
                        /* none is positive class */
                        else {
                            out.get(o).tn++;
                        }

                    }
                }
            }

        }
        return out;

    }

    /**
     * Performs cross validation with the specified parameters.
     * 
     * @param data
     *            the data set to use in the cross validation. This data set is
     *            split in the appropriate number of folds.
     * @param numFolds
     *            the number of folds to create
     * @return the results of the cross-validation.
     */
    public Map<Object, PerformanceMeasure> crossValidation(Dataset data, int folds) {
        return crossValidation(data, folds, new Random(System.currentTimeMillis()));

    }

    /**
     * Performs cross validation with the specified parameters. By default the
     * number of folds is 10.
     * 
     * @param data
     *            the data set to use in the cross validation. This data set is
     *            split in the appropriate number of folds.
     * 
     * @return the results of the cross-validation.
     */
    public Map<Object, PerformanceMeasure> crossValidation(Dataset data) {
        return crossValidation(data, 10);
    }

}
