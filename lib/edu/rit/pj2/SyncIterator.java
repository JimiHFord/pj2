//******************************************************************************
//
// File:    SyncIterator.java
// Package: edu.rit.pj2
// Unit:    Class edu.rit.pj2.SyncIterator
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

package edu.rit.pj2;

import java.util.Iterator;

/**
 * Class SyncIterator provides an iterator with multiple thread safe methods.
 *
 * @param  <E>  Iterator element data type.
 *
 * @author  Alan Kaminsky
 * @version 21-May-2013
 */
class SyncIterator<E>
	{

// Hidden data members.

	private Iterator<E> iter;

// Exported constructors.

	/**
	 * Construct a new synchronized iterator on top of the given iterator.
	 *
	 * @param  iter  Iterator.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>iter</TT> is null.
	 */
	public SyncIterator
		(Iterator<E> iter)
		{
		if (iter == null)
			throw new NullPointerException
				("SyncIterator(): iter is null");
		this.iter = iter;
		}

// Exported operations.

	/**
	 * Get the next element from this iterator. Assumes that the underlying
	 * iterator will not return a null element.
	 *
	 * @return  Next element, or null if no more elements.
	 */
	public synchronized E next()
		{
		return iter.hasNext() ? iter.next() : null;
		}

	}
