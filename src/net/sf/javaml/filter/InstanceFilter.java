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
package net.sf.javaml.filter;

import net.sf.javaml.core.Instance;

/**
 * The interface for filters that can be applied on an
 * {@link net.sf.javaml.core.Instance} without the need for a reference
 * {@link net.sf.javaml.core.Dataset}.
 * 
 * When applying a filter to an instance it may modify the instance and will
 * return the modified version of the instance.
 * 
 * 
 * 
 * @see Instance
 * @see Dataset
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public interface InstanceFilter {

    /**
     * Applies this filter to an instance 
     * 
     * @param inst
     *            the instance to apply this filter to
     * @return the modified instance
     */
    public void filter(Instance inst);


}
