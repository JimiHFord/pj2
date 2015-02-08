//******************************************************************************
//
// File:    WebLog03.java
// Package: edu.rit.pjmr.example
// Unit:    Class edu.rit.pjmr.example.WebLog03
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
 * Class WebLog03 is the main program for a PJMR map-reduce job that analyzes
 * web server log files.
 * <P>
 * Usage: <TT>java pj2 [threads=<I>NT</I>] edu.rit.pjmr.example.WebLog03
 * <I>account</I> <I>file</I> <I>node</I> [<I>node</I> ...]</TT>
 * <P>
 * The program runs a separate mapper task on each of the given nodes. Each
 * mapper task has one source and <I>NT</I> mappers (default: one mapper). The
 * source reads the given web log file on the node where the mapper task is
 * running.
 * <P>
 * The program lists the unique IP addresses that requested web pages for the
 * given user account, along with the number of requests from each IP address.
 * The IP address is the first item on each web log line. The user account is
 * found between the first <TT>"/~"</TT> and the next <TT>"/"</TT> or whitespace
 * on each web log line. The IP addresses are printed in descending order of
 * number of requests. For an equal number of requests, the IP addresses are
 * printed in ascending order.
 *
 * @author  Alan Kaminsky
 * @version 30-Oct-2014
 */
public class WebLog03
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
		if (args.length < 3) usage();
		int NT = Math.max (threads(), 1);
		System.out.printf
			("$ java pj2 threads=%d edu.rit.pjmr.example.WebLog03", NT);
		for (String arg : args)
			System.out.printf (" %s", arg);
		System.out.println();
		System.out.printf ("%s%n", new Date());
		System.out.flush();

		for (int i = 2; i < args.length; ++ i)
			mapperTask (args[i])
				.source (new TextFileSource (args[1]))
				.mapper (NT, MyMapper.class, args[0]);

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
		System.err.println ("Usage: java pj2 [threads=<NT>] edu.rit.pjmr.example.WebLog03 <file> <node> [<node> ...]");
		throw new IllegalArgumentException();
		}

	/**
	 * Mapper class.
	 */
	private static class MyMapper
		extends Mapper<TextId,String,IPAddress,LongVbl>
		{
		private static final LongVbl ONE = new LongVbl.Sum (1L);
		private String account;

		public void start
			(String[] args,
			 Combiner<IPAddress,LongVbl> combiner)
			{
			account = args[0];
			}

		public void map
			(TextId inKey,   // Line number
			 String inValue, // Line from file
			 Combiner<IPAddress,LongVbl> combiner)
			{
			int n = inValue.length();
			int i = inValue.indexOf ("/~");
			if (i != -1)
				{
				int j = i + 2;
				char c;
				while (j < n &&
					(c = inValue.charAt (j)) != '/' &&
					! Character.isWhitespace (c))
						++ j;
				if (inValue.substring (i + 2, j) .equals (account))
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
		long total;
		public void start (String[] args)
			{
			total = 0;
			}
		public void reduce
			(IPAddress key, // IP address
			 LongVbl value) // Number of requests
			{
			System.out.printf ("%s\t%s%n", value, key);
			System.out.flush();
			total += value.item;
			}
		public void finish()
			{
			System.out.printf ("%d\tTotal%n", total);
			System.out.flush();
			}
		}

	}
