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
package tutorials.featureselection;

import java.io.File;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.featureselection.scoring.GainRatio;
import net.sf.javaml.tools.data.FileHandler;

public class TutorialFeatureScoring {
    /**
     * Shows the basic steps to create use a feature scoring algorithm.
     * 
     * @author Thomas Abeel
     * 
     */
    public static void main(String[] args) throws Exception {
        /* Load the iris data set */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");

        GainRatio ga = new GainRatio();
        /* Apply the algorithm to the data set */
        ga.build(data);
        /* Print out the score of each attribute */
        for (int i = 0; i < ga.noAttributes(); i++)
            System.out.println(ga.score(i));
    }

}
