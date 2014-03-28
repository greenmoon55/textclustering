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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.classification.AbstractClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * ZeroR classifier implementation. This classifier will determine the class
 * distribution in the training data and will always return this as the
 * predicted class distribution.
 * 
 * @author Thomas Abeel
 * 
 */
public class ZeroR extends AbstractClassifier {

	private static final long serialVersionUID = -5506945214184891019L;

	private Map<Object, Double> mapping = null;

	@Override
	public void buildClassifier(Dataset data) {
		Map<Object, Double> mapping = new HashMap<Object, Double>();
		for (Instance i : data) {
			if (i.classValue() != null) {
				if (!mapping.containsKey(i.classValue()))
					mapping.put(i.classValue(), 0.0);
				mapping.put(i.classValue(), mapping.get(i.classValue()) + 1);

			}

		}
		this.mapping = Collections.unmodifiableMap(mapping);

	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		return mapping;
	}

}
