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
package net.sf.javaml.classification;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Nearest mean classifier. This classifier calculates the mean for each class
 * and use this to classify further instances.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class NearestMeanClassifier extends AbstractMeanClassifier {

    private static final long serialVersionUID = 3044426429892220857L;

    private EuclideanDistance dist = new EuclideanDistance();

    @Override
    public Object classify(Instance instance) {
        double min = Double.POSITIVE_INFINITY;
        Object pred = null;
        for (Object o : mean.keySet()) {
            double d = dist.calculateDistance(mean.get(o), instance);
            if (d < min) {
                min = d;
                pred = o;
            }
        }
        return pred;
    }

    

}
