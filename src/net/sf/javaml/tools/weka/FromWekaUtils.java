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

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 * Provides utility methods to convert data from the WEKA format to the Java-ML
 * format.
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class FromWekaUtils {
	private Dataset data;
	private Instances wData;

	public FromWekaUtils(Instances wData) {
		data=new DefaultDataset();
		this.wData=wData;
		for (int i = 0; i < wData.numInstances(); i++) {
			Instance wInst = wData.instance(i);
			data.add(instanceFromWeka(wInst));
		}
	}

	public net.sf.javaml.core.Instance instanceFromWeka(Instance inst) {
		net.sf.javaml.core.Instance out;
		if (inst instanceof SparseInstance) {
			out = new net.sf.javaml.core.SparseInstance();
			SparseInstance tmp = (SparseInstance) inst;
			for (int i = 0; i < tmp.numValues(); i++) {
				int index = inst.index(i);
				double value = inst.value(index);
				out.put(index, value);
			}

		}

		else {
			double[] vals;
			if (inst.classIsMissing())
				vals = inst.toDoubleArray();
			else {
				vals = new double[inst.numAttributes() - 1];
				double[] tmp = inst.toDoubleArray();
				System.arraycopy(tmp, 0, vals, 0, inst.classIndex());
				System.arraycopy(tmp, inst.classIndex() + 1, vals, inst.classIndex(), vals.length - inst.classIndex());

			}
			out = new DenseInstance(vals);

		}
		
		if (!inst.classIsMissing()) {
			out.setClassValue(wData.classAttribute().value((int)inst.classValue()));
		}
		return out;
	}

	public Dataset getDataset() {
		return data;
	}

}
