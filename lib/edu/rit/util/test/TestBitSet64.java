//******************************************************************************
//
// File:    TestBitSet64.java
// Package: edu.rit.util.test
// Unit:    Class edu.rit.util.test.TestBitSet64
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

package edu.rit.util.test;

import edu.rit.util.BitSet64;
import edu.rit.util.IntAction;
import edu.rit.util.IntList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class TestBitSet64 is a unit test main program for class {@linkplain
 * edu.rit.util.BitSet64 BitSet64}. The program creates two bitsets named
 * <TT>a</TT> and <TT>b</TT>. The program reads commands from the standard
 * input. Each command is of the form <TT>"a.<I>method</I>(<I>args</I>)"</TT> or
 * <TT>"b.<I>method</I>(<I>args</I>)"</TT>. There is no whitespace in the
 * command. The program calls the given method with the given arguments on the
 * given bitset, then prints the state of both bitsets. The command <TT>"q"</TT>
 * quits the program.
 * <P>
 * Usage: <TT>java edu.rit.util.test.TestBitSet64</TT>
 *
 * @author  Alan Kaminsky
 * @version 07-Jan-2015
 */
public class TestBitSet64
	{

// Prevent construction.

	private TestBitSet64()
		{
		}

// Unit test main program.

	/**
	 * Unit test main program.
	 */
	public static void main
		(String[] args)
		{
		BitSet64 a = new BitSet64();
		BitSet64 b = new BitSet64();
		Scanner s = new Scanner (System.in);

		for (;;)
			try
				{
				print ("a", a);
				print ("b", b);

				System.out.print ("? ");
				System.out.flush();
				String cmd = s.nextLine();

				if (cmd.equals ("q"))
					break;

				BitSet64 set = null;
				if (cmd.charAt (0) == 'a')
					set = a;
				else if (cmd.charAt (0) == 'b')
					set = b;
				else
					{
					huh();
					continue;
					}

				if (cmd.substring(2).startsWith ("isEmpty"))
					System.out.printf ("%b%n", set.isEmpty());
				else if (cmd.substring(2).startsWith ("clear"))
					set.clear();
				else if (cmd.equals ("a.copy(b)"))
					a.copy (a);
				else if (cmd.equals ("b.copy(a)"))
					b.copy (a);
				else if (cmd.substring(2).startsWith ("size"))
					System.out.printf ("%d%n", set.size());
				else if (cmd.substring(2).startsWith ("contains"))
					{
					Matcher m = INTEGER.matcher (cmd);
					if (m.find())
						System.out.printf ("%b%n", set.contains
							(Integer.parseInt (m.group())));
					else
						huh();
					}
				else if (cmd.substring(2).startsWith ("add"))
					{
					IntList elems = new IntList();
					Matcher m = INTEGER.matcher (cmd);
					while (m.find())
						elems.addLast (Integer.parseInt (m.group()));
					switch (elems.size())
						{
						case 1:
							set.add (elems.get(0));
							break;
						case 2:
							set.add (elems.get(0), elems.get(1));
							break;
						default:
							huh();
							break;
						}
					}
				else if (cmd.substring(2).startsWith ("remove"))
					{
					IntList elems = new IntList();
					Matcher m = INTEGER.matcher (cmd);
					while (m.find())
						elems.addLast (Integer.parseInt (m.group()));
					switch (elems.size())
						{
						case 1:
							set.remove (elems.get(0));
							break;
						case 2:
							set.remove (elems.get(0), elems.get(1));
							break;
						default:
							huh();
							break;
						}
					}
				else if (cmd.substring(2).startsWith ("flip"))
					{
					IntList elems = new IntList();
					Matcher m = INTEGER.matcher (cmd);
					while (m.find())
						elems.addLast (Integer.parseInt (m.group()));
					switch (elems.size())
						{
						case 1:
							set.flip (elems.get(0));
							break;
						case 2:
							set.flip (elems.get(0), elems.get(1));
							break;
						default:
							huh();
							break;
						}
					}
				else if (cmd.equals ("a.isSubsetOf(b)"))
					System.out.printf ("%b%n", a.isSubsetOf (b));
				else if (cmd.equals ("b.isSubsetOf(a)"))
					System.out.printf ("%b%n", b.isSubsetOf (a));
				else if (cmd.equals ("a.union(b)"))
					a.union (b);
				else if (cmd.equals ("b.union(a)"))
					b.union (a);
				else if (cmd.equals ("a.intersection(b)"))
					a.intersection (b);
				else if (cmd.equals ("b.intersection(a)"))
					b.intersection (a);
				else if (cmd.equals ("a.difference(b)"))
					a.difference (b);
				else if (cmd.equals ("b.difference(a)"))
					b.difference (a);
				else if (cmd.equals ("a.symmetricDifference(b)"))
					a.symmetricDifference (b);
				else if (cmd.equals ("b.symmetricDifference(a)"))
					b.symmetricDifference (a);
				else
					huh();
				}
			catch (NumberFormatException exc)
				{
				huh();
				}
		}

	private static final Pattern INTEGER = Pattern.compile ("\\-?\\d+");

	private static void huh()
		{
		System.out.printf ("Huh?%n");
		}

	private static void print
		(String label,
		 BitSet64 set)
		{
		System.out.printf ("%s = {", label);
		set.forEachItemDo (new IntAction()
			{
			boolean first = true;
			public void run (int elem)
				{
				System.out.printf ("%s%d", first ? "" : ",", elem);
				first = false;
				}
			});
		System.out.printf ("}%n");
		}

	}
