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
package net.sf.javaml.featureselection.ranking;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.featureselection.FeatureRanking;
import net.sf.javaml.featureselection.FeatureScoring;
import net.sf.javaml.utils.ArrayUtils;

/**
 * Creates an attribute ranking from an attribute evaluation technique.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class RankingFromScoring implements FeatureRanking {

    private int[] ranking=null;

    private FeatureScoring ae;

    public RankingFromScoring(FeatureScoring ae) {
        this.ae = ae;
    }

    public void build(Dataset data) {
    	int noAttributes=data.noAttributes();
        ae.build(data);
        double[] values = new double[noAttributes];
        for (int i = 0; i < values.length; i++)
            values[i] = ae.score(i);

        ranking = new int[values.length];
        int[] order = ArrayUtils.sort(values);
        for (int i = 0; i < order.length; i++) {
            ranking[order[i]] = order.length - i - 1;
        }
    }

    public int rank(int attIndex) {
    	if(ranking==null)
    		throw new TrainingRequiredException();
        return ranking[attIndex];
    }

    @Override
    public int noAttributes() {
    	if(ranking==null)
    		throw new TrainingRequiredException();
       return ranking.length;
        
    }

}
