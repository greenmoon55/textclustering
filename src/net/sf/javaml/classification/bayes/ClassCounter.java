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
package net.sf.javaml.classification.bayes;

import java.util.Iterator;
import java.util.Vector;

/**
 * Data structure used for Bayesian networks
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */

 public class ClassCounter {

	protected double[] counter_class;
	protected Vector<Integer>[] classInstanceIDList;

	 ClassCounter(int amountOfClasses) {
		counter_class = new double[amountOfClasses];
		classInstanceIDList = new Vector[amountOfClasses];
		for (int i = 0; i < counter_class.length; i++) {
			counter_class[i] = 0.0;
			classInstanceIDList[i] = new Vector<Integer>();
		}
	}

	/** Sets the name of the example */
	 void setClassInstanceIDList(Vector<Integer> vidlist, int index) {
		classInstanceIDList[index] = vidlist;
	}

	 void addInstanceIDtoList(int index, int iId) {
		classInstanceIDList[index].add(new Integer(iId));
	}

	/** Returns the name of the example */
	 Vector<Integer> getClassInstanceIDList(int index) {
		return classInstanceIDList[index];
	}

	 public Vector<Integer> getClassInstanceIDLists() {
		Vector<Integer> classInstanceIDList_merge = new Vector<Integer>();
		for (int i = 0; i < counter_class.length; i++) {
			Vector<Integer> temp = getClassInstanceIDList(i);
			Iterator it = temp.iterator();
			while (it.hasNext()) {
				int tempp = (Integer) it.next();
				if (!classInstanceIDList_merge.contains(tempp)) {
					classInstanceIDList_merge.add(tempp);
				}
			}
		}
		return classInstanceIDList_merge;
	}

	/** Sets the name of the example */
	 void setCountClass(double amount, int index) {
		counter_class[index] = amount;
	}

	/** Returns the name of the example */
	 double getCountClass(int index) {
		return counter_class[index];
	}

	/** Returns the name of the example */
	// to get prior probs of a featurevalue
	 double getSumAllCountClasses() {
		double sum = 0.0;
		for (int i = 0; i < counter_class.length; i++) {
			sum += counter_class[i];
		}
		return sum;
	}

	 double[] getCounterTable() {
		return counter_class;
	}

}
