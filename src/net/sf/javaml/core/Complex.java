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
package net.sf.javaml.core;

/**
 * Implements a mutable Complex number.
 * 
 * 
 * 
 * @see ComplexInstance
 * 
 * @version 0.1.7
 * 
 * @author Thomas Abeel
 * 
 */
public class Complex {

    /**
     * The imaginary part of this complex number.
     */
    public double im = 0;

    /**
     * The real part of this imaginary number
     */
    public double re = 0;

    /**
     * Creates a new Complex number with the supplied real and complex part.
     * 
     * @param re
     *            the real part
     * @param im
     *            the complex part
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Creates a new Complex number with 0 for its real and imaginary part.
     */
    public Complex() {
        this(0, 0);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return re + " + " + im + "i";
    }

    /**
     * Takes the absolute value of this complex number. Basically it returns
     * |this|.
     * 
     * @return the absolute value of this complex number
     */
    public double abs() {
        return Math.sqrt(re * re + im * im);
    }

    /**
     * Adds a complex number to this one.
     * 
     * @param b
     *            the number to add to this one.
     */
    public void plus(Complex b) {
        re += b.re;
        im += b.re;
    }

    /**
     * Subtracts a complex number from this one.
     * 
     * @param b
     *            the number to subtract from this one.
     */
    public void minus(Complex b) {
        re -= b.re;
        im -= b.im;
    }

    /**
     * Multiplies this complex number with another one.
     * 
     * @param b
     *            the number to multiply with.
     */
    public void times(Complex b) {
        double real = re * b.re - im * b.im;
        double imag = re * b.im + im * b.re;
        re = real;
        im = imag;
    }

    /**
     * Multiplies with real number
     * 
     * @param alpha
     *            the real number to multiply with.
     */
    public void times(double alpha) {
        re *= alpha;
        im *= alpha;
    }

    /**
     * Takes the conjugate of this complex number.
     * 
     */
    public void conjugate() {
        im = -im;
    }

    /**
     * Adds two complex numbers and return the result.
     * 
     * @param a
     *            first complex number
     * @param b
     *            second complex number
     * @return the sum of a and b
     */
    public static Complex plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    /**
     * Multiplies a complex number with a real value.
     * 
     * @param a
     *            the complex number
     * @param d
     *            the real value
     * @return the resulting complex number
     */
    public static Complex multiply(Complex a, double d) {
        return new Complex(a.re * d, a.im * d);
    }

    /**
     * Multiplies two complex numbers and return the resulting complex number.
     * 
     * @param a
     *            the first complex number
     * @param b
     *            the second complex number
     * @return the result of the multiplication of a and b
     */
    public static Complex multiply(Complex a, Complex b) {
        // (x + yi)(u + vi) = (xu - yv) + (xv + yu)i.
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    /**
     * The imaginary constant I
     */
    public static final Complex I = new Complex(0, 1);

}
