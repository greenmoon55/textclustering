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
package net.sf.javaml.featureselection.ensemble;

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.featureselection.FeatureRanking;
import net.sf.javaml.utils.ArrayUtils;

/**
 * Provides a linear aggregation feature selection ensemble as described in
 * Saeys et al. 2008.
 * 
 * <pre>
 * Saeys, Y., Abeel, T., Van de Peer, Y. (2008) Robust Feature Selection using 
 * Ensemble Feature Selection Techniques. Proceedings of ECML/PKDD 5212, 313-25.
 * </pre>
 * 
 * @author Thomas Abeel
 * 
 */
public class LinearRankingEnsemble implements FeatureRanking {
	/* Feature rankers */
	private FeatureRanking[] aes;

	/* Random generator for bootstraps */
	private Random rg;

	/**
	 * Creates a ranking ensemble with the provided single feature rankers.
	 * 
	 * @param aes
	 *            array of feature rankers
	 */
	public LinearRankingEnsemble(FeatureRanking[] aes) {
		this(aes, new Random(System.currentTimeMillis()));
	}

	/**
	 * Creates a ranking ensemble with the provided single feature rankers and a
	 * specified random generator used for the generation of bootstraps.
	 * 
	 * @param aes
	 *            array of feature rankers
	 * @param rg
	 *            random generator to create bootstraps
	 */
	public LinearRankingEnsemble(FeatureRanking[] aes, Random rg) {
		this.aes = aes;
		this.rg = rg;
	}

	private int[] ranking;

	@Override
	public void build(Dataset data) {
		int numAtt = data.noAttributes();
		/* [i] contains the sum of ranks of feature i */
		double[] sum = new double[numAtt];
		for (FeatureRanking ae : aes) {
			Dataset bootstrapData = new DefaultDataset();
			while (bootstrapData.size() < data.size()) {
				int random = rg.nextInt(data.size());
				bootstrapData.add(data.get(random));
			}
			Dataset copy = bootstrapData.copy();
			ae.build(copy);
			for (int i = 0; i < numAtt; i++)
				sum[i] += ae.rank(i);
		}
		toRank(sum);

	}

	private void toRank(double[] sum) {
		int[] order = ArrayUtils.sort(sum);
		ranking = new int[order.length];
		for (int i = 0; i < order.length; i++) {
			ranking[order[i]] = i;
		}
	}

	@Override
	public int rank(int attIndex) {
		return ranking[attIndex];
	}

	@Override
	public int noAttributes() {
		return ranking.length;
	}

}
