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
package net.sf.javaml.distance.fastdtw.timeseries;

import java.util.Random;

// Referenced classes of package timeseries:
//            TimeSeries, TimeSeriesPoint

public class SineWave extends TimeSeries {

    public SineWave(int length, double cycles, double noise) {
        super(1);
        for (int x = 0; x < length; x++) {
            double nextPoint = Math.sin(((double) x / (double) length) * 2D * 3.1415926535897931D * cycles)
                    + rand.nextGaussian() * noise;
            super.addLast(x, new TimeSeriesPoint(new double[] { nextPoint }));
        }

    }

    private static final Random rand = new Random();

}