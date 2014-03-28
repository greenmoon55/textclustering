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
package net.sf.javaml.distance.fastdtw.lang;


public class TypeConversions
{

    public TypeConversions()
    {
    }

    public static byte[] doubleToByteArray(double number)
    {
        long longNum = Double.doubleToLongBits(number);
        return (new byte[] {
            (byte)(int)(longNum >>> 56 & 255L), (byte)(int)(longNum >>> 48 & 255L), (byte)(int)(longNum >>> 40 & 255L), (byte)(int)(longNum >>> 32 & 255L), (byte)(int)(longNum >>> 24 & 255L), (byte)(int)(longNum >>> 16 & 255L), (byte)(int)(longNum >>> 8 & 255L), (byte)(int)(longNum >>> 0 & 255L)
        });
    }

    public static byte[] doubleArrayToByteArray(double numbers[])
    {
        int doubleSize = 8;
        byte byteArray[] = new byte[numbers.length * 8];
        for(int x = 0; x < numbers.length; x++)
            System.arraycopy(doubleToByteArray(numbers[x]), 0, byteArray, x * 8, 8);

        return byteArray;
    }
}