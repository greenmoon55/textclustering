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
package net.sf.javaml.distance.fastdtw.dtw;

import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class LinearWindow extends SearchWindow {

    public LinearWindow(TimeSeries tsI, TimeSeries tsJ, int searchRadius) {
        super(tsI.size(), tsJ.size());
        double ijRatio = (double) tsI.size() / (double) tsJ.size();
        boolean isIlargest = tsI.size() >= tsJ.size();
        for (int i = 0; i < tsI.size(); i++)
            if (isIlargest) {
                int j = Math.min((int) Math.round((double) i / ijRatio), tsJ.size() - 1);
                super.markVisited(i, j);
            } else {
                int maxJ = (int) Math.round((double) (i + 1) / ijRatio) - 1;
                int minJ = (int) Math.round((double) i / ijRatio);
                super.markVisited(i, minJ);
                super.markVisited(i, maxJ);
            }

        super.expandWindow(searchRadius);
    }

    public LinearWindow(TimeSeries tsI, TimeSeries tsJ) {
        this(tsI, tsJ, 0);
    }

    private static final int defaultRadius = 0;
}