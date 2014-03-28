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
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/**
 * 
 * Implementation of the FastDTW algorithm as described by Salvador and Chan.
 * 
 * Stan Salvador and Philip Chan, FastDTW: Toward Accurate Dynamic Time Warping
 * in Linear Time and Space, KDD Workshop on Mining Temporal and Sequential
 * Data, pp. 70-80, 2004. http://www.cs.fit.edu/~pkc/papers/tdm04.pdf
 * 
 * 
 * Stan Salvador and Philip Chan, Toward Accurate Dynamic Time Warping in Linear
 * Time and Space, Intelligent Data Analysis, 11(5):561-580, 2007.
 * http://www.cs.fit.edu/~pkc/papers/ida07.pdf
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * @author Philip Chan, pkc@cs.fit.edu
 * 
 * 
 * 
 */
public class FastDTW extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -3604661850260159935L;

    private int radius;

    @Override
    public double measure(Instance x, Instance y) {
        final TimeSeries tsI = new TimeSeries(x);
        final TimeSeries tsJ = new TimeSeries(y);
        final TimeWarpInfo info = net.sf.javaml.distance.fastdtw.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, radius);
        return info.getDistance();
    }

    public FastDTW(int radius) {
        super();
        this.radius = radius;
    }

}
