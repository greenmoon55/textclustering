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
package net.sf.javaml.classification.bayes;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Implementation of the Naive Bayes classification algorithm.
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 * 
 */
public class NaiveBayesClassifier extends AbstractBayesianClassifier {

	private static final long serialVersionUID = -3206001837043122519L;

	/**
	 * Instantiate the Naive Bayes algorithm with inclusion of laplace
	 * correction
	 * 
	 * @param lap
	 *            laplace correction
	 * @param log
	 *            logarithmic results to avoid rounding results to zero because
	 *            of limited computer precision
	 * @param sparse
	 *            sparseness of used dataset
	 */
	public NaiveBayesClassifier(boolean lap, boolean log, boolean sparse) {
		super(lap, log, sparse);
	}

	@Override
	public void buildClassifier(Dataset data) {
		super.buildClassifier(data);
	}

	/**
	 * Calculates the probability that testExample belongs a certain class
	 * 
	 * @param testExample
	 *            The test example to be categorized
	 */

	protected HashMap<Object, Double> calculateProbs(Instance inst) {

		HashMap<Object, Double> out = new HashMap<Object, Double>(numClasses);
		coverAbsentFeatures_And_fill_helpMap(inst);
		// fetch conditional freqs
		Hashtable<Integer, Hashtable<Double, ClassCounter>> featureName_HT = trainResult
				.getFeatureTable();
		double[] freq = trainResult.getClassFreqs().clone();
		
		// Normalisation factor
		double total = 0;
		
		// Subtraction/addition of log2 instead of division/multiplication
		for (int k = 0; k < numClasses; k++) {

			double denominator = freq[k];
			double classScore = fnc.log2(freq[k]) - fnc.log2(numInstances); 

			for (Object key : featureName_HT.keySet()) {

				int featureName = (Integer) key;
				int numValues = featureName_HT.get(featureName).size();
				Double featureValue = getInstValue(featureName, inst);
				double numerator = featureName_HT.get(featureName).get(
						featureValue).getCountClass(k);
				// Laplace correction
				classScore += fnc
						.log2(numerator + 1) - fnc.log2(denominator + numValues); 
			}
			
			out.put(classes[k], classScore);
			total = total + Math.pow(2,classScore); 
		}
		
		// Normalizing to probabilities 
		for (int l = 0; l < classes.length; l++) { 
			double classScore = out.get(classes[l]); 
			out.put(classes[l], Math.pow(2.0,(classScore-fnc.log2(total)))); 
		}

		return out; 
	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		return calculateProbs(instance);
	}

}
