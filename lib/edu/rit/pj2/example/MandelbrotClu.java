//******************************************************************************
//
// File:    MandelbrotClu.java
// Package: edu.rit.pj2.example
// Unit:    Class edu.rit.pj2.example.MandelbrotClu
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

package edu.rit.pj2.example;

import edu.rit.image.Color;
import edu.rit.image.ColorArray;
import edu.rit.image.ColorImageQueue;
import edu.rit.image.ColorPngWriter;
import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Job;
import edu.rit.pj2.Loop;
import edu.rit.pj2.Schedule;
import edu.rit.pj2.Section;
import edu.rit.pj2.Task;
import edu.rit.pj2.Tuple;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class MandelbrotClu is a cluster parallel program that calculates the
 * Mandelbrot Set.
 * <P>
 * Usage: <TT>java pj2 [workers=<I>K</I>] edu.rit.pj2.example.MandelbrotClu
 * <I>width</I> <I>height</I> <I>xcenter</I> <I>ycenter</I> <I>resolution</I>
 * <I>maxiter</I> <I>gamma</I> <I>filename</I></TT>
 * <BR><I>K</I> = Number of worker tasks (default 1)
 * <BR><I>width</I> = Image width (pixels)
 * <BR><I>height</I> = Image height (pixels)
 * <BR><I>xcenter</I> = X coordinate of center point
 * <BR><I>ycenter</I> = Y coordinate of center point
 * <BR><I>resolution</I> = Pixels per unit
 * <BR><I>maxiter</I> = Maximum number of iterations
 * <BR><I>gamma</I> = Used to calculate pixel hues
 * <BR><I>filename</I> = PNG image file name
 * <P>
 * The program considers a rectangular region of the complex plane centered at
 * (<I>xcenter,ycenter</I>) of <I>width</I> pixels by <I>height</I> pixels,
 * where the distance between adjacent pixels is 1/<I>resolution</I>. The
 * program takes each pixel's location as a complex number <I>c</I> and performs
 * the following iteration:
 * <P>
 * <I>z</I><SUB>0</SUB> = 0
 * <BR><I>z</I><SUB><I>i</I>+1</SUB> = <I>z</I><SUB><I>i</I></SUB><SUP>2</SUP> + <I>c</I>
 * <P>
 * until <I>z</I><SUB><I>i</I></SUB>'s magnitude becomes greater than or equal
 * to 2, or <I>i</I> reaches a limit of <I>maxiter</I>. The complex numbers
 * <I>c</I> where <I>i</I> reaches a limit of <I>maxiter</I> are considered to
 * be in the Mandelbrot Set. (Actually, a number is in the Mandelbrot Set only
 * if the iteration would continue forever without <I>z</I><SUB><I>i</I></SUB>
 * becoming infinite; the foregoing is just an approximation.) The program
 * creates an image with the pixels corresponding to the complex numbers
 * <I>c</I> and the pixels' colors corresponding to the value of <I>i</I>
 * achieved by the iteration. Following the traditional practice, points in the
 * Mandelbrot set are black, and the other points are brightly colored in a
 * range of colors depending on <I>i</I>. The exact hue of each pixel is
 * (<I>i</I>/<I>maxiter</I>)<SUP><I>gamma</I></SUP>. The image is stored in a
 * PNG file specified on the command line.
 * <P>
 * Here is an example command and the resulting image:
 * <P>
 * <TT>$ java pj2 edu.rit.pj2.example.MandelbrotClu 400 400 -0.75 0 150 1000 0.4 ms400a.png</TT>
 * <P>
 * <CENTER><IMG SRC="doc-files/ms400a.png"></CENTER>
 *
 * @author  Alan Kaminsky
 * @version 04-Jul-2014
 */
