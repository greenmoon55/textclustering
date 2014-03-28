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
package net.sf.javaml.featureselection.scoring;

import java.util.List;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.FeatureScoring;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import net.sf.javaml.tools.DatasetTools;
import net.sf.javaml.utils.ContingencyTables;

/**
 * Implements the Symmetrical Uncertainty (SU) evaluation method for attributes.
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * @author Mark Hall
 */
public class SymmetricalUncertainty implements FeatureScoring {

    private Dataset training;

    private int bins = 10;

    public void build(Dataset data) {
        AbstractFilter discretize = new EqualWidthBinning(bins);
        discretize.build(data);
        discretize.filter(data);
        Instance min = DatasetTools.minAttributes(data);
        Instance max = DatasetTools.maxAttributes(data);
        for (int i = 0; i < data.noAttributes(); i++) {
            if (min.value(i) != 0 || max.value(i) != 9) {
                System.err.println(i + " " + min.value(i) + "\t" + max.value(i));
            }

        }
        this.training = data;

    }

    /**
     * Evaluates an individual attribute by measuring the symmetrical
     * uncertainty between it and the class.
     * 
     * @param attribute
     *            the index of the attribute to be evaluated
     * @return the symmetrical uncertainty
     */
    public double score(int attribute) {
        // int ii, jj;
        // int ni = training.numValues(attribute) + 1;
        // int nj = training.numClasses() + 1;
        double[][] counts = new double[bins][training.classes().size()];
        List<Object> classes = new Vector<Object>();
        classes.addAll(training.classes());
        // System.out.println(training);
        for (Instance inst : training) {
            // ii = (int) inst.value(attribute);
            // jj = (int) inst.classValue();
            if ((int) inst.value(attribute) >= bins) {
                System.err.println("Exceeding bins: " + bins);
            }
            if (classes.indexOf(inst.classValue()) >= training.classes().size())
                System.err.println("Exceeding classes: " + training.classes().size());
            counts[(int) inst.value(attribute)][classes.indexOf(inst.classValue())]++;
        }
        return ContingencyTables.symmetricalUncertainty(counts);
    }

    @Override
    public int noAttributes() {
        return training.noAttributes();
    }
}
