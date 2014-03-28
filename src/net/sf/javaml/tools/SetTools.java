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
package net.sf.javaml.tools;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements additional operations on sets.
 * 
 * @author Thomas Abeel
 * 
 */
public class SetTools {
    /**
     * Returns the union of the two sets provided as arguments.
     * 
     * @param a
     *            the first set
     * @param b
     *            the second set
     * @return union of a and b
     */
    public static Set<Integer> union(Set<? extends Integer> a, Set<? extends Integer> b) {
        Set<Integer> out = new HashSet<Integer>();
        out.addAll(a);
        out.addAll(b);
        return out;

    }

    /**
     * Returns the intersection of the two sets provided as arguments.
     * 
     * @param a
     *            the first set
     * @param b
     *            the second set
     * @return intersection of a and b
     */
    public static Set<Integer> intersection(Set<? extends Integer> a, Set<? extends Integer> b) {
        Set<Integer> out = new HashSet<Integer>();
        out.addAll(a);
        out.retainAll(b);
        return out;
    }
}
