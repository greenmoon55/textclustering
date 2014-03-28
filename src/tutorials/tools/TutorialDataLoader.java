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
package tutorials.tools;

import java.io.File;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

/**
 * This tutorial shows how to load data from a local file.
 * 
 * The two files that are used here can be retrieved from the SVN or at the
 * following URLS:
 * 
 * {@linkplain http 
 * ://java-ml.svn.sourceforge.net/viewvc/java-ml/trunk/devtools/data/iris.data}
 * and {@linkplain http 
 * ://java-ml.svn.sourceforge.net/viewvc/java-ml/trunk/devtools
 * /data/smallsparse.tsv} .
 * 
 * Check out these two files for the dense and sparse file formats. The class
 * label can have any value, the other attributes should be numbers.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialDataLoader {

    public static void main(String[] args) throws Exception {
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        System.out.println(data);
        data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, " ", ":");
        System.out.println(data);

    }

}
