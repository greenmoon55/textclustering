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

import be.abeel.util.HashMap2D;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.FeatureScoring;
import net.sf.javaml.filter.normalize.NormalizeMidrange;

/**
 * Feature scoring algorithm based on Kullback-Leibler divergence of the value
 * distributions of features.
 * 
 * 
 * Note: Calling the build method will normalize the data.
 * 
 * @author Thomas Abeel
 * 
 */
public class KullbackLeiblerDivergence implements FeatureScoring {

    private double[] maxDivergence;

    private HashMap2D<Object, Object, double[]> pairWiseDivergence = new HashMap2D<Object, Object, double[]>();

    private int bins;

    public KullbackLeiblerDivergence() {
        this(100);
    }

    public KullbackLeiblerDivergence(int i) {
        this.bins = i;
    }

    @Override
    public void build(Dataset data) {
        maxDivergence = new double[data.noAttributes()];
        /* Normalize to [0,100[ */
        NormalizeMidrange nm = new NormalizeMidrange(bins / 2, bins - 0.000001);
        nm.build(data);
        nm.filter(data);
        /* Calculate all pairwise divergencies */
        for (Object p : data.classes()) {
            for (Object q : data.classes()) {
                if (!p.equals(q)) {
                    double[] d = pairWise(p, q, data);
                    pairWiseDivergence.put(p, q, d);
                }
            }
        }
        /* Search for maximum pairwise divergencies */
        for (Object p : data.classes()) {
            for (Object q : data.classes()) {
                double[] d = pairWiseDivergence.get(p, q);
                if (d != null) {
                    for (int i = 0; i < d.length; i++) {
                        if (d[i] > maxDivergence[i])
                            maxDivergence[i] = d[i];
                    }
                }
            }
        }

    }

    private double[] pairWise(Object p, Object q, Dataset data) {
        double[] divergence = new double[data.noAttributes()];
        /*
         * For probability distributions P and Q of a discrete random variable
         * the K�L divergence of Q from P is defined to be:
         * 
         * D_KL(P|Q)=sum_i(P(i)log(P(i)/Q(i)))
         */
        double maxSum = 0;
        for (int i = 0; i < data.noAttributes(); i++) {
            double sum = 0;
            double[] countQ = new double[bins];
            double[] countP = new double[bins];
            double pCount = 0, qCount = 0;
            for (Instance inst : data) {
                if (inst.classValue().equals(q)) {
                    countQ[(int) inst.value(i)]++;
                    qCount++;
                }
                if (inst.classValue().equals(p)) {
                    countP[(int) inst.value(i)]++;
                    pCount++;
                }
            }

            for (int j = 0; j < countP.length; j++) {
                countP[j] /= pCount;
                countQ[j] /= qCount;
                /*
                 * Probabilities should never be really 0, they can be small
                 * though
                 */
                if (countP[j] == 0)
                    countP[j] = 0.0000001;
                if (countQ[j] == 0)
                    countQ[j] = 0.0000001;
                sum += countP[j] * Math.log(countP[j] / countQ[j]);
            }
            divergence[i] = sum;
            /* Keep track of highest value */
            if (sum > maxSum)
                maxSum = sum;
        }
        /* Normalize to [0,1] */
        for (int i = 0; i < data.noAttributes(); i++) {
            divergence[i] /= maxSum;
        }
        return divergence;
    }

    @Override
    public double score(int attribute) {
        return maxDivergence[attribute];
    }

    @Override
    public int noAttributes() {
        return maxDivergence.length;
    }

}
