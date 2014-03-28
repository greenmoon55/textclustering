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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public abstract class AbstractClassifier implements Classifier {

	private static final long serialVersionUID = -4461661354949399603L;

	protected Set<Object> parentClasses = null;

	@Override
	public Object classify(Instance instance) {
		Map<Object, Double> distribution = classDistribution(instance);
		double max = 0;
		Object out = null;
		for (Object key : distribution.keySet()) {
			if (distribution.get(key) > max) {
				max = distribution.get(key);
				out = key;
			}
		}
		return out;
	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		HashMap<Object, Double> out = new HashMap<Object, Double>();
		for (Object o : parentClasses) {
			out.put(o, 0.0);
		}
		out.put(classify(instance), 1.0);
		return out;

	}

	@Override
	public void buildClassifier(Dataset data) {
		this.parentClasses = new HashSet<Object>();
		parentClasses.addAll(data.classes());

	}
}
