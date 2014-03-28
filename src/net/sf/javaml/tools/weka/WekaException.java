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
package net.sf.javaml.tools.weka;

/**
 * This exception should be thrown when something went wrong with calls to the
 * WEKA library.
 * 
 * 
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class WekaException extends RuntimeException {

    public WekaException() {
        super();
    }

    public WekaException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public WekaException(String arg0) {
        super(arg0);
    }

    public WekaException(Throwable arg0) {
        super(arg0);
    }

    private static final long serialVersionUID = 185381938656230128L;

}
