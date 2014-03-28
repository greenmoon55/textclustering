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
package net.sf.javaml.featureselection.subset;

import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.featureselection.FeatureSubsetSelection;
import net.sf.javaml.tools.DatasetTools;

/**
 * Provides an implementation of the forward greedy attribute subset selection.
 * 
 * @author Thomas Abeel
 * 
 */
public class GreedyForwardSelection implements FeatureSubsetSelection {
    /* Number of features that should be selected */
    private int n;

    /*
     * DistanceMetric to determine the relation between each attribute and the
     * class labels.
     */
    private DistanceMeasure dm;

    /**
     * Creates a new GreedyForwardSelection that will select the supplied number
     * of attributes.
     * 
     * @param n
     *            number of attributes to select in the subset
     */
    public GreedyForwardSelection(int n, DistanceMeasure dm) {
        this.n = n;
        this.dm = dm;
    }

    private Set<Integer> selectedAttributes = null;

    @Override
    public void build(Dataset data) {
        /*
         * When more attributes should be selected then there are, return all
         * attributes.
         */
        if (n > data.noAttributes()) {
            selectedAttributes = data.get(0).keySet();
            return;
        }
        /*
         * Regular procedure, add iteratively the best attribute till we have
         * enough attributes selected.
         */
        Instance classInstance = DatasetTools.createInstanceFromClass(data);
        selectedAttributes = new HashSet<Integer>();
        while (selectedAttributes.size() < n) {
            selectNext(data, classInstance);
        }

    }

    private void selectNext(Dataset data, Instance classInstance) {
        int bestIndex = -1;
        double bestScore = Double.NaN;
        for (int i = 0; i < data.noAttributes(); i++) {
            if (!selectedAttributes.contains(i)) {
                Instance attributeInstance = DatasetTools.createInstanceFromAttribute(data, i);

                double score = dm.measure(attributeInstance, classInstance);

                if (!Double.isNaN(score) && bestIndex == -1) {
                    bestIndex = i;
                    bestScore = score;
                } else {
                    if (!Double.isNaN(score) && dm.compare(score, bestScore)) {
                        bestIndex = i;
                        bestScore = score;

                    }
                }

            }
        }
        selectedAttributes.add(bestIndex);

    }

    @Override
    public Set<Integer> selectedAttributes() {
        return selectedAttributes;
    }

    @Override
    public int noAttributes() {
        return selectedAttributes.size();
    }
}
