//******************************************************************************
//
// File:    IntHistogramVbl.java
// Package: edu.rit.pj2.vbl
// Unit:    Class edu.rit.pj2.vbl.IntHistogramVbl
//
// This Java source file is copyright (C) 2015 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java 2 Library ("PJ2"). PJ2 is
// free software; you can redistribute it and/or modify it under the terms of
// the GNU General Public License as published by the Free Software Foundation;
// either version 3 of the License, or (at your option) any later version.
//
// PJ2 is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

package edu.rit.pj2.vbl;

/**
 * Class IntHistogramVbl is the abstract base class for a histogram that
 * categorizes values of type <TT>int</TT> into bins and counts the number of
 * occurrences in each bin. The {@link #bin(int) bin()} method determines the
 * bin for a given value; this method must be overridden in a subclass.
 * <P>
 * Class IntHistogramVbl supports doing a chi-square test on the bin counts. See
 * the {@link #chisqr() chisqr()}, {@link #pvalue(double) pvalue()}, and {@link
 * #expectedCount(int) expectedCount()} methods.
 * <P>
 * Class IntHistogramVbl supports the <I>parallel reduction</I> pattern. Each
 * thread creates a thread-local copy of the shared variable by calling the
 * {@link edu.rit.pj2.Loop#threadLocal(Vbl) threadLocal()} method of class
 * {@linkplain edu.rit.pj2.Loop Loop} or the {@link
 * edu.rit.pj2.Section#threadLocal(Vbl) threadLocal()} method of class
 * {@linkplain edu.rit.pj2.Section Section}. Each thread performs operations on
 * its own copy, without needing to synchronize with the other threads. At the
 * end of the parallel statement, the thread-local copies are automatically
 * <I>reduced</I> together, and the result is stored in the original shared
 * variable. The reduction is performed by the shared variable's {@link
 * #reduce(Vbl) reduce()} method.
 *
 * @author  Alan Kaminsky
 * @version 06-Feb-2015
 */
public abstract class IntHistogramVbl
	extends HistogramVbl
	{

// Exported constructors.

	/**
	 * Construct a new uninitialized histogram. This constructor is for use only
	 * by object streaming.
	 */
	public IntHistogramVbl()
		{
		super();
		}

	/**
	 * Construct a new histogram with the given number of bins.
	 *
	 * @param  B  Number of bins (&ge; 2).
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>B</TT> &lt; 2.
	 */
	public IntHistogramVbl
		(int B)
		{
		super (B);
		}

	/**
	 * Construct a new histogram that is a deep copy of the given histogram.
	 *
	 * @param  hist  Histogram to copy.
	 */
	public IntHistogramVbl
		(HistogramVbl hist)
		{
		super (hist);
		}

// Exported operations.

	/**
	 * Accumulate the given value into this histogram. The bin returned by the
	 * {@link #bin(int) bin(x)} method is incremented.
	 *
	 * @param  x  Value.
	 */
	public void accumulate
		(int x)
		{
		increment (bin (x));
		}

// Hidden operations.

	/**
	 * Determine the bin corresponding to the given value. A subclass must
	 * override this method.
	 *
	 * @param  x  Value.
	 *
	 * @return  Bin number.
	 */
	protected abstract int bin
		(int x);

	}
