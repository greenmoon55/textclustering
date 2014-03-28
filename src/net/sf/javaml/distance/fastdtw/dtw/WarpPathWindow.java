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

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class WarpPathWindow extends SearchWindow {

    public WarpPathWindow(WarpPath path, int searchRadius) {
        super(path.get(path.size() - 1).getCol() + 1, path.get(path.size() - 1).getRow() + 1);
        for (int p = 0; p < path.size(); p++)
            super.markVisited(path.get(p).getCol(), path.get(p).getRow());

        super.expandWindow(searchRadius);
    }

    private static final int defaultRadius = 0;
}