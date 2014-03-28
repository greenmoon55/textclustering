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
package net.sf.javaml.sampling;

import java.util.List;

/**
 * Defines sampling methods to select a subset of a set integers. The original
 * set may contain duplicates and the output set may contain duplicates.
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class SamplingMethod {

	/**
	 * Samples a set of integers and returns a new set of integers that is the
	 * result of the sampling.
	 * 
	 * The returned set will be the same size as the original set.
	 * 
	 * @param set
	 *            the set to sample from
	 * @return the selected of integers
	 */
	List<Integer> sample(List<Integer> set) {
		return sample(set, set.size());
	}

	/**
	 * Samples a set of integers and returns a new set of integers that is the
	 * result of the sampling.
	 * 
	 * @param set
	 *            the set to sample from
	 * @param size
	 *            the number of items that should be in the returned sample
	 * @return the selected set of integers
	 */
	List<Integer> sample(List<Integer> set, int size) {
		return sample(set, size, System.currentTimeMillis());
	}

	/**
	 * Samples a set of integers and returns a new set of integers that is the
	 * result of the sampling.
	 * 
	 * @param set
	 *            the set to sample from
	 * @param size
	 *            the number of items that should be in the returned sample
	 * @param seed
	 *            the seed used for the random generator
	 * @return the selected set of integers
	 */
	abstract List<Integer> sample(List<Integer> set, int size, long seed);

	
}
