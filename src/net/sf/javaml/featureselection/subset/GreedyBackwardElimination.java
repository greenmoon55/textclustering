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
 * Provides an implementation of the backward greedy attribute subset
 * elimination algorithm.
 * 
 * @author Thomas Abeel
 * 
 */
public class GreedyBackwardElimination implements FeatureSubsetSelection {
    /* Number of features that the algorithm should keep */
    private int n;

    private DistanceMeasure dm;

    /**
     * Creates a new GreedyForwardSelection that will select the supplied number
     * of attributes.
     * 
     * @param n
     *            number of attributes to select in the subset
     */
    public GreedyBackwardElimination(int n, DistanceMeasure dm) {
        this.n = n;
        this.dm = dm;
    }

    private Set<Integer> removedAttributes = null;

    private HashSet<Integer> selectedAttributes;

    @Override
    public void build(Dataset data) {
        /*
         * When more attributes should be selected then there are, return all
         * attributes.
         */
        if (n > data.noAttributes()) {
            removedAttributes = new HashSet<Integer>();
            return;
        }
        /*
         * Regular procedure, remove the worst attribute till we have enough
         * attributes left.
         */
        Instance classInstance = DatasetTools.createInstanceFromClass(data);
        removedAttributes = new HashSet<Integer>();
        while (removedAttributes.size() < data.noAttributes() - n) {
            removeNext(data, classInstance);
        }

        /* Create the inverse of the removed attributes */
        selectedAttributes = new HashSet<Integer>();
        for (int i = 0; i < data.noAttributes(); i++)
            selectedAttributes.add(i);
        selectedAttributes.removeAll(removedAttributes);

    }

    private void removeNext(Dataset data, Instance classInstance) {
        int worstIndex = -1;
        double worstScore = Double.NaN;
        for (int i = 0; i < data.noAttributes(); i++) {
            if (!removedAttributes.contains(i)) {
                Instance attributeInstance = DatasetTools.createInstanceFromAttribute(data, i);

                double score = dm.measure(attributeInstance, classInstance);
                /* When the score is NaN, remove the attribute immediately */
                if (Double.isNaN(score)) {
                    worstIndex = i;
                    break;
                }

                if (worstIndex == -1) {
                    worstIndex = i;
                    worstScore = score;
                } else {
                    if (!dm.compare(score, worstScore)) {
                        worstIndex = i;
                        worstScore = score;

                    }
                }

            }
        }
        removedAttributes.add(worstIndex);

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
