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

import java.util.SortedSet;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.InstanceTools;

/**
 * This tutorial show how to create a {@link net.sf.javaml.core.Dataset} from a
 * collection of instances. This tutorial assumes you know how to create an
 * {@link net.sf.javaml.core.Instance}. To create instances for this tutorial
 * we will use a method from {@link net.sf.javaml.tools.InstanceTools} to create
 * random instances.
 * 
 * In this tutorial we will create a number of instances and group them in a
 * data set.
 * 
 * Basically a data set is a collection of instances.
 * 
 * 
 * @see CreatingAnInstance
 * @see net.sf.javaml.core.Instance
 * @see net.sf.javaml.core.Dataset
 * @see net.sf.javaml.core.DefaultDataset
 * @see net.sf.javaml.tools.InstanceTools
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialDataset {

    /**
     * Create a data set and put some instances in it.
     */
   public static void main(String[]args){
        Dataset data = new DefaultDataset();
        for (int i = 0; i < 10; i++) {
            Instance tmpInstance = InstanceTools.randomInstance(25);
            data.add(tmpInstance);
        }
        /* Retrieve all class values that are ever used in the data set */
        SortedSet<Object> classValues = data.classes();
        System.out.println(classValues);
   }	
}
