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
 * Abstract super class for all correlation measures. A correlation measure
 * quantifies the correlation between two instances. Values are in the range
 * [-1,1]. One means perfect correlation, zero means no correlation and minus
 * one means perfect inverse correlation.
 * 
 * @see net.sf.javaml.distance.DistanceMeasure
 * @see net.sf.javaml.distance.AbstractSimilarity
 * @see net.sf.javaml.distance.AbstractDistance
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractCorrelation implements DistanceMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2504319617417736626L;

	/**
	 * Comparisons for correlation measures are done with the absolute value of
	 * the real values
	 */
	public boolean compare(double x, double y) {
		return Math.abs(x) > Math.abs(y);
	}

	@Override
	public double getMinValue() {
		return 1;
	}

	@Override
	public double getMaxValue() {
		return 0;
	}

}
