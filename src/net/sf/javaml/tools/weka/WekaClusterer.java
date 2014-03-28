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
import java.util.Vector;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import weka.core.Instances;

/**
 * Provides a bridge between Java-ML and the clustering algorithms in WEKA.
 * 
 * 
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class WekaClusterer implements Clusterer {

    private weka.clusterers.Clusterer wekaCluster;

    public WekaClusterer(weka.clusterers.Clusterer wekaCluster) {
        this.wekaCluster = wekaCluster;
    }

    public Dataset[] cluster(Dataset data) {
        try {
            /*
             * Weka cannot handle class values in clustering, so we have to
             * store and remove them first
             */
            HashMap<Integer, Object> classValueMapping = new HashMap<Integer, Object>();
            for (Instance i : data) {
                classValueMapping.put(i.getID(), i.classValue());
                i.setClassValue(null);

            }
            data.classes().clear();

            /* Convert to Weka and train clustering */
            Instances insts = new ToWekaUtils(data).getDataset();
            Vector<Dataset> output = new Vector<Dataset>();
            wekaCluster.buildClusterer(insts);
            /* Apply clustering to the data set and restore class values */
            for (int i = 0; i < insts.numInstances(); i++) {
                int clusterIndex = wekaCluster.clusterInstance(insts.instance(i));
                while (output.size() <= clusterIndex)
                    output.add(new DefaultDataset());
                data.instance(i).setClassValue(classValueMapping.get(data.instance(i).getID()));
                output.get(clusterIndex).add(data.instance(i));
            }
            return output.toArray(new Dataset[output.size()]);
        } catch (Exception e) {
            throw new WekaException(e);
        }
    }

}