public class MandelbrotClu
	extends Job
	{

	/**
	 * Job main program.
	 */
	public void main
		(String[] args)
		{
		// Parse command line arguments.
		if (args.length != 8) usage();
		int width = Integer.parseInt (args[0]);
		int height = Integer.parseInt (args[1]);
		double xcenter = Double.parseDouble (args[2]);
		double ycenter = Double.parseDouble (args[3]);
		double resolution = Double.parseDouble (args[4]);
		int maxiter = Integer.parseInt (args[5]);
		double gamma = Double.parseDouble (args[6]);
		File filename = new File (args[7]);

		// Set up task group with K worker tasks. Partition rows among workers.
		masterSchedule (proportional);
		masterChunk (10);
		masterFor (0, height - 1, WorkerTask.class) .args (args);

		// Set up PNG file writing task.
		rule() .task (OutputTask.class) .args (args) .runInJobProcess();
		}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java pj2 [workers=<K>] edu.rit.pj2.example.MandelbrotClu <width> <height> <xcenter> <ycenter> <resolution> <maxiter> <gamma> <filename>");
		System.err.println ("<K> = Number of worker tasks (default 1)");
		System.err.println ("<width> = Image width (pixels)");
		System.err.println ("<height> = Image height (pixels)");
		System.err.println ("<xcenter> = X coordinate of center point");
		System.err.println ("<ycenter> = Y coordinate of center point");
		System.err.println ("<resolution> = Pixels per unit");
		System.err.println ("<maxiter> = Maximum number of iterations");
		System.err.println ("<gamma> = Used to calculate pixel hues");
		System.err.println ("<filename> = PNG image file name");
		throw new IllegalArgumentException();
		}

	/**
	 * Tuple for sending results from worker tasks to output task.
	 */
	private static class OutputTuple
		extends Tuple
		{
		public int row;               // Row index
		public ColorArray pixelData;  // Row's pixel data

		public OutputTuple()
			{
			}

		public OutputTuple
			(int row,
			 ColorArray pixelData)
			{
			this.row = row;
			this.pixelData = pixelData;
			}

		public void writeOut (OutStream out) throws IOException
			{
			out.writeUnsignedInt (row);
			out.writeObject (pixelData);
			}

		public void readIn (InStream in) throws IOException
			{
			row = in.readUnsignedInt();
			pixelData = (ColorArray) in.readObject();
			}
		}

	/**
	 * Worker task class.
	 */
	private static class WorkerTask
		extends Task
		{
		// Command line arguments.
		int width;
		int height;
		double xcenter;
		double ycenter;
		double resolution;
		int maxiter;
		double gamma;

		// Initial pixel offsets from center.
		int xoffset;
		int yoffset;

		// Table of hues.
		Color[] huetable;

		// Worker task main program.
		public void main
			(String[] args)
			throws Exception
			{
			// Parse command line arguments.
			width = Integer.parseInt (args[0]);
			height = Integer.parseInt (args[1]);
			xcenter = Double.parseDouble (args[2]);
			ycenter = Double.parseDouble (args[3]);
			resolution = Double.parseDouble (args[4]);
			maxiter = Integer.parseInt (args[5]);
			gamma = Double.parseDouble (args[6]);

			// Initial pixel offsets from center.
			xoffset = -(width - 1) / 2;
			yoffset = (height - 1) / 2;

			// Create table of hues for different iteration counts.
			huetable = new Color [maxiter + 2];
			for (int i = 1; i <= maxiter; ++ i)
				huetable[i] = new Color().hsb
					(/*hue*/ (float) Math.pow ((double)(i - 1)/maxiter, gamma),
					 /*sat*/ 1.0f,
					 /*bri*/ 1.0f);
			huetable[maxiter + 1] = new Color().hsb (1.0f, 1.0f, 0.0f);

			// Compute all rows and columns.
			workerFor() .schedule (dynamic) .exec (new Loop()
				{
				ColorArray pixelData;
				
				public void start()
					{
					pixelData = new ColorArray (width);
					}

				public void run (int r) throws Exception
					{
					double y = ycenter + (yoffset - r) / resolution;

					for (int c = 0; c < width; ++ c)
						{
						double x = xcenter + (xoffset + c) / resolution;

						// Iterate until convergence.
						int i = 0;
						double aold = 0.0;
						double bold = 0.0;
						double a = 0.0;
						double b = 0.0;
						double zmagsqr = 0.0;
						while (i <= maxiter && zmagsqr <= 4.0)
							{
							++ i;
							a = aold*aold - bold*bold + x;
							b = 2.0*aold*bold + y;
							zmagsqr = a*a + b*b;
							aold = a;
							bold = b;
							}

						// Record number of iterations for pixel.
						pixelData.color (c, huetable[i]);
						}

					putTuple (new OutputTuple (r, pixelData));
					}
				});
			}
		}

	/**
	 * Output PNG file writing task.
	 */
	private static class OutputTask
		extends Task
		{
		// Command line arguments.
		int width;
		int height;
		File filename;

		// For writing PNG image file.
		ColorPngWriter writer;
		ColorImageQueue imageQueue;

		// Task main program.
		public void main
			(String[] args)
			throws Exception
			{
			// Parse command line arguments.
			width = Integer.parseInt (args[0]);
			height = Integer.parseInt (args[1]);
			filename = new File (args[7]);

			// Set up for writing PNG image file.
			writer = new ColorPngWriter (height, width,
				new BufferedOutputStream (new FileOutputStream (filename)));
			filename.setReadable (true, false);
			filename.setWritable (true, false);
			imageQueue = writer.getImageQueue();

			// Overlapped pixel data gathering and file writing.
			parallelDo (new Section()
				{
				// Pixel data gathering section.
				public void run() throws Exception
					{
					OutputTuple template = new OutputTuple();
					OutputTuple tuple;
					for (int i = 0; i < height; ++ i)
						{
						tuple = (OutputTuple) takeTuple (template);
						imageQueue.put (tuple.row, tuple.pixelData);
						}
					}
				},

			new Section()
				{
				// File writing section.
				public void run() throws Exception
					{
					writer.write();
					}
				});
			}
		}

	}
