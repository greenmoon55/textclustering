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

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

public class TimeSeriesPoint {

    public TimeSeriesPoint(double values[]) {
        hashCode = 0;
        measurements = new double[values.length];
        for (int x = 0; x < values.length; x++) {
            hashCode += (new Double(values[x])).hashCode();
            measurements[x] = values[x];
        }

    }

    public TimeSeriesPoint(Collection values) {
        measurements = new double[values.size()];
        hashCode = 0;
        Iterator i = values.iterator();
        for (int index = 0; i.hasNext(); index++) {
            Object nextElement = i.next();
            if (nextElement instanceof Double)
                measurements[index] = ((Double) nextElement).doubleValue();
            else if (nextElement instanceof Integer)
                measurements[index] = ((Integer) nextElement).doubleValue();
            else if (nextElement instanceof BigInteger)
                measurements[index] = ((BigInteger) nextElement).doubleValue();
            else
                throw new InternalError("ERROR:  The element " + nextElement + " is not a valid numeric type");
            hashCode += (new Double(measurements[index])).hashCode();
        }

    }

    public double get(int dimension) {
        return measurements[dimension];
    }

    public void set(int dimension, double newValue) {
        hashCode -= (new Double(measurements[dimension])).hashCode();
        measurements[dimension] = newValue;
        hashCode += (new Double(newValue)).hashCode();
    }

    public double[] toArray() {
        return measurements;
    }

    public int size() {
        return measurements.length;
    }

    public String toString() {
        String outStr = "(";
        for (int x = 0; x < measurements.length; x++) {
            outStr = outStr + measurements[x];
            if (x < measurements.length - 1)
                outStr = outStr + ",";
        }

        outStr = outStr + ")";
        return outStr;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof TimeSeriesPoint) {
            double testValues[] = ((TimeSeriesPoint) o).toArray();
            if (testValues.length == measurements.length) {
                for (int x = 0; x < measurements.length; x++)
                    if (measurements[x] != testValues[x])
                        return false;

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return hashCode;
    }

    private double measurements[];

    private int hashCode;
}