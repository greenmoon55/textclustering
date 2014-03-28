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
package net.sf.javaml.distance;

import java.util.HashSet;

import net.sf.javaml.core.Instance;

/**
 * Consistency index for a pair of subsets.
 * 
 * Based on definition 1 in L. Kuncheva 2007, A stability index for features
 * selection, Proceedings of the 25th IASTED international conference on
 * artificial intelligence and applications.
 * 
 * <code>
 * I(A,B)=r*n-k*k
 *        -------
 *        k*(n-k)
 * with:
 * n = original number of features, i.e. n=|X| with A &sub; X and B &sub; X
 * k = number of features in A and B, i.e. k=|A|=|B|
 * r = number of feature in common between A and B, i.e. r=|A &cap; B| 
 * </code>
 * 
 * @author Thomas Abeel
 * 
 */
public class ConsistencyIndex extends AbstractDistance {

    private static final long serialVersionUID = -9108138773263724130L;

    /* original number of features. */
    private double n;

    public ConsistencyIndex(int n) {
        this.n = n;
    }

    @Override
    public double measure(Instance a, Instance b) {
    	HashSet<Integer> set1 = new HashSet<Integer>();
        HashSet<Integer> set2 = new HashSet<Integer>();

        for (int i = 0; i < a.noAttributes(); i++)
            set1.add((int) a.value(i));

        for (int i = 0; i < b.noAttributes(); i++)
            set2.add((int) b.value(i));
        
             
        double k = Math.max(set1.size(),set2.size());
        /* exceptional cases */
        if (k == 0 || k == n)
            return 0;

        /* normal calculation */
        set1.retainAll(set2);
        double r = set1.size(); //
              
        return (r * n - k * k) / (k * (n - k));
    }
}
