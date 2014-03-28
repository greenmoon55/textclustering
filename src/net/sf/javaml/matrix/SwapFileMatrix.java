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
package net.sf.javaml.matrix;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A matrix that is stored in a file on disk.
 * 
 * @author Thomas Abeel
 * 
 */
final class SwapFileMatrix extends Matrix {

    private RandomAccessFile matrix;

    private int rows;

    private int cols;

    public SwapFileMatrix(int cols, int rows) throws IOException {
        this.cols = cols;
        this.rows = rows;
        File swapFile = File.createTempFile("swap", "matrix");
        swapFile.deleteOnExit();
        matrix = new RandomAccessFile(swapFile, "rw");
    }

    @Override
    public int columns() {
        return cols;
    }

    @Override
    public double get(int col, int row) {
        try {
            matrix.seek((col * row + row) * 8);
            return matrix.readDouble();
        } catch (IOException e) {
            System.err.println("Something went wrong, but we return 0 anyway.");
            return 0;
        }
    }

    @Override
    public void put(int col, int row, double value) {
        try {
            matrix.seek((col * row + row) * 8);
            matrix.writeDouble(value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @Override
    public int rows() {
        return rows;
    }

}
