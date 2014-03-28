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
 * Interface for algorithms that can generate an attribute ranking. Higher
 * values in the ranking means that the attribute is less informative or is
 * worth less.
 * 
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface FeatureRanking extends FeatureSelection {

    /**
     * Get the ranking of the given attribute. The first rank is 0 and is the
     * best. The higher the rank, the worse the attribute.
     * 
     * @param attIndex
     *            the index of the attribute to rank
     * @return the rank of the attribute
     */
    public int rank(int attIndex);

}
