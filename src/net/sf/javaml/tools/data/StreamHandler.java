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
package net.sf.javaml.tools.data;

import java.io.Reader;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import be.abeel.io.ColumnIterator;
import be.abeel.io.LineIterator;

public class StreamHandler {
	public static Dataset loadSparse(Reader in, int classIndex, String attSep, String indexSep) {

        ColumnIterator it = new ColumnIterator(in);
        it.setDelimiter(attSep);
        it.setSkipBlanks(true);
        it.setSkipComments(true);
        Dataset out = new DefaultDataset();
        /* to keep track of the maximum number of attributes */
        int maxAttributes = 0;
        for (String[] arr : it) {
            SparseInstance inst = new SparseInstance();

            for (int i = 0; i < arr.length; i++) {
                if (i == classIndex) {
                    inst.setClassValue(arr[i]);
                } else {
                    String[] tmp = arr[i].split(indexSep);
                    double val;
                    try {
                        val = Double.parseDouble(tmp[1]);
                    } catch (NumberFormatException e) {
                        val = Double.NaN;
                    }
                    inst.put(Integer.parseInt(tmp[0]), val);
                }
            }
            if (inst.noAttributes() > maxAttributes)
                maxAttributes = inst.noAttributes();
            out.add(inst);

        }
        for (Instance inst : out) {
            ((SparseInstance) inst).setNoAttributes(maxAttributes);
        }
        return out;
    }
	 public static Dataset load(Reader in, int classIndex, String separator) {

	        LineIterator it = new LineIterator(in);
	        it.setSkipBlanks(true);
	        it.setSkipComments(true);
	        Dataset out = new DefaultDataset();
	        for (String line : it) {
	            String[] arr = line.split(separator);
	            double[] values;
	            if (classIndex == -1)
	                values = new double[arr.length];
	            else
	                values = new double[arr.length - 1];
	            String classValue = null;
	            for (int i = 0; i < arr.length; i++) {
	                if (i == classIndex) {
	                    classValue = arr[i];
	                } else {
	                    double val;
	                    try {
	                        val = Double.parseDouble(arr[i]);
	                    } catch (NumberFormatException e) {
	                        val = Double.NaN;
	                    }
	                    if (classIndex != -1 && i > classIndex)
	                        values[i - 1] = val;
	                    else
	                        values[i] = val;
	                }
	            }
	            out.add(new DenseInstance(values, classValue));

	        }
	        return out;
	    }
	
}
