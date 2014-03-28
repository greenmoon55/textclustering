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
package net.sf.javaml.classification.meta;

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.classification.AbstractClassifier;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.sampling.Sampling;

/**
 * Bootstrap aggregating (Bagging) meta learner. This is the most basic
 * implementation of Bagging.
 * 
 * @author Thomas Abeel
 * 
 */
public class SimpleBagging extends AbstractClassifier {

    private static final long serialVersionUID = 208101377048893813L;

    private Classifier[] classifiers;

    public SimpleBagging(Classifier[] classifiers){
        this.classifiers = classifiers;
    }

    /* Reference to the training data */
    private Dataset reference = null;

    @Override
    public void buildClassifier(Dataset data) {
        this.reference = data;
        for (int i = 0; i < classifiers.length; i++) {
            Dataset sample = Sampling.NormalBootstrapping.sample(data).x();
            classifiers[i].buildClassifier(sample);
        }
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        Map<Object, Double> membership = new HashMap<Object, Double>();
        for (Object o : reference.classes())
            membership.put(o, 0.0);
        for (int i = 0; i < classifiers.length; i++) {
            Object prediction = classifiers[i].classify(instance);
            membership.put(prediction, membership.get(prediction) + (1.0 / classifiers.length));
        }

        return membership;

    }
}
