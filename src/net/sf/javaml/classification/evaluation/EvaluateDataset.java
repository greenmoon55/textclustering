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

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Tests a classifier on a data set
 * 
 * @author Thomas Abeel
 */
public class EvaluateDataset {

    /**
     * Tests a classifier on a data set
     * 
     * @param cls
     *            the classifier to test
     * @param data
     *            the data set to test on
     * @return the performance for each class
     */
    public static Map<Object, PerformanceMeasure> testDataset(Classifier cls, Dataset data) {
        Map<Object, PerformanceMeasure> out = new HashMap<Object, PerformanceMeasure>();
        for (Object o : data.classes()) {
            out.put(o, new PerformanceMeasure());
        }
        for (Instance instance : data) {
            Object prediction = cls.classify(instance);
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
        return out;
    }
}
