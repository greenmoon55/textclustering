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

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Abstract classifier class that is the parent of all classifiers that require
 * the mean of each class as training.
 * 
 * @author Thomas Abeel
 * 
 */
public class AbstractMeanClassifier extends AbstractClassifier {

    private static final long serialVersionUID = 8596181454461400908L;

    protected Map<Object, Instance> mean;

    public Instance getMean(Object clazz) {
        return mean.get(clazz);

    }

    @Override
    public void buildClassifier(Dataset data) {
        super.buildClassifier(data);
        mean = new HashMap<Object, Instance>();
        HashMap<Object, Integer> count = new HashMap<Object, Integer>();
        for (Instance i : data) {
            if (!mean.containsKey(i.classValue())) {
                mean.put(i.classValue(), i);
                count.put(i.classValue(), 1);
            } else {
                mean.put(i.classValue(), mean.get(i.classValue()).add(i));
                count.put(i.classValue(), count.get(i.classValue()) + 1);
            }
        }
        for (Object o : mean.keySet()) {
            mean.put(o, mean.get(o).divide(count.get(o)));
        }

    }
}
