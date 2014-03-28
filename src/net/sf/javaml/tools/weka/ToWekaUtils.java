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

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.SparseInstance;
import weka.core.Instances;

/**
 * Provides utility methods to convert data to the WEKA format.
 * 
 * 
 * 
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class ToWekaUtils {
    private Instances wData;

    private boolean classSet;

    private Vector<Object> classes;

    public ToWekaUtils(Dataset data) {
        classes=new Vector<Object>();
        classes.addAll(data.classes());
        FastVector att = new FastVector();
        for (int i = 0; i < data.noAttributes(); i++) {
            att.addElement(new Attribute("att" + i));
        }
        classSet = data.classes().size() > 0;

        Attribute ca = null;
        if (classSet) {
            FastVector fvNominalVal = new FastVector(data.classes().size());
            for (Object o : data.classes()) {
                fvNominalVal.addElement(o.toString());
            }
            ca = new Attribute("classAtt", fvNominalVal);
            att.addElement(ca);
        }
        wData = new Instances("generated_from_java-ml_dataset", att, data.size());
        if (classSet) {
            assert (ca != null);
            wData.setClass(ca);
        }

        for (net.sf.javaml.core.Instance i : data) {

            wData.add(instanceToWeka(i));
        }

    }

    public Instances getDataset() {
        return wData;
    }

    /* Converts this (dense/sparse) JavaML instance to a (dense/sparse) Weka instance */
    public Instance instanceToWeka(net.sf.javaml.core.Instance inst) {
        double[] values = new double[classSet ? inst.noAttributes() + 1 : inst.noAttributes()];
        // System.arraycopy(i.values().t.toArray(), 0, values, 0, classSet ?
        // values.length - 1 : values.length);
        for (int i = 0; i < (classSet ? values.length - 1 : values.length); i++) {
            values[i] = inst.get(i);
        }
        // if (classSet)
        // values[values.length - 1] = inst.classValue();
        
        Instance wI = null;
        if (inst instanceof net.sf.javaml.core.SparseInstance)
            wI = new SparseInstance(1, values);
        else
            wI = new Instance(1, values);
        
        wI.setDataset(wData);
        if (inst.classValue() != null) {
            wI.setClassValue(inst.classValue().toString());

        }
        return wI;
    }

    public Object convertClass(double index) {
       return classes.get((int)index);
    }
}
