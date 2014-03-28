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

import net.sf.javaml.core.Dataset;

/**
 * Top-level interface for feature selection algorithms. There are three main
 * types of features selection: (i) feature scoring, (ii) feature ranking and
 * (iii) feature subset selection. Feature scoring is the most general method
 * and can be converted in the latter two, while feature ranking can only be
 * turned into feature subset selection methods.
 * 
 * Each type of feature selection has its own interface that inherits from this
 * one.
 * 
 * @see net.sf.javaml.featureselection.FeatureScoring
 * @see net.sf.javaml.featureselection.FeatureRanking
 * @see net.sf.javaml.featureselection.FeatureSubsetSelection
 * 
 * @author Thomas Abeel
 * 
 */
public interface FeatureSelection {

    /**
     * Build the attribute evaluation on the supplied data set.
     * 
     * Note: This method can change the data set that is supplied to the method!
     * 
     * @param data
     *            data set to train the attribute evaluation algorithm on.
     */
    public void build(Dataset data);

    /**
     * Returns the number of attributes that have been ranked, scored or
     * selected.
     * 
     * @return the number of ranked, scored or selected attributes
     */
    public int noAttributes();

}
