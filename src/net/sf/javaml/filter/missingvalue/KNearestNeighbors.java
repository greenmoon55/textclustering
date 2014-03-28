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
package net.sf.javaml.filter.missingvalue;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.utils.ArrayUtils;

/**
 * Replaces the missing value with the average of the values of its nearest
 * neighbors.
 * 
 * This technique does not guarantee that all missing will be replaced. If all
 * neighbors also have the same missing attributes, it is impossible to replace
 * the original value.
 * 
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class KNearestNeighbors implements DatasetFilter {

    private int k = 5;

    public void setK(int k) {
        this.k = k;
    }

    public void build(Dataset data) {
        // do nothing

    }

    public void filter(Dataset data) {
        for (Instance i : data) {
            removeMissingValues(i, data);
        }
    }

    private void removeMissingValues(Instance inst, Dataset data) {
        if (InstanceTools.hasMissingValues(inst)) {
            double[] sum = new double[inst.noAttributes()];
            double[] count = new double[inst.noAttributes()];
            for (Instance x : data.kNearest(k, inst, new EuclideanDistance())) {
                for (int i = 0; i < x.noAttributes(); i++) {
                    if (!Double.isNaN(x.value(i))) {
                        sum[i] += x.value(i);
                        count[i]++;
                    }

                }
            }
            sum = ArrayUtils.divide(sum, count);

            for (int i = 0; i < inst.noAttributes(); i++) {
                if (Double.isNaN(inst.value(i)) && count[i] != 0) {
                    inst.put(i, sum[i]);
                }
            }

        }
    }
}
