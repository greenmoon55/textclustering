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
class WindowMatrix implements CostMatrix {

    WindowMatrix(SearchWindow searchWindow) {
        try {
            windowCells = new MemoryResidentMatrix(searchWindow);
        } catch (OutOfMemoryError e) {
            System.err
                    .println("Ran out of memory initializing window matrix, all cells in the window cannot fit into main memory.  Will use a swap file instead (will run ~50% slower)");
            System.gc();
            windowCells = new SwapFileMatrix(searchWindow);
        }
    }

    public void put(int col, int row, double value) {
        windowCells.put(col, row, value);
    }

    public double get(int col, int row) {
        return windowCells.get(col, row);
    }

    public int size() {
        return windowCells.size();
    }

    public void freeMem() {
        if (windowCells instanceof SwapFileMatrix)
            try {
                ((SwapFileMatrix) windowCells).freeMem();
            } catch (Throwable t) {
            }
    }

    private CostMatrix windowCells;
}