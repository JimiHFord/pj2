//******************************************************************************
//
// File:    GpuIntMatrix.java
// Package: edu.rit.gpu
// Unit:    Class edu.rit.gpu.GpuIntMatrix
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

package edu.rit.gpu;

/**
 * Class GpuIntMatrix provides an integer matrix GPU variable. This is a
 * two-dimensional array of type <TT>int</TT> stored in the GPU's memory and
 * mirrored in the CPU's memory.
 * <P>
 * Class GpuIntMatrix supports mirroring all of the GPU's data matrix in the
 * CPU's memory, mirroring only a portion of the GPU's data matrix in the CPU's
 * memory, or mirroring none of the GPU's data matrix. Class GpuIntMatrix
 * provides operations for copying all or portions of the data matrix from the
 * CPU to the GPU or from the GPU to the CPU.
 * <P>
 * To use an integer matrix GPU variable:
 * <OL TYPE=1>
 * <P><LI>
 * Construct an instance of class GpuIntMatrix by calling the {@link
 * Gpu#getIntMatrix(int,int) getIntMatrix()} method on a {@linkplain Gpu Gpu}
 * object, specifying the number of rows and columns in the matrix and
 * (optionally) the number of rows and columns in the matrix portion mirrored in
 * the CPU.
 * <P><LI>
 * Set the {@link #item item} field's elements to the desired values. Call the
 * {@link #hostToDev() hostToDev()} method to copy the mirrored portion of the
 * matrix from CPU memory to GPU memory.
 * <P><LI>
 * Pass the GpuIntMatrix object as an argument of a GPU kernel function call. In
 * the GPU code, this becomes a pointer (type <TT>int**</TT> or <TT>unsigned
 * int**</TT>) to an array of <I>R</I> row pointers, where <I>R</I> is the
 * number of rows in the matrix; each row pointer points to an array of <I>C</I>
 * data elements, where <I>C</I> is the number of columns in the matrix. The GPU
 * code sets the matrix's elements to the desired values.
 * <P><LI>
 * Call the {@link #devToHost() devToHost()} method to copy the mirrored portion
 * of the matrix from GPU memory to CPU memory. Examine the values of the {@link
 * #item item} field's elements.
 * </OL>
 *
 * @author  Alan Kaminsky
 * @version 03-Apr-2014
 */
