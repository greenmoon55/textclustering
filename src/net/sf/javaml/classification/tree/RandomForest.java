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
package net.sf.javaml.classification.tree;

import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.meta.Bagging;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public class RandomForest implements Classifier {

    /**
     * 
     */
    private static final long serialVersionUID = 5832370995432897745L;

    private Bagging bagger;

    private int treeCount;

    private int numAttributes;

    public void setNumAttributes(int k) {
        this.numAttributes = k;

    }

    private boolean calculateOutOfBagErrorEstimate = false;

    private Random rg;

    public RandomForest(int treeCount){
        this(treeCount,false,1,new Random(System.currentTimeMillis()));
    }
    
    public RandomForest(int treeCount, boolean calculateOutOfBagErrorEstimate, int numAttributes, Random rg) {
        this.treeCount = treeCount;
        this.rg = rg;
        this.calculateOutOfBagErrorEstimate = calculateOutOfBagErrorEstimate;
        this.numAttributes = numAttributes;
    }

    public double getOutOfBagErrorEstimate() {
        return bagger.getOutOfBagErrorEstimate();
    }

    public void buildClassifier(Dataset data) {
        if(treeCount<0)
            treeCount=(int)Math.sqrt(data.noAttributes())+1;
        RandomTree[] trees = new RandomTree[treeCount];
        for (int i = 0; i < trees.length; i++) {
            trees[i] = new RandomTree(numAttributes,rg);
        }
        bagger = new Bagging(trees, rg);
        bagger.setCalculateOutOfBagErrorEstimate(calculateOutOfBagErrorEstimate);
        bagger.buildClassifier(data);

    }

    @Override
    public Object classify(Instance instance) {
        return bagger.classify(instance);
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        return bagger.classDistribution(instance);
    }

}
