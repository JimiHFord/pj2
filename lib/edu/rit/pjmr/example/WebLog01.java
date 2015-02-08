//******************************************************************************
//
// File:    WebLog01.java
// Package: edu.rit.pjmr.example
// Unit:    Class edu.rit.pjmr.example.WebLog01
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

import edu.rit.pj2.vbl.LongVbl;
import edu.rit.pjmr.Combiner;
import edu.rit.pjmr.Customizer;
import edu.rit.pjmr.Mapper;
import edu.rit.pjmr.PjmrJob;
import edu.rit.pjmr.Reducer;
import edu.rit.pjmr.TextFileSource;
import edu.rit.pjmr.TextId;
import java.util.Date;
import java.util.Scanner;

/**
 * Class WebLog01 is the main program for a PJMR map-reduce job that analyzes
 * web server log files.
 * <P>
 * Usage: <TT>java pj2 [threads=<I>NT</I>] edu.rit.pjmr.example.WebLog01
 * <I>file</I> <I>node</I> [<I>node</I> ...]</TT>
 * <P>
 * The program runs a separate mapper task on each of the given nodes. Each
 * mapper task has one source and <I>NT</I> mappers (default: one mapper). The
 * source reads the given web log file on the node where the mapper task is
 * running.
 * <P>
 * The program lists the unique IP addresses that made requests of the web
 * server, along with the number of requests. The IP address is the first item
 * on each line of the web log file. The IP addresses are printed in descending
 * order of number of requests. For an equal number of requests, the IP
 * addresses are printed in ascending order.
 *
 * @author  Alan Kaminsky
 * @version 30-Oct-2014
 */
public class WebLog01
	extends PjmrJob<TextId,String,IPAddress,LongVbl>
	{

	/**
	 * PJMR job main program.
	 *
	 * @param  args  Command line arguments.
	 */
	public void main
		(String[] args)
		{
		if (args.length < 2) usage();
		int NT = Math.max (threads(), 1);
		System.out.printf
			("$ java pj2 threads=%d edu.rit.pjmr.example.WebLog01", NT);
		for (String arg : args)
			System.out.printf (" %s", arg);
		System.out.println();
		System.out.printf ("%s%n", new Date());
		System.out.flush();

		for (int i = 1; i < args.length; ++ i)
			mapperTask (args[i])
				.source (new TextFileSource (args[0]))
				.mapper (NT, MyMapper.class);

		reducerTask()
			.customizer (MyCustomizer.class)
			.reducer (MyReducer.class);

		startJob();
		}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java pj2 [threads=<NT>] edu.rit.pjmr.example.WebLog01 <file> <node> [<node> ...]");
		throw new IllegalArgumentException();
		}

	/**
	 * Mapper class.
	 */
	private static class MyMapper
		extends Mapper<TextId,String,IPAddress,LongVbl>
		{
		private static final LongVbl ONE = new LongVbl.Sum (1L);

		public void map
			(TextId inKey,   // Line number
			 String inValue, // Line from file
			 Combiner<IPAddress,LongVbl> combiner)
			{
			Scanner scanner = new Scanner (inValue);
			if (scanner.hasNext())
				{
				try
					{
					IPAddress ipaddr = new IPAddress (scanner.next());
					combiner.add (ipaddr, ONE);
					}
				catch (Throwable exc)
					{
					}
				}
			}
		}

	/**
	 * Reducer task customizer class.
	 */
	private static class MyCustomizer
		extends Customizer<IPAddress,LongVbl>
		{
		public boolean comesBefore
			(IPAddress key_1, LongVbl value_1, // IP address -> requests
			 IPAddress key_2, LongVbl value_2)
			{
			if (value_1.item > value_2.item)
				return true;
			else if (value_1.item < value_2.item)
				return false;
			else
				return key_1.compareTo (key_2) < 0;
			}
		}

	/**
	 * Reducer class.
	 */
	private static class MyReducer
		extends Reducer<IPAddress,LongVbl>
		{
		public void reduce
			(IPAddress key, // IP address
			 LongVbl value) // Number of requests
			{
			System.out.printf ("%s\t%s%n", value, key);
			System.out.flush();
			}
		}

	}
