//******************************************************************************
//
// File:    BitSetVbl.java
// Package: edu.rit.pj2.vbl
// Unit:    Class edu.rit.pj2.vbl.BitSetVbl
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

package edu.rit.pj2.vbl;

import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Vbl;
import edu.rit.util.BitSet;
import java.io.IOException;

/**
 * Class BitSetVbl provides a reduction variable for a set of integers from 0 to
 * a given upper bound shared by multiple threads executing a {@linkplain
 * edu.rit.pj2.ParallelStatement ParallelStatement}. The largest integer that
 * can be stored is <I>N</I>&minus;1, where <I>N</I> is specified as a
 * constructor argument. The set elements are stored in a bitmap representation.
 * <P>
 * Class BitSetVbl supports the <I>parallel reduction</I> pattern. Each thread
 * creates a thread-local copy of the shared variable by calling the {@link
 * edu.rit.pj2.Loop#threadLocal(Vbl) threadLocal()} method of class {@linkplain
 * edu.rit.pj2.Loop Loop} or the {@link edu.rit.pj2.Section#threadLocal(Vbl)
 * threadLocal()} method of class {@linkplain edu.rit.pj2.Section Section}. Each
 * thread performs operations on its own copy, without needing to synchronize
 * with the other threads. At the end of the parallel statement, the
 * thread-local copies are automatically <I>reduced</I> together, and the result
 * is stored in the original shared variable. The reduction is performed by the
 * shared variable's {@link #reduce(Vbl) reduce()} method.
 * <P>
 * The following subclasses provide various predefined reduction operations. You
 * can also define your own subclasses with customized reduction operations.
 * <UL>
 * <LI>Minimum size -- Class {@linkplain BitSetVbl.MinSize}
 * <LI>Maximum size -- Class {@linkplain BitSetVbl.MaxSize}
 * <LI>Set union -- Class {@linkplain BitSetVbl.Union}
 * <LI>Set intersection -- Class {@linkplain BitSetVbl.Intersection}
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 14-Jan-2015
 */
