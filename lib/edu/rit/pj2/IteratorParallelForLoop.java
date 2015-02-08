//******************************************************************************
//
// File:    IteratorParallelForLoop.java
// Package: edu.rit.pj2
// Unit:    Class edu.rit.pj2.IteratorParallelForLoop
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

package edu.rit.pj2;

import java.util.Iterator;

/**
 * Class IteratorParallelForLoop provides a work sharing parallel for loop
 * executed by multiple threads, looping over the elements in an iterator or an
 * iterable collection. An iterator parallel for loop is constructed by the
 * {@link Task#parallelFor(java.util.Iterator) parallelFor(Iterator)} or {@link
 * Task#parallelFor(Iterable) parallelFor(Iterable)} method of class {@linkplain
 * Task}.
 * <P>
 * <B>Programming pattern.</B>
 * To execute a parallel for loop over the elements in an iterator or an
 * iterable collection in the {@link Task#main(String[]) main()} method of a
 * {@linkplain Task}, follow this pattern:
 * <PRE>
 * public class MyTask extends Task
 *     {
 *     public void main (String[] args)
 *         {
 *         parallelFor (<I>iter</I>) .exec (new ObjectLoop&lt;E&gt;()
 *             {
 *             // <I>Thread-local variable declarations (optional)</I>
 *             public void start()
 *                 {
 *                 // <I>One-time thread-local initialization (optional method)</I>
 *                 }
 *             public void run (E obj)
 *                 {
 *                 // <I>Loop body code for element obj (required method)</I>
 *                 }
 *             public void finish()
 *                 {
 *                 // <I>One-time thread-local finalization (optional method)</I>
 *                 }
 *             });
 *         }
 *     }</PRE>
 * <P>
 * The <I>iter</I> is either an {@linkplain java.util.Iterator Iterator} or an
 * {@linkplain java.lang.Iterable Iterable} collection. <TT>E</TT> is the data
 * type of the iterator's or collection's elements. The iterator must return
 * only non-null elements. The collection must contain only non-null elements.
 * <P>
 * <B>Parallel thread team.</B>
 * The parallel for loop is executed by a team of threads. The number of threads
 * is given by the <TT>threads</TT> property of the enclosing task (see the
 * {@link Task#threads() threads()} method of class {@linkplain Task}). The
 * default is one thread for each core of the machine on which the program is
 * running. The default can be overridden as follows:
 * <PRE>
 *     parallelFor (<I>iter</I>) .threads (<I>threads</I>) .exec (new ObjectLoop&lt;E&gt;() ...</PRE>
 * <P>
 * <B>Parallel loop body.</B>
 * The threads execute the methods in the inner {@linkplain ObjectLoop} class.
 * For further information about how the parallel for loop executes, see class
 * {@linkplain ObjectLoop}.
 *
 * @param  <E>  Data type of the iterator's or iterable collection's element.
 *
 * @see  ParallelStatement
 * @see  ParallelForLoop
 * @see  ObjectLoop
 *
 * @author  Alan Kaminsky
 * @version 22-Mar-2014
 */
public class IteratorParallelForLoop<E>
	extends ParallelForLoop
	{

// Hidden data members.

	private SyncIterator<E> iter;
	private ObjectLoop<E> loop;

// Hidden constructors.

	/**
	 * Construct a new integer parallel for loop.
	 *
	 * @param  task  Task in which the parallel for loop is executing.
	 * @param  iter  Iterator.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>iter</TT> is null.
	 */
	IteratorParallelForLoop
		(Task task,
		 Iterator<E> iter)
		{
		super (task);
		this.iter = new SyncIterator<E> (iter);
		}

// Exported operations.

	/**
	 * Set this parallel for loop's <TT>threads</TT> property. The
	 * <TT>threads</TT> property specifies the number of threads that will
	 * execute this parallel for loop. The default is the <TT>threads</TT>
	 * property of the enclosing task. For further information, see the {@link
	 * Task#threads(int) threads()} method of class {@linkplain Task}.
	 *
	 * @param  threads  Number of threads (&ge; 1), {@link
	 *                  Task#THREADS_EQUALS_CORES}, or {@link
	 *                  Task#DEFAULT_THREADS}.
	 *
	 * @return  This parallel for loop object.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>threads</TT> is illegal.
	 */
	public IteratorParallelForLoop<E> threads
		(int threads)
		{
		properties.threads (threads);
		return this;
		}

	/**
	 * Execute this parallel for loop with the loop body specified by the given
	 * {@linkplain ObjectLoop} object.
	 *
	 * @param  loop  Loop object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>loop</TT> is null.
	 */
	public void exec
		(ObjectLoop<E> loop)
		{
		threads = properties.actualThreads();
		this.loop = loop;
		stop = false;
		Team.execute (threads, this);
		}

// Hidden operations.

	/**
	 * Execute this parallel statement.
	 *
	 * @param  rank          Rank of the team thread.
	 * @param  reductionMap  Reduction map of the team thread.
	 *
	 * @exception  Exception
	 *     The <TT>run()</TT> method may throw any exception.
	 */
	void run
		(int rank,
		 ReductionMap reductionMap)
		throws Exception
		{
		// Thread 0 operates on the original loop object, the other threads
		// operate on their own copies of the loop object.
		ObjectLoop<E> loop =
			rank == 0 ? this.loop : (ObjectLoop<E>)(this.loop.clone());
		loop.parallelForLoop = this;
		loop.rank = rank;
		loop.reductionMap = reductionMap;

		// Perform one-time initialization.
		loop.start();

		// Execute iterations.
		E obj = null;
		while (! stop && (obj = iter.next()) != null)
			loop.run (obj);

		// Perform one-time finalization.
		loop.finish();

		loop.parallelForLoop = null;
		loop.rank = -1;
		loop.reductionMap = null;
		}

	}
