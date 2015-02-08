//******************************************************************************
//
// File:    WebLog04.java
// Package: edu.rit.pjmr.example
// Unit:    Class edu.rit.pjmr.example.WebLog04
//
// This Java source file is copyright (C) 2014 by Alan Kaminsky. All rights
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

package edu.rit.pjmr.example;

import edu.rit.numeric.ListXYSeries;
import edu.rit.numeric.plot.Plot;
import edu.rit.pj2.vbl.LongVbl;
import edu.rit.pjmr.Combiner;
import edu.rit.pjmr.Customizer;
import edu.rit.pjmr.Mapper;
import edu.rit.pjmr.PjmrJob;
import edu.rit.pjmr.Reducer;
import edu.rit.pjmr.TextFileSource;
import edu.rit.pjmr.TextId;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

/**
 * Class WebLog04 is the main program for a PJMR map-reduce job that analyzes
 * web server log files.
 * <P>
 * Usage: <TT>java pj2 [threads=<I>NT</I>] edu.rit.pjmr.example.WebLog04
 * <I>plotfile</I> <I>logfile</I> <I>node</I> [<I>node</I> ...]</TT>
 * <P>
 * The program runs a separate mapper task on each of the given nodes. Each
 * mapper task has one source and <I>NT</I> mappers (default: one mapper). The
 * source reads the given web log file on the node where the mapper task is
 * running.
 * <P>
 * The program counts the number of web requests that occurred during each
 * five-minute period during the day. On each web log line, the hour and minute
 * appear after the first <TT>":"</TT> in the format <TT>"hh:mm"</TT>. The
 * program prints a table of number of web requests versus time of day. The
 * program also generates a plot of the data and stores the plot in the given
 * plot file. The plot can be examined with the {@linkplain View View} program.
 *
 * @author  Alan Kaminsky
 * @version 30-Oct-2014
 */
public class WebLog04
	extends PjmrJob<TextId,String,Integer,LongVbl>
	{

	/**
	 * PJMR job main program.
	 *
	 * @param  args  Command line arguments.
	 */
	public void main
		(String[] args)
		{
		if (args.length < 3) usage();
		int NT = Math.max (threads(), 1);
		System.out.printf
			("$ java pj2 threads=%d edu.rit.pjmr.example.WebLog04", NT);
		for (String arg : args)
			System.out.printf (" %s", arg);
		System.out.println();
		System.out.printf ("%s%n", new Date());
		System.out.flush();

		for (int i = 2; i < args.length; ++ i)
			mapperTask (args[i])
				.source (new TextFileSource (args[1]))
				.mapper (NT, MyMapper.class);

		reducerTask()
			.runInJobProcess()
			.customizer (MyCustomizer.class)
			.reducer (MyReducer.class, args[0]);

		startJob();
		}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java pj2 [threads=<NT>] edu.rit.pjmr.example.WebLog04 <plotfile> <logfile> <node> [<node> ...]");
		throw new IllegalArgumentException();
		}

	/**
	 * Mapper class.
	 */
	private static class MyMapper
		extends Mapper<TextId,String,Integer,LongVbl>
		{
		private static final LongVbl ONE = new LongVbl.Sum (1L);

		public void map
			(TextId inKey,   // Line number
			 String inValue, // Line from file
			 Combiner<Integer,LongVbl> combiner)
			{
			int n = inValue.length();
			int i = inValue.indexOf (':');
			if (i != -1)
				{
				int h = Integer.parseInt (inValue.substring (i+1, i+3));
				int m = Integer.parseInt (inValue.substring (i+4, i+6));
				m = m/5;
				int minute = h*60 + m*5;
				combiner.add (minute, ONE);
				}
			}
		}

	/**
	 * Reducer task customizer class.
	 */
	private static class MyCustomizer
		extends Customizer<Integer,LongVbl>
		{
		public boolean comesBefore
			(Integer key_1, LongVbl value_1, // Minute -> requests
			 Integer key_2, LongVbl value_2)
			{
			return key_1.compareTo (key_2) < 0;
			}
		}

	/**
	 * Reducer class.
	 */
	private static class MyReducer
		extends Reducer<Integer,LongVbl>
		{
		private String plotfile;
		private ListXYSeries data;

		public void start
			(String[] args)
			{
			plotfile = args[0];
			data = new ListXYSeries();
			}

		public void reduce
			(Integer key,   // Minute
			 LongVbl value) // Number of requests
			{
			System.out.printf ("%02d:%02d\t%d%n", key/60, key%60, value.item);
			System.out.flush();
			data.add (key/60.0, value.item);
			}

		public void finish()
			{
			Plot plot = new Plot()
				.plotTitle ("Requests vs. Time of Day")
				.xAxisTitle ("Hour")
				.xAxisStart (0)
				.xAxisEnd (24)
				.xAxisMajorDivisions (12)
				.yAxisTitle ("Requests")
				.seriesStroke (null)
				.xySeries (data);
			try
				{
				Plot.write (plot, plotfile);
				}
			catch (IOException exc)
				{
				exc.printStackTrace (System.err);
				}
			}
		}

	}
