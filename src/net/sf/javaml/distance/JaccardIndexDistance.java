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

import net.sf.javaml.core.Instance;

/**
 * 
 * Jaccard index. The distance between two sets is computed in the following
 * way:
 * 
 * <pre>
 *               n1 + n2 - 2*n12 
 * D(S1, S2) = ------------------ 
 *               n1 + n2 - n12
 *               
 *               
 * </pre>
 * 
 * <pre>
 * D(S1,S2) = |S1 ^ S2|
 *            ---------
 *            |S1 u S2|
 * </pre>
 * 
 * 
 * Where n1 and n2 are the numbers of elements in sets S1 and S2, respectively,
 * and n12 is the number that is in both sets (section).
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @linkplain http://en.wikipedia.org/wiki/Jaccard_index
 * 
 * @author Thomas Abeel
 * 
 */
public class JaccardIndexDistance extends AbstractDistance {

    private static final long serialVersionUID = 6715828721744669500L;

    private JaccardIndexSimilarity jci;

    public JaccardIndexDistance() {
        this.jci = new JaccardIndexSimilarity();
    }

    public double measure(Instance a, Instance b) {

        return 1 - jci.measure(a, b);

    }

}
