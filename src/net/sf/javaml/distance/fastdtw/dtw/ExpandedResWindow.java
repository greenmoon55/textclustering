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

import net.sf.javaml.distance.fastdtw.matrix.ColMajorCell;
import net.sf.javaml.distance.fastdtw.timeseries.PAA;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class ExpandedResWindow extends SearchWindow {

    public ExpandedResWindow(TimeSeries tsI, TimeSeries tsJ, PAA shrunkI, PAA shrunkJ, WarpPath shrunkWarpPath,
            int searchRadius) {
        super(tsI.size(), tsJ.size());
        int currentI = shrunkWarpPath.minI();
        int currentJ = shrunkWarpPath.minJ();
        int lastWarpedI = 0x7fffffff;
        int lastWarpedJ = 0x7fffffff;
        for (int w = 0; w < shrunkWarpPath.size(); w++) {
            ColMajorCell currentCell = shrunkWarpPath.get(w);
            int warpedI = currentCell.getCol();
            int warpedJ = currentCell.getRow();
            int blockISize = shrunkI.aggregatePtSize(warpedI);
            int blockJSize = shrunkJ.aggregatePtSize(warpedJ);
            if (warpedJ > lastWarpedJ)
                currentJ += shrunkJ.aggregatePtSize(lastWarpedJ);
            if (warpedI > lastWarpedI)
                currentI += shrunkI.aggregatePtSize(lastWarpedI);
            if (warpedJ > lastWarpedJ && warpedI > lastWarpedI) {
                super.markVisited(currentI - 1, currentJ);
                super.markVisited(currentI, currentJ - 1);
            }
            for (int x = 0; x < blockISize; x++) {
                super.markVisited(currentI + x, currentJ);
                super.markVisited(currentI + x, (currentJ + blockJSize) - 1);
            }

            lastWarpedI = warpedI;
            lastWarpedJ = warpedJ;
        }

        super.expandWindow(searchRadius);
    }
}