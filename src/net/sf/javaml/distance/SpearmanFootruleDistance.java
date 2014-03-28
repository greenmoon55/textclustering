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

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * TODO WRITE DOC
 * @author Thomas Abeel
 *
 */
public class SpearmanFootruleDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -6347213714272482397L;

    public double measure(Instance a, Instance b) {
        if (a.noAttributes() != b.noAttributes())
            throw new IllegalArgumentException("Instances should be compatible.");
        long k = a.noAttributes();
        long denom;
        if(k%2==0)
            denom=(k*k)/2;
        else
            denom=((k+1)*(k-1))/2;
        double sum = 0.0;
        for (int i = 0; i < a.noAttributes(); i++) {
            double diff = Math.abs(a.value(i) - b.value(i));
            sum += diff;
        }
        return 1.0 - (sum / ((double) denom));
    }

    public double getMaximumDistance(Dataset data) {
        return 1;
    }

    public double getMinimumDistance(Dataset data) {
        return 0;
    }

}
