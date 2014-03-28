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
package net.sf.javaml.distance;

/**
 * Abstract super class for all distance measures. A distance measure quantifies
 * the distance between two instances. High values denote more distant
 * instances.
 * 
 * @see net.sf.javaml.distance.DistanceMeasure
 * @see net.sf.javaml.distance.AbstractSimilarity
 * @see net.sf.javaml.distance.AbstractCorrelation
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractDistance implements DistanceMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5437432787091767310L;

	public boolean compare(double x, double y) {
		return x < y;
	}

	@Override
	public double getMinValue() {
		return 0;
	}

	@Override
	public double getMaxValue() {
		return Double.POSITIVE_INFINITY;
	}
}
