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
package tutorials.classification;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import tutorials.TutorialData;

/**
 * Tutorial for Naive Bayes classifier
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
public class TutorialNaiveBayes {

	public static void main(String[] args) throws Exception {

		/* Load a data set */
		Dataset data = TutorialData.IRIS.load();

		/* Discretize through EqualWidtBinning */
		EqualWidthBinning eb = new EqualWidthBinning(20);
		System.out.println("Start discretisation");
		eb.build(data);
		Dataset ddata = data.copy();
		eb.filter(ddata);

		boolean useLaplace = true;
		boolean useLogs = true;
		Classifier nbc = new NaiveBayesClassifier(useLaplace, useLogs, false);
		nbc.buildClassifier(data);

		Dataset dataForClassification = TutorialData.IRIS.load();

		/* Counters for correct and wrong predictions. */
		int correct = 0, wrong = 0;

		/* Classify all instances and check with the correct class values */
		for (Instance inst : dataForClassification) {
			eb.filter(inst);
			Object predictedClassValue = nbc.classify(inst);
			Object realClassValue = inst.classValue();
			if (predictedClassValue.equals(realClassValue))
				correct++;
			else {
				wrong++;

			}

		}
		System.out.println("correct " + correct);
		System.out.println("incorrect " + wrong);

	}

}
