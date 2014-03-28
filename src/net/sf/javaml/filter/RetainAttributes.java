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

import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.core.Instance;

/**
 * Filter to retain a set of wanted attributes and remove all others
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class RetainAttributes extends AbstractFilter {

	private Set<Integer> toKeep = new HashSet<Integer>();

	private Set<Integer> toRemove = new HashSet<Integer>();

	/**
	 * Construct a filter that retains all the attributes with the indices given
	 * in the array as parameter.
	 * 
	 * @param indices
	 *            the indices of the columns that will be retained.
	 */
	public RetainAttributes(int[] indices) {
		for (int i : indices) {
			this.toKeep.add(i);
		}
	}

	/**
	 * Construct a filter that retains all the attributes with the indices given
	 * in the array as parameter.
	 * 
	 * @param indices
	 *            the indices of the columns that will be retained.
	 */
	public RetainAttributes(Set<Integer> indices) {
		this.toKeep.addAll(indices);
	}

	@Override
	public void filter(Instance instance) {
		if (toRemove.size() + toKeep.size() != instance.noAttributes())
			buildRemove(instance.noAttributes());
		instance.removeAttributes(toRemove);
	}

	private void buildRemove(int noAttributes) {
		toRemove.clear();
		for (int i = 0; i < noAttributes; i++) {
			if (!toKeep.contains(i)) {
				toRemove.add(i);
			}
		}

	}
}
