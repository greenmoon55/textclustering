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
import java.util.Map;

import libsvm.LibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.sampling.Sampling;
import net.sf.javaml.tools.data.FileHandler;
import be.abeel.util.Pair;

/**
 * Sample program illustrating how to use sampling.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialSampling {

	public static void main(String[] args) throws Exception {

		Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");

		Sampling s = Sampling.SubSampling;

		for (int i = 0; i < 5; i++) {
			Pair<Dataset, Dataset> datas = s.sample(data, (int) (data.size() * 0.8), i);
			Classifier c = new LibSVM();
			c.buildClassifier(datas.x());
			Map<Object,PerformanceMeasure> pms = EvaluateDataset.testDataset(c, datas.y());
			System.out.println(pms);

		}

	}
}
