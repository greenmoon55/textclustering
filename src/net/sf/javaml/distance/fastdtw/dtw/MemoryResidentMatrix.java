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
class MemoryResidentMatrix implements CostMatrix {

    MemoryResidentMatrix(SearchWindow searchWindow) {
        window = searchWindow;
        cellValues = new double[window.size()];
        colOffsets = new int[window.maxI() + 1];
        int currentOffset = 0;
        for (int i = window.minI(); i <= window.maxI(); i++) {
            colOffsets[i] = currentOffset;
            currentOffset += (window.maxJforI(i) - window.minJforI(i)) + 1;
        }

    }

    public void put(int col, int row, double value) {
        if (row < window.minJforI(col) || row > window.maxJforI(col)) {
            throw new InternalError("CostMatrix is filled in a cell (col=" + col + ", row=" + row
                    + ") that is not in the " + "search window");
        } else {
            cellValues[(colOffsets[col] + row) - window.minJforI(col)] = value;
            return;
        }
    }

    public double get(int col, int row) {
        if (row < window.minJforI(col) || row > window.maxJforI(col))
            return (1.0D / 0.0D);
        else
            return cellValues[(colOffsets[col] + row) - window.minJforI(col)];
    }

    public int size() {
        return cellValues.length;
    }

    private static final double OUT_OF_WINDOW_VALUE = (1.0D / 0.0D);

    private final SearchWindow window;

    private double cellValues[];

    private int colOffsets[];
}