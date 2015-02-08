//******************************************************************************
//
// File:    LaunchOutOfResourcesCudaException.java
// Package: edu.rit.gpu
// Unit:    Class edu.rit.gpu.LaunchOutOfResourcesCudaException
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
 * Class LaunchOutOfResourcesCudaException is thrown if a
 * CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES error occurred.
 *
 * @author  Alan Kaminsky
 * @version 18-Feb-2014
 */
public class LaunchOutOfResourcesCudaException
	extends CudaException
	{

	/**
	 * Construct a new launch out of resources CUDA exception.
	 */
	public LaunchOutOfResourcesCudaException()
		{
		super();
		}

	/**
	 * Construct a new launch out of resources CUDA exception with the given
	 * detail message.
	 *
	 * @param  msg  Detail message.
	 */
	public LaunchOutOfResourcesCudaException
		(String msg)
		{
		super (msg);
		}

	/**
	 * Construct a new launch out of resources CUDA exception with the given
	 * chained exception.
	 *
	 * @param  exc  Chained exception.
	 */
	public LaunchOutOfResourcesCudaException
		(Throwable exc)
		{
		super (exc);
		}

	/**
	 * Construct a new launch out of resources CUDA exception with the given
	 * detail message and chained exception.
	 *
	 * @param  msg  Detail message.
	 * @param  exc  Chained exception.
	 */
	public LaunchOutOfResourcesCudaException
		(String msg,
		 Throwable exc)
		{
		super (msg, exc);
		}

	}
