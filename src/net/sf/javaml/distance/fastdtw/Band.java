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
package net.sf.javaml.distance.fastdtw;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractDistance;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.dtw.LinearWindow;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class Band extends AbstractDistance {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1279757837120934014L;

	// PUBLIC FUNCTIONS
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("USAGE:  java Band timeSeries1 timeSeries2 radius");
            System.exit(1);
        } else {
            final TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
            final TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');
            final TimeWarpInfo info = DTW.getWarpInfoBetween(tsI, tsJ, new LinearWindow(tsI, tsJ, Integer
                    .parseInt(args[2])));

            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
        } // end if

    } // end main()

    private int radius = 5;

    public Band(int radius) {
        super();
        this.radius = radius;
    }

    @Override
    public double measure(Instance x, Instance y) {
        final TimeSeries tsI = new TimeSeries(x);
        final TimeSeries tsJ = new TimeSeries(y);
        final TimeWarpInfo info = DTW.getWarpInfoBetween(tsI, tsJ, new LinearWindow(tsI, tsJ, radius));

        System.out.println("Warp Distance: " + info.getDistance());
        System.out.println("Warp Path:     " + info.getPath());
        return info.getDistance();
    }

} // end class Band
