//******************************************************************************
//
// File:    Histogram.java
// Package: edu.rit.pj2.example
// Unit:    Class edu.rit.pj2.example.Histogram
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

package edu.rit.pj2.example;

import edu.rit.numeric.Statistics;
import edu.rit.pj2.Vbl;

/**
 * Class Histogram provides a histogram used for a statistical test on a
 * pseudorandom number generator.
 *
 * @author  Alan Kaminsky
 * @version 16-Jan-2015
 */
public class Histogram
	implements Vbl
	{

// Hidden data members.

	private int B;        // Number of bins
	private long[] count; // Count in each bin
	private long total;   // Total count in all bins

// Exported constructors.

	/**
	 * Construct a new histogram with the given number of bins.
	 *
	 * @param  B  Number of bins (&ge; 2).
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>B</TT> &lt; 2.
	 */
	public Histogram
		(int B)
		{
		if (B < 2)
			throw new IllegalArgumentException (String.format
				("Histogram(): B = %d illegal", B));
		this.B = B;
		this.count = new long [B];
		this.total = 0;
		}

	/**
	 * Construct a new histogram that is a deep copy of the given histogram.
	 *
	 * @param  hist  Histogram to copy.
	 */
	public Histogram
		(Histogram hist)
		{
		copy (hist);
		}

// Exported operations.

	/**
	 * Make this histogram be a deep copy of the given histogram.
	 *
	 * @param  hist  Histogram to copy.
	 */
	public void copy
		(Histogram hist)
		{
		this.B = hist.B;
		this.count = (long[]) hist.count.clone();
		this.total = hist.total;
		}

	/**
	 * Accumulate the given random number into this histogram.
	 *
	 * @param  x  Random number &ge; 0.0, &lt; 1.0.
	 */
	public void accumulate
		(double x)
		{
		++ count[(int)(x*B)];
		++ total;
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
	 * Returns the chi-square statistic for this histogram. The null hypothesis
	 * is that the accumulated random numbers are uniformly distributed between
	 * 0.0 and 1.0.
	 *
	 * @return  Chi-square statistic.
	 */
	public double chisqr()
		{
		double expected = (double)total/(double)B;
		double chisqr = 0.0;
		for (int i = 0; i < B; ++ i)
			{
			double d = expected - count[i];
			chisqr += d*d;
			}
		return chisqr/expected;
		}

	/**
	 * Returns the p-value of the given chi-square statistic for this histogram.
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
		return new Histogram (this);
		}

	/**
	 * Set this shared variable to the given shared variable.
	 *
	 * @param  vbl  Shared variable.
	 */
	public void set
		(Vbl vbl)
		{
		copy ((Histogram)vbl);
		}

	/**
	 * Reduce the given shared variable into this shared variable.
	 *
	 * @param  vbl  Shared variable.
	 */
	public void reduce
		(Vbl vbl)
		{
		Histogram hist = (Histogram)vbl;
		if (hist.B != this.B)
			throw new IllegalArgumentException();
		for (int i = 0; i < B; ++ i)
			this.count[i] += hist.count[i];
		this.total += hist.total;
		}

	}
