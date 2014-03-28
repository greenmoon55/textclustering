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
package net.sf.javaml.filter.normalize;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.filter.InstanceFilter;
import net.sf.javaml.filter.instance.ReplaceValueFilter;
import net.sf.javaml.tools.DatasetTools;

/**
 * This filter will normalize the data set with a certain mid-range and a
 * certain range for each attribute. This class implements both the
 * {@link DatasetFilter} and {@link InstanceFilter} interfaces, but before you
 * can apply this filter to a single instance, you'll need to apply it to a data
 * set to calculate the proper mid-ranges for each attribute.
 * 
 * For example mid-range 0 and range 2 would yield a data set within the range
 * [-1,1].
 * 
 * Each attribute of each {@link Instance} is normalized separately. For
 * instance if you have three instances {-1;10}, {1;15} and {0;20} and you
 * normalize with mid-range 0 and range 2, you would get {-1,-1}, {1,0} and
 * {0,1}.
 * 
 * The default is normalization in the interval [-1,1].
 * 
 * 
 * 
 * @see InstanceFilter
 * @see DatasetFilter
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizeMidrange extends AbstractFilter {

    /**
     * A normalization filter to the interval [-1,1]
     * 
     */
    public NormalizeMidrange() {
        this(0, 2);
    }

    private double normalMiddle;

    private double normalRange;

    public NormalizeMidrange(double middle, double range) {
        this.normalMiddle = middle;
        this.normalRange = range;

    }

    private Instance currentRange = null;

    private Instance currentMiddle = null;

    public void build(Dataset data) {
        // Calculate the proper range and midrange
        Instance max = DatasetTools.maxAttributes(data);
        Instance min = DatasetTools.minAttributes(data);
        currentRange = max.minus(min);
        currentMiddle = min.add(max).divide(2);

    }

    @Override
    public void filter(Instance instance) {
        if (currentRange == null || currentMiddle == null)
            throw new TrainingRequiredException();

        if (instance instanceof DenseInstance) {
            Instance tmp = instance.minus(currentMiddle).divide(currentRange).multiply(normalRange).add(normalMiddle);
            instance.clear();
            instance.putAll(tmp);

        }
        if (instance instanceof SparseInstance) {
            for (int index : instance.keySet()) {
                instance.put(index, ((instance.value(index) - currentMiddle.value(index)) / currentRange.value(index))
                        * normalRange + normalMiddle);
            }
        }
        new ReplaceValueFilter(Double.NEGATIVE_INFINITY, normalMiddle).filter(instance);
        new ReplaceValueFilter(Double.POSITIVE_INFINITY, normalMiddle).filter(instance);
        new ReplaceValueFilter(Double.NaN, normalMiddle).filter(instance);
    }

    public void filter(Dataset data) {
        if (currentRange == null || currentMiddle == null)
            build(data);
        for (Instance i : data)
            filter(i);
    }

}
