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
 * Calculates the Spearman rank correlation of two instances. The value on
 * position 0 of the instance should be the rank of attribute 0. And so on and so forth.
 * 
 * 
 * 
 * 
 * 
 * @version 0.1.7
 * 
 * @linkplain http://en.wikipedia.org/wiki/Spearman's_rank_correlation_coefficient
 * 
 * @author Thomas Abeel
 * 
 */
public class SpearmanRankCorrelation extends AbstractCorrelation {

    private static final long serialVersionUID = -6347213714272482397L;

    @Override
    public double measure(Instance a, Instance b) {
        if (a.noAttributes() != b.noAttributes())
            throw new IllegalArgumentException("Instances should be compatible.");
        long k = a.noAttributes();
        long denom = k * (k * k - 1);
        double sum = 0.0;
        for (int i = 0; i < a.noAttributes(); i++) {
            double diff = (a.value(i) - b.value(i));
            sum += (diff * diff);
        }
        return 1.0 - (6.0 * (sum / ((double) denom)));
    }

}
