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

// Referenced classes of package timeseries:
//            TimeSeries, TimeSeriesPoint

public class PAA extends TimeSeries {

    public PAA(TimeSeries ts, int shrunkSize) {
        if (shrunkSize > ts.size())
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation may not be largerr than the \noriginal time series (shrunkSize="
                            + shrunkSize + " , origSize=" + ts.size() + ").");
        if (shrunkSize <= 0)
            throw new InternalError(
                    "ERROR:  The size of an aggregate representation must be greater than zero and \nno larger than the original time series.");
        originalLength = ts.size();
        aggPtSize = new int[shrunkSize];
        super.setMaxCapacity(shrunkSize);
        setLabels(ts.getLabels());
        double reducedPtSize = (double) ts.size() / (double) shrunkSize;
        int ptToReadTo;
        for (int ptToReadFrom = 0; ptToReadFrom < ts.size(); ptToReadFrom = ptToReadTo + 1) {
            ptToReadTo = (int) Math.round(reducedPtSize * (double) (size() + 1)) - 1;
            int ptsToRead = (ptToReadTo - ptToReadFrom) + 1;
            double timeSum = 0.0D;
            double measurementSums[] = new double[ts.numOfDimensions()];
            for (int pt = ptToReadFrom; pt <= ptToReadTo; pt++) {
                double currentPoint[] = ts.getMeasurementVector(pt);
                timeSum += ts.getTimeAtNthPoint(pt);
                for (int dim = 0; dim < ts.numOfDimensions(); dim++)
                    measurementSums[dim] += currentPoint[dim];

            }

            timeSum /= ptsToRead;
            for (int dim = 0; dim < ts.numOfDimensions(); dim++)
                measurementSums[dim] = measurementSums[dim] / (double) ptsToRead;

            aggPtSize[super.size()] = ptsToRead;
            addLast(timeSum, new TimeSeriesPoint(measurementSums));
        }

    }

    public int originalSize() {
        return originalLength;
    }

    public int aggregatePtSize(int ptIndex) {
        return aggPtSize[ptIndex];
    }

    public String toString() {
        return "(" + originalLength + " point time series represented as " + size() + " points)\n" + super.toString();
    }

    private int aggPtSize[];

    private final int originalLength;
}