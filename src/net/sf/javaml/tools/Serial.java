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
package net.sf.javaml.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class with utility methods for serialization.
 * 
 * @author Thomas Abeel
 * 
 */
public class Serial {

    public static boolean exists(String fileName) {
        return (new File(fileName)).exists();
    }

    public static boolean store(Object p, String fileName) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(fileName)));
            out.writeObject(p);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object load(File fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(fileName)));
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object load(String fileName) {
        return load(new File(fileName));
    }
}
