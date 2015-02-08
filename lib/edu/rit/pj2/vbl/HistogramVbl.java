//******************************************************************************
//
// File:    HistogramVbl.java
// Package: edu.rit.pj2.vbl
// Unit:    Class edu.rit.pj2.vbl.HistogramVbl
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

import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.io.Streamable;
import edu.rit.numeric.Statistics;
import edu.rit.pj2.Vbl;
import edu.rit.util.Instance;
import java.io.IOException;

/**
 * Class HistogramVbl is the abstract base class for a histogram that
 * categorizes values into bins and counts the number of occurrences in each
 * bin. Subclasses of class HistogramVbl categorize items of various types.
 * <P>
 * Class HistogramVbl supports doing a chi-square test on the bin counts. See
 * the {@link #chisqr() chisqr()}, {@link #pvalue(double) pvalue()}, and {@link
 * #expectedCount(int) expectedCount()} methods.
 * <P>
 * Class HistogramVbl supports the <I>parallel reduction</I> pattern. Each
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
public abstract class HistogramVbl
	implements Vbl, Streamable
	{

// Hidden data members.

	private int B;        // Number of bins
	private long[] count; // Count in each bin
	private long total;   // Total count in all bins

// Exported constructors.

	/**
	 * Construct a new uninitialized histogram. This constructor is for use only
	 * by object streaming.
	 */
	public HistogramVbl()
		{
		}

	/**
	 * Construct a new histogram with the given number of bins.
	 *
	 * @param  B  Number of bins (&ge; 2).
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>B</TT> &lt; 2.
	 */
	public HistogramVbl
		(int B)
		{
		if (B < 2)
			throw new IllegalArgumentException (String.format
				("HistogramVbl(): B = %d illegal", B));
		this.B = B;
		this.count = new long [B];
		this.total = 0;
		}

	/**
	 * Construct a new histogram that is a deep copy of the given histogram.
	 *
	 * @param  hist  Histogram to copy.
	 */
	public HistogramVbl
		(HistogramVbl hist)
		{
		copy (hist);
		}

// Exported operations.

	/**
	 * Clear this histogram. All the bin counts are set to zero.
	 */
	public void clear()
		{
		for (int i = 0; i < B; ++ i)
			count[i] = 0L;
		total = 0L;
		}

	/**
	 * Make this histogram be a deep copy of the given histogram.
	 *
	 * @param  hist  Histogram to copy.
	 */
	public void copy
		(HistogramVbl hist)
		{
		this.B = hist.B;
		this.count = (long[]) hist.count.clone();
		this.total = hist.total;
		}

	/**
	 * Returns the number of bins in this histogram.
	 *
	 * @return  Number of bins.
	 */
	public int size()
		{
		return B;
		}

	/**
	 * Returns the count in the given bin of this histogram.
	 *
	 * @param  i  Bin number.
	 *
	 * @return  Count in bin <TT>i</TT>.
	 */
	public long count
		(int i)
		{
		return count[i];
		}

	/**
	 * Returns the total count in all bins of this histogram.
	 *
	 * @return  Total count.
	 */
	public long total()
		{
		return total;
		}

	/**
	 * Returns the chi-square statistic for this histogram. The expected count
	 * in bin <I>i</I> is determined by calling the {@link #expectedCount(int)
	 * expectedCount(i)} method.
	 *
	 * @return  Chi-square statistic.
	 */
	public double chisqr()
		{
		double chisqr = 0.0;
		for (int i = 0; i < B; ++ i)
			{
			double expected = expectedCount (i);
			double d = expected - count[i];
			chisqr += d*d/expected;
			}
		return chisqr;
		}

	/**
	 * Returns the p-value of the given chi-square statistic for this histogram.
	 * The chi-square statistic is assumed to obey the chi-square distribution
	 * with <I>B</I>&minus;1 degrees of freedom, where <I>B</I> is the number of
	 * bins.
	 *
	 * @param  chisqr  Chi-square statistic.
	 *
	 * @return  P-value of <TT>chisqr</TT>.
	 */
	public double pvalue
		(double chisqr)
		{
		return Statistics.chiSquarePvalue (B - 1, chisqr);
		}

	/**
	 * Create a clone of this shared variable.
	 *
	 * @return  The cloned object.
	 */
	public Object clone()
		{
		try
			{
			HistogramVbl vbl = Instance.newDefaultInstance (this.getClass());
			vbl.copy (this);
			return vbl;
			}
		catch (Throwable exc)
			{
			throw new IllegalArgumentException
				("HistogramVbl.clone(): Could not create new instance",
				 exc);
			}
		}

	/**
	 * Set this shared variable to the given shared variable.
	 *
	 * @param  vbl  Shared variable.
	 */
	public void set
		(Vbl vbl)
		{
		copy ((HistogramVbl)vbl);
		}

	/**
	 * Reduce the given shared variable into this shared variable.
	 * <P>
	 * The reduction is performed by adding the count in each bin of
	 * <TT>vbl</TT> to the count in the corresponding bin of this histogram.
	 *
	 * @param  vbl  Shared variable.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>vbl</TT> does not have the same
	 *     number of bins as this histogram.
	 */
	public void reduce
		(Vbl vbl)
		{
		HistogramVbl hist = (HistogramVbl)vbl;
		if (hist.B != this.B)
			throw new IllegalArgumentException
				("HistogramVbl.reduce(): Histograms are different sizes");
		for (int i = 0; i < B; ++ i)
			this.count[i] += hist.count[i];
		this.total += hist.total;
		}

	/**
	 * Write this histogram to the given out stream.
	 *
	 * @param  out  Out stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void writeOut
		(OutStream out)
		throws IOException
		{
		out.writeInt (B);
		out.writeLongArray (count);
		out.writeLong (total);
		}

	/**
	 * Read this histogram from the given in stream.
	 *
	 * @param  in  In stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void readIn
		(InStream in)
		throws IOException
		{
		B = in.readInt();
		count = in.readLongArray();
		total = in.readLong();
		}

// Hidden operations.

	/**
	 * Increment the given bin in this histogram. The subclass of class
	 * HistogramBin determines the manner in which values are categorized into
	 * bins.
	 *
	 * @param  i  Bin number.
	 */
	protected void increment
		(int i)
		{
		++ count[i];
		++ total;
		}

	/**
	 * Determine the expected count in the given bin for a chi-square test.
	 * <P>
	 * The base class implementation of this method assumes that the bin counts
	 * are supposed to be all the same; thus, the expected count in each bin is
	 * the total count divided by the number of bins. A subclass can override
	 * this method to return different expected counts.
	 *
	 * @param  i  Bin number.
	 *
	 * @return  Expected count in bin <TT>i</TT>.
	 */
	protected double expectedCount
		(int i)
		{
		return (double)total/(double)B;
		}

	}
