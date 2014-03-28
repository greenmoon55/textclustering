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
package net.sf.javaml.featureselection;

/**
 * Interface for all attribute evaluation methods. Attribute evaluation methods
 * can be used to calculate the worth of a certain attribute. This is
 * interesting for removing attributes with little information to make your
 * algorithms run faster.
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface FeatureScoring extends FeatureSelection {

    /**
     * Evaluate a single attribute. This should return a value between 0 and 1.
     * 
     * The higher the value, the better the feature is.
     * 
     * @param attribute
     *            the index of the attribute to evaluate
     * 
     * @return the worth of that attribute, a value between 0 and 1.
     */
    public double score(int attribute);

}
