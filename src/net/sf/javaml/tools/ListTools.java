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

import java.util.ArrayList;
import java.util.List;

/**
 * Implements additional operations for lists
 * 
 * @author Thomas Abeel
 * 
 */
public class ListTools {

	/**
	 * Create a list of the specified size filled with integers from 0 to size-1
	 * 
	 * @param size
	 *            size of the returned list
	 * @return list of integers with each value equal to position in the list
	 */
	public static List<Integer> incfill(int size) {
		List<Integer>out=new ArrayList<Integer>(size);
		for(int i=0;i<size;i++)
			out.add(i);
		return out;
	}
}