public class BitSetVbl
	extends BitSet
	implements Vbl
	{

// Exported constructors.

	/**
	 * Construct a new uninitialized set. This constructor is for use only by
	 * object streaming.
	 */
	public BitSetVbl()
		{
		super();
		}

	/**
	 * Construct a new empty set. The set can hold elements from 0 through
	 * <I>N</I>&minus;1 inclusive, where <I>N</I> is the smallest multiple of 32
	 * greater than or equal to <TT>max</TT>.
	 *
	 * @param  max  Maximum number of elements (&ge; 1).
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>max</TT> &lt; 1.
	 */
	public BitSetVbl
		(int max)
		{
		super (max);
		}

	/**
	 * Construct a new set that is a copy of the given set.
	 *
	 * @param  set  Set to copy.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>set</TT> is null.
	 */
	public BitSetVbl
		(BitSet set)
		{
		super (set);
		}

// Exported operations.

	/**
	 * Clear this set.
	 *
	 * @return  This set.
	 */
	public BitSetVbl clear()
		{
		return (BitSetVbl) super.clear();
		}

	/**
	 * Change this set to be a copy of the given set.
	 *
	 * @param  set  Set to copy.
	 *
	 * @return  This set.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>set</TT> is null.
	 */
	public BitSetVbl copy
		(BitSet set)
		{
		return (BitSetVbl) super.copy (set);
		}

	/**
	 * Add the given element to this set. If <TT>elem</TT> is not in the range 0
	 * .. <I>N</I>&minus;1, this set is unchanged.
	 *
	 * @param  elem  Element.
	 *
	 * @return  This set.
	 */
	public BitSetVbl add
		(int elem)
		{
		return (BitSetVbl) super.add (elem);
		}

	/**
	 * Add all elements in the given range to this set. All elements from
	 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are added to this
	 * set. If any element is not in the range 0 .. <I>N</I>&minus;1, that
	 * element is not added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
	 * unchanged.
	 *
	 * @param  lb  Lower bound element (inclusive).
	 * @param  ub  Upper bound element (exclusive).
	 *
	 * @return  This set.
	 */
	public BitSetVbl add
		(int lb,
		 int ub)
		{
		return (BitSetVbl) super.add (lb, ub);
		}

	/**
	 * Remove the given element from this set. If <TT>elem</TT> is not in the
	 * range 0 .. <I>N</I>&minus;1, this set is unchanged.
	 *
	 * @param  elem  Element.
	 *
	 * @return  This set.
	 */
	public BitSetVbl remove
		(int elem)
		{
		return (BitSetVbl) super.remove (elem);
		}

	/**
	 * Remove all elements in the given range from this set. All elements from
	 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are removed from this
	 * set. If any element is not in the range 0 .. <I>N</I>&minus;1, that
	 * element is not removed. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
	 * unchanged.
	 *
	 * @param  lb  Lower bound element (inclusive).
	 * @param  ub  Upper bound element (exclusive).
	 *
	 * @return  This set.
	 */
	public BitSetVbl remove
		(int lb,
		 int ub)
		{
		return (BitSetVbl) super.remove (lb, ub);
		}

	/**
	 * Flip the given element. If this set contains <TT>elem</TT>, it is
	 * removed; if this set does not contain <TT>elem</TT>, it is added. If
	 * <TT>elem</TT> is not in the range 0 .. <I>N</I>&minus;1, this set is
	 * unchanged.
	 *
	 * @param  elem  Element.
	 *
	 * @return  This set.
	 */
	public BitSetVbl flip
		(int elem)
		{
		return (BitSetVbl) super.flip (elem);
		}

	/**
	 * Flip all elements in the given range. All elements from <TT>lb</TT>
	 * through <TT>ub</TT>&minus;1, inclusive, are flipped. If this set contains
	 * such an element, it is removed; if this set does not contain such an
	 * element, it is added. If any element is not in the range 0 ..
	 * <I>N</I>&minus;1, that element is not flipped. If <TT>lb</TT> &ge;
	 * <TT>ub</TT>, this set is unchanged.
	 *
	 * @param  lb  Lower bound element (inclusive).
	 * @param  ub  Upper bound element (exclusive).
	 *
	 * @return  This set.
	 */
	public BitSetVbl flip
		(int lb,
		 int ub)
		{
		return (BitSetVbl) super.flip (lb, ub);
		}

	/**
	 * Change this set to be the union of itself and the given set. The union
	 * consists of all elements that appear in this set or the given set or
	 * both.
	 *
	 * @param  set  Set.
	 *
	 * @return  This set.
	 */
	public BitSetVbl union
		(BitSet set)
		{
		return (BitSetVbl) super.union (set);
		}

	/**
	 * Change this set to be the intersection of itself and the given set. The
	 * intersection consists of all elements that appear in this set and the
	 * given set.
	 *
	 * @param  set  Set.
	 *
	 * @return  This set.
	 */
	public BitSetVbl intersection
		(BitSet set)
		{
		return (BitSetVbl) super.intersection (set);
		}

	/**
	 * Change this set to be the difference of itself and the given set. The
	 * difference consists of all elements that appear in this set and not in
	 * the given set.
	 *
	 * @param  set  Set.
	 *
	 * @return  This set.
	 */
	public BitSetVbl difference
		(BitSet set)
		{
		return (BitSetVbl) super.difference (set);
		}

	/**
	 * Change this set to be the symmetric difference of itself and the given
	 * set. The symmetric difference consists of all elements that appear in
	 * this set or the given set, but not both.
	 *
	 * @param  set  Set.
	 *
	 * @return  This set.
	 */
	public BitSetVbl symmetricDifference
		(BitSet set)
		{
		return (BitSetVbl) super.symmetricDifference (set);
		}

	/**
	 * Create a clone of this shared variable.
	 *
	 * @return  The cloned object.
	 */
	public Object clone()
		{
		try
			{
			return ((BitSetVbl) super.clone()) .copy (this);
			}
		catch (CloneNotSupportedException exc)
			{
			throw new IllegalStateException
				("BitSetVbl.clone(): Shouldn't happen", exc);
			}
		}

	/**
	 * Set this shared variable to the given shared variable.
	 *
	 * @param  vbl  Shared variable.
	 *
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if the class of <TT>vbl</TT> is not
	 *     compatible with the class of this shared variable.
	 */
	public void set
		(Vbl vbl)
		{
		copy ((BitSetVbl)vbl);
		}

	/**
	 * Reduce the given shared variable into this shared variable. The two
	 * variables are combined together, and the result is stored in this shared
	 * variable. The <TT>reduce()</TT> method does not need to be multiple
	 * thread safe (thread synchronization is handled by the caller).
	 * <P>
	 * The BitSetVbl base class's <TT>reduce()</TT> method leaves this shared
	 * variable unchanged.
	 *
	 * @param  vbl  Shared variable.
	 *
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if the class of <TT>vbl</TT> is not
	 *     compatible with the class of this shared variable.
	 */
	public void reduce
		(Vbl vbl)
		{
		}

// Exported subclasses.

	/**
	 * Class BitSetVbl.MinSize provides a reduction variable for a set of
	 * integers from 0 to a given upper bound, where the reduction operation is
	 * to keep the set with the smallest size. The set elements are stored in a
	 * bitmap representation. The largest integer that can be stored is
	 * <I>N</I>&minus;1, where <I>N</I> is specified as a constructor argument.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class MinSize
		extends BitSetVbl
		{

	// Exported constructors.

		/**
		 * Construct a new uninitialized set. This constructor is for use only
		 * by object streaming.
		 */
		public MinSize()
			{
			super();
			}

		/**
		 * Construct a new empty set. The set can hold elements from 0 through
		 * <I>N</I>&minus;1 inclusive, where <I>N</I> is the smallest multiple
		 * of 32 greater than or equal to <TT>max</TT>.
		 *
		 * @param  max  Maximum number of elements (&ge; 1).
		 *
		 * @exception  IllegalArgumentException
		 *     (unchecked exception) Thrown if <TT>max</TT> &lt; 1.
		 */
		public MinSize
			(int max)
			{
			super (max);
			}

		/**
		 * Construct a new set that is a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public MinSize
			(BitSet set)
			{
			super (set);
			}

		/**
		 * Clear this set.
		 *
		 * @return  This set.
		 */
		public MinSize clear()
			{
			return (MinSize) super.clear();
			}

		/**
		 * Change this set to be a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @return  This set.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public MinSize copy
			(BitSet set)
			{
			return (MinSize) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public MinSize add
			(int elem)
			{
			return (MinSize) super.add (elem);
			}

		/**
		 * Add all elements in the given range to this set. All elements from
		 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are added to this
		 * set. If any element is not in the range 0 .. <I>N</I>&minus;1, that
		 * element is not added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public MinSize add
			(int lb,
			 int ub)
			{
			return (MinSize) super.add (lb, ub);
			}

		/**
		 * Remove the given element from this set. If <TT>elem</TT> is not in
		 * the range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public MinSize remove
			(int elem)
			{
			return (MinSize) super.remove (elem);
			}

		/**
		 * Remove all elements in the given range from this set. All elements
		 * from <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are removed
		 * from this set. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not removed. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public MinSize remove
			(int lb,
			 int ub)
			{
			return (MinSize) super.remove (lb, ub);
			}

		/**
		 * Flip the given element. If this set contains <TT>elem</TT>, it is
		 * removed; if this set does not contain <TT>elem</TT>, it is added. If
		 * <TT>elem</TT> is not in the range 0 .. <I>N</I>&minus;1, this set is
		 * unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public MinSize flip
			(int elem)
			{
			return (MinSize) super.flip (elem);
			}

		/**
		 * Flip all elements in the given range. All elements from <TT>lb</TT>
		 * through <TT>ub</TT>&minus;1, inclusive, are flipped. If this set
		 * contains such an element, it is removed; if this set does not contain
		 * such an element, it is added. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not flipped. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public MinSize flip
			(int lb,
			 int ub)
			{
			return (MinSize) super.flip (lb, ub);
			}

		/**
		 * Change this set to be the union of itself and the given set. The
		 * union consists of all elements that appear in this set or the given
		 * set or both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MinSize union
			(BitSet set)
			{
			return (MinSize) super.union (set);
			}

		/**
		 * Change this set to be the intersection of itself and the given set.
		 * The intersection consists of all elements that appear in this set and
		 * the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MinSize intersection
			(BitSet set)
			{
			return (MinSize) super.intersection (set);
			}

		/**
		 * Change this set to be the difference of itself and the given set. The
		 * difference consists of all elements that appear in this set and not
		 * in the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MinSize difference
			(BitSet set)
			{
			return (MinSize) super.difference (set);
			}

		/**
		 * Change this set to be the symmetric difference of itself and the
		 * given set. The symmetric difference consists of all elements that
		 * appear in this set or the given set, but not both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MinSize symmetricDifference
			(BitSet set)
			{
			return (MinSize) super.symmetricDifference (set);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSetVbl.MinSize class's <TT>reduce()</TT> method changes this
		 * set to the given set if the given set's size is smaller.
		 *
		 * @param  vbl  Shared variable.
		 *
		 * @exception  ClassCastException
		 *     (unchecked exception) Thrown if the class of <TT>vbl</TT> is not
		 *     compatible with the class of this shared variable.
		 */
		public void reduce
			(Vbl vbl)
			{
			BitSetVbl set = (BitSetVbl)vbl;
			if (set.size() < this.size())
				this.copy (set);
			}
		}

	/**
	 * Class BitSetVbl.MaxSize provides a reduction variable for a set of
	 * integers from 0 to a given upper bound, where the reduction operation is
	 * to keep the set with the largest size. The set elements are stored in a
	 * bitmap representation. The largest integer that can be stored is
	 * <I>N</I>&minus;1, where <I>N</I> is specified as a constructor argument.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class MaxSize
		extends BitSetVbl
		{

	// Exported constructors.

		/**
		 * Construct a new uninitialized set. This constructor is for use only
		 * by object streaming.
		 */
		public MaxSize()
			{
			super();
			}

		/**
		 * Construct a new empty set. The set can hold elements from 0 through
		 * <I>N</I>&minus;1 inclusive, where <I>N</I> is the smallest multiple
		 * of 32 greater than or equal to <TT>max</TT>.
		 *
		 * @param  max  Maximum number of elements (&ge; 1).
		 *
		 * @exception  IllegalArgumentException
		 *     (unchecked exception) Thrown if <TT>max</TT> &lt; 1.
		 */
		public MaxSize
			(int max)
			{
			super (max);
			}

		/**
		 * Construct a new set that is a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public MaxSize
			(BitSet set)
			{
			super (set);
			}

		/**
		 * Clear this set.
		 *
		 * @return  This set.
		 */
		public MaxSize clear()
			{
			return (MaxSize) super.clear();
			}

		/**
		 * Change this set to be a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @return  This set.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public MaxSize copy
			(BitSet set)
			{
			return (MaxSize) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public MaxSize add
			(int elem)
			{
			return (MaxSize) super.add (elem);
			}

		/**
		 * Add all elements in the given range to this set. All elements from
		 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are added to this
		 * set. If any element is not in the range 0 .. <I>N</I>&minus;1, that
		 * element is not added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public MaxSize add
			(int lb,
			 int ub)
			{
			return (MaxSize) super.add (lb, ub);
			}

		/**
		 * Remove the given element from this set. If <TT>elem</TT> is not in
		 * the range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public MaxSize remove
			(int elem)
			{
			return (MaxSize) super.remove (elem);
			}

		/**
		 * Remove all elements in the given range from this set. All elements
		 * from <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are removed
		 * from this set. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not removed. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public MaxSize remove
			(int lb,
			 int ub)
			{
			return (MaxSize) super.remove (lb, ub);
			}

		/**
		 * Flip the given element. If this set contains <TT>elem</TT>, it is
		 * removed; if this set does not contain <TT>elem</TT>, it is added. If
		 * <TT>elem</TT> is not in the range 0 .. <I>N</I>&minus;1, this set is
		 * unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public MaxSize flip
			(int elem)
			{
			return (MaxSize) super.flip (elem);
			}

		/**
		 * Flip all elements in the given range. All elements from <TT>lb</TT>
		 * through <TT>ub</TT>&minus;1, inclusive, are flipped. If this set
		 * contains such an element, it is removed; if this set does not contain
		 * such an element, it is added. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not flipped. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public MaxSize flip
			(int lb,
			 int ub)
			{
			return (MaxSize) super.flip (lb, ub);
			}

		/**
		 * Change this set to be the union of itself and the given set. The
		 * union consists of all elements that appear in this set or the given
		 * set or both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MaxSize union
			(BitSet set)
			{
			return (MaxSize) super.union (set);
			}

		/**
		 * Change this set to be the intersection of itself and the given set.
		 * The intersection consists of all elements that appear in this set and
		 * the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MaxSize intersection
			(BitSet set)
			{
			return (MaxSize) super.intersection (set);
			}

		/**
		 * Change this set to be the difference of itself and the given set. The
		 * difference consists of all elements that appear in this set and not
		 * in the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MaxSize difference
			(BitSet set)
			{
			return (MaxSize) super.difference (set);
			}

		/**
		 * Change this set to be the symmetric difference of itself and the
		 * given set. The symmetric difference consists of all elements that
		 * appear in this set or the given set, but not both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public MaxSize symmetricDifference
			(BitSet set)
			{
			return (MaxSize) super.symmetricDifference (set);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSetVbl.MaxSize class's <TT>reduce()</TT> method changes this
		 * set to the given set if the given set's size is larger.
		 *
		 * @param  vbl  Shared variable.
		 *
		 * @exception  ClassCastException
		 *     (unchecked exception) Thrown if the class of <TT>vbl</TT> is not
		 *     compatible with the class of this shared variable.
		 */
		public void reduce
			(Vbl vbl)
			{
			BitSetVbl set = (BitSetVbl)vbl;
			if (set.size() > this.size())
				this.copy (set);
			}
		}

	/**
	 * Class BitSetVbl.Union provides a reduction variable for a set of integers
	 * from 0 to a given upper bound, where the reduction operation is set
	 * union. The set elements are stored in a bitmap representation. The
	 * largest integer that can be stored is <I>N</I>&minus;1, where <I>N</I> is
	 * specified as a constructor argument.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class Union
		extends BitSetVbl
		{

	// Exported constructors.

		/**
		 * Construct a new uninitialized set. This constructor is for use only
		 * by object streaming.
		 */
		public Union()
			{
			super();
			}

		/**
		 * Construct a new empty set. The set can hold elements from 0 through
		 * <I>N</I>&minus;1 inclusive, where <I>N</I> is the smallest multiple
		 * of 32 greater than or equal to <TT>max</TT>.
		 *
		 * @param  max  Maximum number of elements (&ge; 1).
		 *
		 * @exception  IllegalArgumentException
		 *     (unchecked exception) Thrown if <TT>max</TT> &lt; 1.
		 */
		public Union
			(int max)
			{
			super (max);
			}

		/**
		 * Construct a new set that is a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public Union
			(BitSet set)
			{
			super (set);
			}

		/**
		 * Clear this set.
		 *
		 * @return  This set.
		 */
		public Union clear()
			{
			return (Union) super.clear();
			}

		/**
		 * Change this set to be a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @return  This set.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public Union copy
			(BitSet set)
			{
			return (Union) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public Union add
			(int elem)
			{
			return (Union) super.add (elem);
			}

		/**
		 * Add all elements in the given range to this set. All elements from
		 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are added to this
		 * set. If any element is not in the range 0 .. <I>N</I>&minus;1, that
		 * element is not added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public Union add
			(int lb,
			 int ub)
			{
			return (Union) super.add (lb, ub);
			}

		/**
		 * Remove the given element from this set. If <TT>elem</TT> is not in
		 * the range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public Union remove
			(int elem)
			{
			return (Union) super.remove (elem);
			}

		/**
		 * Remove all elements in the given range from this set. All elements
		 * from <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are removed
		 * from this set. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not removed. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public Union remove
			(int lb,
			 int ub)
			{
			return (Union) super.remove (lb, ub);
			}

		/**
		 * Flip the given element. If this set contains <TT>elem</TT>, it is
		 * removed; if this set does not contain <TT>elem</TT>, it is added. If
		 * <TT>elem</TT> is not in the range 0 .. <I>N</I>&minus;1, this set is
		 * unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public Union flip
			(int elem)
			{
			return (Union) super.flip (elem);
			}

		/**
		 * Flip all elements in the given range. All elements from <TT>lb</TT>
		 * through <TT>ub</TT>&minus;1, inclusive, are flipped. If this set
		 * contains such an element, it is removed; if this set does not contain
		 * such an element, it is added. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not flipped. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public Union flip
			(int lb,
			 int ub)
			{
			return (Union) super.flip (lb, ub);
			}

		/**
		 * Change this set to be the union of itself and the given set. The
		 * union consists of all elements that appear in this set or the given
		 * set or both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Union union
			(BitSet set)
			{
			return (Union) super.union (set);
			}

		/**
		 * Change this set to be the intersection of itself and the given set.
		 * The intersection consists of all elements that appear in this set and
		 * the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Union intersection
			(BitSet set)
			{
			return (Union) super.intersection (set);
			}

		/**
		 * Change this set to be the difference of itself and the given set. The
		 * difference consists of all elements that appear in this set and not
		 * in the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Union difference
			(BitSet set)
			{
			return (Union) super.difference (set);
			}

		/**
		 * Change this set to be the symmetric difference of itself and the
		 * given set. The symmetric difference consists of all elements that
		 * appear in this set or the given set, but not both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Union symmetricDifference
			(BitSet set)
			{
			return (Union) super.symmetricDifference (set);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSetVbl.Union class's <TT>reduce()</TT> method changes this
		 * set to the union of this set and the given set.
		 *
		 * @param  vbl  Shared variable.
		 *
		 * @exception  ClassCastException
		 *     (unchecked exception) Thrown if the class of <TT>vbl</TT> is not
		 *     compatible with the class of this shared variable.
		 */
		public void reduce
			(Vbl vbl)
			{
			union ((BitSetVbl)vbl);
			}
		}

	/**
	 * Class BitSetVbl.Intersection provides a reduction variable for a set of
	 * integers from 0 to a given upper bound, where the reduction operation is
	 * set intersection. The set elements are stored in a bitmap representation.
	 * The largest integer that can be stored is <I>N</I>&minus;1, where
	 * <I>N</I> is specified as a constructor argument.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class Intersection
		extends BitSetVbl
		{

	// Exported constructors.

		/**
		 * Construct a new uninitialized set. This constructor is for use only
		 * by object streaming.
		 */
		public Intersection()
			{
			super();
			}

		/**
		 * Construct a new empty set. The set can hold elements from 0 through
		 * <I>N</I>&minus;1 inclusive, where <I>N</I> is the smallest multiple
		 * of 32 greater than or equal to <TT>max</TT>.
		 *
		 * @param  max  Maximum number of elements (&ge; 1).
		 *
		 * @exception  IllegalArgumentException
		 *     (unchecked exception) Thrown if <TT>max</TT> &lt; 1.
		 */
		public Intersection
			(int max)
			{
			super (max);
			}

		/**
		 * Construct a new set that is a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public Intersection
			(BitSet set)
			{
			super (set);
			}

		/**
		 * Clear this set.
		 *
		 * @return  This set.
		 */
		public Intersection clear()
			{
			return (Intersection) super.clear();
			}

		/**
		 * Change this set to be a copy of the given set.
		 *
		 * @param  set  Set to copy.
		 *
		 * @return  This set.
		 *
		 * @exception  NullPointerException
		 *     (unchecked exception) Thrown if <TT>set</TT> is null.
		 */
		public Intersection copy
			(BitSet set)
			{
			return (Intersection) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public Intersection add
			(int elem)
			{
			return (Intersection) super.add (elem);
			}

		/**
		 * Add all elements in the given range to this set. All elements from
		 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are added to this
		 * set. If any element is not in the range 0 .. <I>N</I>&minus;1, that
		 * element is not added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public Intersection add
			(int lb,
			 int ub)
			{
			return (Intersection) super.add (lb, ub);
			}

		/**
		 * Remove the given element from this set. If <TT>elem</TT> is not in
		 * the range 0 .. <I>N</I>&minus;1, this set is unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public Intersection remove
			(int elem)
			{
			return (Intersection) super.remove (elem);
			}

		/**
		 * Remove all elements in the given range from this set. All elements
		 * from <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are removed
		 * from this set. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not removed. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public Intersection remove
			(int lb,
			 int ub)
			{
			return (Intersection) super.remove (lb, ub);
			}

		/**
		 * Flip the given element. If this set contains <TT>elem</TT>, it is
		 * removed; if this set does not contain <TT>elem</TT>, it is added. If
		 * <TT>elem</TT> is not in the range 0 .. <I>N</I>&minus;1, this set is
		 * unchanged.
		 *
		 * @param  elem  Element.
		 *
		 * @return  This set.
		 */
		public Intersection flip
			(int elem)
			{
			return (Intersection) super.flip (elem);
			}

		/**
		 * Flip all elements in the given range. All elements from <TT>lb</TT>
		 * through <TT>ub</TT>&minus;1, inclusive, are flipped. If this set
		 * contains such an element, it is removed; if this set does not contain
		 * such an element, it is added. If any element is not in the range 0 ..
		 * <I>N</I>&minus;1, that element is not flipped. If <TT>lb</TT> &ge;
		 * <TT>ub</TT>, this set is unchanged.
		 *
		 * @param  lb  Lower bound element (inclusive).
		 * @param  ub  Upper bound element (exclusive).
		 *
		 * @return  This set.
		 */
		public Intersection flip
			(int lb,
			 int ub)
			{
			return (Intersection) super.flip (lb, ub);
			}

		/**
		 * Change this set to be the union of itself and the given set. The
		 * union consists of all elements that appear in this set or the given
		 * set or both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Intersection union
			(BitSet set)
			{
			return (Intersection) super.union (set);
			}

		/**
		 * Change this set to be the intersection of itself and the given set.
		 * The intersection consists of all elements that appear in this set and
		 * the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Intersection intersection
			(BitSet set)
			{
			return (Intersection) super.intersection (set);
			}

		/**
		 * Change this set to be the difference of itself and the given set. The
		 * difference consists of all elements that appear in this set and not
		 * in the given set.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Intersection difference
			(BitSet set)
			{
			return (Intersection) super.difference (set);
			}

		/**
		 * Change this set to be the symmetric difference of itself and the
		 * given set. The symmetric difference consists of all elements that
		 * appear in this set or the given set, but not both.
		 *
		 * @param  set  Set.
		 *
		 * @return  This set.
		 */
		public Intersection symmetricDifference
			(BitSet set)
			{
			return (Intersection) super.symmetricDifference (set);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSetVbl.Intersection class's <TT>reduce()</TT> method changes
		 * this set to the intersection of this set and the given set.
		 *
		 * @param  vbl  Shared variable.
		 *
		 * @exception  ClassCastException
		 *     (unchecked exception) Thrown if the class of <TT>vbl</TT> is not
		 *     compatible with the class of this shared variable.
		 */
		public void reduce
			(Vbl vbl)
			{
			intersection ((BitSetVbl)vbl);
			}
		}

	}
