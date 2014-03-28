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
package net.sf.javaml.tools.weka;

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import weka.core.Instances;

public class WekaClassifier implements Classifier {

    private static final long serialVersionUID = -4607698346509036963L;

    private weka.classifiers.Classifier wekaClass;

    private ToWekaUtils utils;

    public WekaClassifier(weka.classifiers.Classifier wekaClass) {
        this.wekaClass = wekaClass;
    }

    public void buildClassifier(Dataset data) {
        utils = new ToWekaUtils(data);
        Instances inst = utils.getDataset();
        try {
            wekaClass.buildClassifier(inst);
        } catch (Exception e) {
            throw new WekaException(e);
        }

    }

    @Override
    public Object classify(Instance instance) {

        try {
            return utils.convertClass(wekaClass.classifyInstance(utils.instanceToWeka(instance)));
        } catch (Exception e) {
            throw new WekaException(e);
        }
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        try {
            Map<Object, Double> out = new HashMap<Object, Double>();
            double[] distr = wekaClass.distributionForInstance(utils.instanceToWeka(instance));
            for (int i = 0; i < distr.length; i++)
                out.put(utils.convertClass(i), distr[i]);
            return out;
        } catch (Exception e) {
            throw new WekaException(e);
        }
    }

}
