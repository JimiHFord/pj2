//******************************************************************************
//
// File:    Logger.java
// Package: edu.rit.util
// Unit:    Class edu.rit.util.Logger
//
// This Java source file is copyright (C) 2013 by Alan Kaminsky. All rights
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

package edu.rit.util;

/**
 * Interface Logger specifies the interface for an object that logs messages.
 *
 * @author  Alan Kaminsky
 * @version 12-Jun-2013
 */
public interface Logger
	{

// Exported operations.

	/**
	 * Set the prefix. The string <TT>"<I>prefix</I>:&nbsp;"</TT> will be
	 * included at the beginning of each log message.
	 *
	 * @param  prefix  Prefix, or null for no prefix.
	 */
	public void prefix
		(String prefix);

	/**
	 * Log the given message.
	 *
	 * @param  msg  Message.
	 */
	public void log
		(String msg);

	/**
	 * Log the given exception.
	 *
	 * @param  exc  Exception.
	 */
	public void log
		(Throwable exc);

	/**
	 * Log the given message and exception.
	 *
	 * @param  msg  Message.
	 * @param  exc  Exception.
	 */
	public void log
		(String msg,
		 Throwable exc);

	/**
	 * Log the given date and message.
	 *
	 * @param  date  Date and time in milliseconds since midnight 01-Jan-1970
	 *               UTC.
	 * @param  msg   Message.
	 */
	public void log
		(long date,
		 String msg);

	/**
	 * Log the given date and exception.
	 *
	 * @param  date  Date and time in milliseconds since midnight 01-Jan-1970
	 *               UTC.
	 * @param  exc   Exception.
	 */
	public void log
		(long date,
		 Throwable exc);

	/**
	 * Log the given date, message, and exception.
	 *
	 * @param  date  Date and time in milliseconds since midnight 01-Jan-1970
	 *               UTC.
	 * @param  msg   Message.
	 * @param  exc   Exception.
	 */
	public void log
		(long date,
		 String msg,
		 Throwable exc);

	}