public class GpuIntMatrix
	extends GpuMatrix
	{

// Exported data members.

	/**
	 * The mirrored portion of the integer matrix in CPU memory.
	 */
	public final int[][] item;

// Hidden constructors.

	/**
	 * Construct a new dynamically allocated GPU integer matrix.
	 *
	 * @param  gpu          Gpu object.
	 * @param  rows         Number of rows in GPU memory.
	 * @param  cols         Number of columns in GPU memory.
	 * @param  cpurows      Number of rows mirrored in CPU memory.
	 * @param  cpucols      Number of columns mirrored in CPU memory.
	 *
	 * @exception  GpuException
	 *     (unchecked exception) Thrown if a GPU error occurred.
	 */
	GpuIntMatrix
		(Gpu gpu,
		 int rows,
		 int cols,
		 int cpurows,
		 int cpucols)
		{
		super (gpu, rows, cols, cpurows, cpucols, 4L);
		item = new int [cpurows] [cpucols];
		}

// Exported operations.

	/**
	 * Copy the given portion of this GPU matrix from the host CPU's memory to
	 * the GPU device's memory. <TT>rowlen</TT>&times;<TT>collen</TT> elements
	 * starting at indexes <TT>[srcrow][srccol]</TT> in the CPU matrix are
	 * copied to the GPU matrix starting at indexes <TT>[dstrow][dstcol]</TT>.
	 *
	 * @param  dstrow  GPU matrix starting row index.
	 * @param  dstcol  GPU matrix starting column index.
	 * @param  srcrow  CPU matrix starting row index.
	 * @param  srccol  CPU matrix starting column index.
	 * @param  rowlen  Number of rows to copy.
	 * @param  collen  Number of columns to copy.
	 *
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>dstrow</TT> &lt; 0,
	 *     <TT>dstcol</TT> &lt; 0, <TT>srcrow</TT> &lt; 0, <TT>srccol</TT> &lt;
	 *     0, <TT>rowlen</TT> &lt; 0, <TT>collen</TT> &lt; 0,
	 *     <TT>dstrow+rowlen</TT> &gt; <TT>rows()</TT>, <TT>dstcol+collen</TT>
	 *     &gt; <TT>cols()</TT>, <TT>srcrow+rowlen</TT> &gt; <TT>cpuRows()</TT>,
	 *     or <TT>srccol+collen</TT> &gt; <TT>cpuCols()</TT>.
	 * @exception  GpuException
	 *     (unchecked exception) Thrown if a GPU error occurred.
	 */
	public void hostToDev
		(int dstrow,
		 int dstcol,
		 int srcrow,
		 int srccol,
		 int rowlen,
		 int collen)
		{
//System.out.printf ("GpuIntMatrix.hostToDev()%n");
//System.out.printf ("\tdstrow = %d%n", dstrow);
//System.out.printf ("\tdstcol = %d%n", dstcol);
//System.out.printf ("\tsrcrow = %d%n", srcrow);
//System.out.printf ("\tsrccol = %d%n", srccol);
//System.out.printf ("\trowlen = %d%n", rowlen);
//System.out.printf ("\tcollen = %d%n", collen);
//for (int i = 0; i < cpurows; ++ i) for (int j = 0; j < cpucols; ++ j) System.out.printf ("\titem[%d][%d] = %d%n", i, j, item[i][j]);
		if (dstrow < 0 || dstcol < 0 ||
			srcrow < 0 || srccol < 0 ||
			rowlen < 0 || collen < 0 ||
			dstrow + rowlen > rows || dstcol + collen > cols ||
			srcrow + rowlen > cpurows || srccol + collen > cpucols)
				throw new IndexOutOfBoundsException();
		if (rowlen > 0 && collen > 0)
			{
			long dstrowptr = elem00ptr + dstrow*rowbytesize;
			for (int r = 0; r < rowlen; ++ r)
				{
//System.out.printf ("\tCuda.cuMemcpyHtoD(dstrowptr=%d,dstcol=%d,item[%d],srccol=%d,collen=%d)%n", dstrowptr, dstcol, srcrow+r, srccol, collen);
				Cuda.cuMemcpyHtoD (gpu.ctx, dstrowptr, dstcol,
					item[srcrow+r], srccol, collen);
				dstrowptr += rowbytesize;
				}
			}
		}

	/**
	 * Copy the given portion of this GPU matrix from the GPU device's memory to
	 * the host CPU's memory. <TT>rowlen</TT>&times;<TT>collen</TT> elements
	 * starting at indexes <TT>[srcrow][srccol]</TT> in the GPU matrix are
	 * copied to the CPU matrix starting at indexes <TT>[dstrow][dstcol]</TT>.
	 *
	 * @param  dstrow  CPU matrix starting row index.
	 * @param  dstcol  CPU matrix starting column index.
	 * @param  srcrow  GPU matrix starting row index.
	 * @param  srccol  GPU matrix starting column index.
	 * @param  rowlen  Number of rows to copy.
	 * @param  collen  Number of columns to copy.
	 *
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>dstrow</TT> &lt; 0,
	 *     <TT>dstcol</TT> &lt; 0, <TT>srcrow</TT> &lt; 0, <TT>srccol</TT> &lt;
	 *     0, <TT>rowlen</TT> &lt; 0, <TT>collen</TT> &lt; 0,
	 *     <TT>dstrow+rowlen</TT> &gt; <TT>cpuRows()</TT>,
	 *     <TT>dstcol+collen</TT> &gt; <TT>cpuCols()</TT>,
	 *     <TT>srcrow+rowlen</TT> &gt; <TT>rows()</TT>, or
	 *     <TT>srccol+collen</TT> &gt; <TT>cols()</TT>.
	 * @exception  GpuException
	 *     (unchecked exception) Thrown if a GPU error occurred.
	 */
	public void devToHost
		(int dstrow,
		 int dstcol,
		 int srcrow,
		 int srccol,
		 int rowlen,
		 int collen)
		{
		if (dstrow < 0 || dstcol < 0 ||
			srcrow < 0 || srccol < 0 ||
			rowlen < 0 || collen < 0 ||
			dstrow + rowlen > cpurows || dstcol + collen > cpucols ||
			srcrow + rowlen > rows || srccol + collen > cols)
				throw new IndexOutOfBoundsException();
		if (rowlen > 0 && collen > 0)
			{
			long srcrowptr = elem00ptr + srcrow*rowbytesize;
			for (int r = 0; r < rowlen; ++ r)
				{
				Cuda.cuMemcpyDtoH (gpu.ctx, item[dstrow+r], dstcol,
					srcrowptr, srccol, collen);
				srcrowptr += rowbytesize;
				}
			}
		}

// Unit test main program.

//	/**
//	 * Unit test main program.
//	 */
//	public static void main
//		(String[] args)
//		{
//		if (args.length != 4) usage();
//		int rows = Integer.parseInt (args[0]);
//		int cols = Integer.parseInt (args[1]);
//		int cpurows = Integer.parseInt (args[2]);
//		int cpucols = Integer.parseInt (args[3]);
//
//		Gpu gpu = Gpu.gpu();
//		GpuIntMatrix matrix = gpu.getIntMatrix (rows, cols, cpurows, cpucols);
//
//		for (int r = 0; r < cpurows; ++ r)
//			for (int c = 0; c < cpucols; ++ c)
//				matrix.item[r][c] = 1000*r + c + 1001;
//		printMatrix (matrix, "After initialization");
//
//		matrix.devToHost();
//		printMatrix (matrix, "After download");
//
//		for (int r = 0; r < cpurows; ++ r)
//			for (int c = 0; c < cpucols; ++ c)
//				matrix.item[r][c] = 1000*r + c + 1001;
//		printMatrix (matrix, "After reinitialization");
//
//		matrix.hostToDev();
//		printMatrix (matrix, "After upload");
//
//		for (int r = 0; r < cpurows; ++ r)
//			for (int c = 0; c < cpucols; ++ c)
//				matrix.item[r][c] = 0;
//		printMatrix (matrix, "After clearing");
//
//		matrix.devToHost();
//		printMatrix (matrix, "After download");
//		}
//
//	private static void printMatrix
//		(GpuIntMatrix matrix,
//		 String msg)
//		{
//		System.out.printf ("%s:%n", msg);
//		for (int r = 0; r < matrix.item.length; ++ r)
//			for (int c = 0; c < matrix.item[r].length; ++ c)
//				System.out.printf ("\t[%d][%d] = %d%n",
//					r, c, matrix.item[r][c]);
//		}
//
//	private static void usage()
//		{
//		System.err.println ("Usage: java edu.rit.gpu.GpuIntMatrix <rows> <cols> <cpurows> <cpucols>");
//		System.exit (1);
//		}

	}
