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
package net.sf.javaml.filter;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Filters all instances from a data set that have their class value not set
 * 
 * 
 * 
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class MissingClassFilter implements DatasetFilter {

    public void build(Dataset data) {
        // do nothing, requires no training

    }

    public void filter(Dataset data) {
        Vector<Instance> toRemove = new Vector<Instance>();
        for (Instance i : data)
            if (i.classValue() == null)
                toRemove.add(i);
        data.removeAll(toRemove);
    }

}
