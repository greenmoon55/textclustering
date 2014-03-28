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
package tutorials.core;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

/**
 * Shows how to create a SparseInstance. A SparseInstance has a default value of
 * 0 for all it attributes, but you can set some of them to other values. Just
 * like the DenseInstance, the SparseInstance also has an optional class label.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialSparseInstance {

    /**
     * Shows how to construct a SparseInstance.
     */
    public static void main(String[]args){
        /*
         * Here we will create an instance with 10 attributes, but will only set
         * the attributes with index 1,3 and 7 with a value.
         */
        /* Create instance with 10 attributes */
        Instance instance = new SparseInstance(10);
        /* Set the values for particular attributes */
        instance.put(1, 1.0);
        instance.put(3, 2.0);
        instance.put(7, 4.0);
    }
}
