//******************************************************************************
//
// File:    BitSet64Vbl.java
// Package: edu.rit.pj2.vbl
// Unit:    Class edu.rit.pj2.vbl.BitSet64Vbl
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
import edu.rit.pj2.TerminateException;
import edu.rit.util.BitSet64;
import java.io.IOException;

/**
 * Class BitSet64Vbl provides a reduction variable for a set of integers from 0
 * to 63 shared by multiple threads executing a {@linkplain
 * edu.rit.pj2.ParallelStatement ParallelStatement}.
 * <P>
 * The set elements are stored in a bitmap representation. The bitmap
 * representation is a value of type <TT>long</TT>. Bit 0 of the bitmap (the
 * least significant bit) corresponds to set element 0, bit 1 of the bitmap (the
 * next least significant bit) corresponds to set element 1, and so on. A bit of
 * the bitmap is 1 if the set contains the corresponding element; a bit of the
 * bitmap is 0 if the set does not contain the corresponding element.
 * <P>
 * Class BitSet64Vbl supports the <I>parallel reduction</I> pattern. Each thread
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
 * <LI>Minimum size -- Class {@linkplain BitSet64Vbl.MinSize}
 * <LI>Maximum size -- Class {@linkplain BitSet64Vbl.MaxSize}
 * <LI>Set union -- Class {@linkplain BitSet64Vbl.Union}
 * <LI>Set intersection -- Class {@linkplain BitSet64Vbl.Intersection}
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 14-Jan-2015
 */
public class BitSet64Vbl
	extends BitSet64
	implements Vbl
	{

// Exported constructors.

	/**
	 * Construct a new empty set.
	 */
	public BitSet64Vbl()
		{
		super();
		}

	/**
	 * Construct a new set with the elements in the given bitmap.
	 *
	 * @param  bitmap  Bitmap of set elements.
	 */
	public BitSet64Vbl
		(long bitmap)
		{
		super (bitmap);
		}

	/**
	 * Construct a new set that is a copy of the given set.
	 *
	 * @param  set  Set to copy.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>set</TT> is null.
	 */
	public BitSet64Vbl
		(BitSet64 set)
		{
		super (set);
		}

// Exported operations.

	/**
	 * Clear this set.
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl clear()
		{
		return (BitSet64Vbl) super.clear();
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
	public BitSet64Vbl copy
		(BitSet64 set)
		{
		return (BitSet64Vbl) super.copy (set);
		}

	/**
	 * Add the given element to this set. If <TT>elem</TT> is not in the range 0
	 * .. 63, this set is unchanged.
	 *
	 * @param  elem  Element.
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl add
		(int elem)
		{
		return (BitSet64Vbl) super.add (elem);
		}

	/**
	 * Add all elements in the given range to this set. All elements from
	 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are added to this
	 * set. If any element is not in the range 0 .. 63, that element is not
	 * added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is unchanged.
	 *
	 * @param  lb  Lower bound element (inclusive).
	 * @param  ub  Upper bound element (exclusive).
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl add
		(int lb,
		 int ub)
		{
		return (BitSet64Vbl) super.add (lb, ub);
		}

	/**
	 * Remove the given element from this set. If <TT>elem</TT> is not in the
	 * range 0 .. 63, this set is unchanged.
	 *
	 * @param  elem  Element.
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl remove
		(int elem)
		{
		return (BitSet64Vbl) super.remove (elem);
		}

	/**
	 * Remove all elements in the given range from this set. All elements from
	 * <TT>lb</TT> through <TT>ub</TT>&minus;1, inclusive, are removed from this
	 * set. If any element is not in the range 0 .. 63, that element is not
	 * removed. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is unchanged.
	 *
	 * @param  lb  Lower bound element (inclusive).
	 * @param  ub  Upper bound element (exclusive).
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl remove
		(int lb,
		 int ub)
		{
		return (BitSet64Vbl) super.remove (lb, ub);
		}

	/**
	 * Flip the given element. If this set contains <TT>elem</TT>, it is
	 * removed; if this set does not contain <TT>elem</TT>, it is added. If
	 * <TT>elem</TT> is not in the range 0 .. 63, this set is unchanged.
	 *
	 * @param  elem  Element.
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl flip
		(int elem)
		{
		return (BitSet64Vbl) super.flip (elem);
		}

	/**
	 * Flip all elements in the given range. All elements from <TT>lb</TT>
	 * through <TT>ub</TT>&minus;1, inclusive, are flipped. If this set contains
	 * such an element, it is removed; if this set does not contain such an
	 * element, it is added. If any element is not in the range 0 .. 63, that
	 * element is not flipped. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
	 * unchanged.
	 *
	 * @param  lb  Lower bound element (inclusive).
	 * @param  ub  Upper bound element (exclusive).
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl flip
		(int lb,
		 int ub)
		{
		return (BitSet64Vbl) super.flip (lb, ub);
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
	public BitSet64Vbl union
		(BitSet64 set)
		{
		return (BitSet64Vbl) super.union (set);
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
	public BitSet64Vbl intersection
		(BitSet64 set)
		{
		return (BitSet64Vbl) super.intersection (set);
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
	public BitSet64Vbl difference
		(BitSet64 set)
		{
		return (BitSet64Vbl) super.difference (set);
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
	public BitSet64Vbl symmetricDifference
		(BitSet64 set)
		{
		return (BitSet64Vbl) super.symmetricDifference (set);
		}

	/**
	 * Change this set's elements to be those in the given bitmap.
	 *
	 * @param  bitmap  Bitmap of set elements.
	 *
	 * @return  This set.
	 */
	public BitSet64Vbl bitmap
		(long bitmap)
		{
		return (BitSet64Vbl) super.bitmap (bitmap);
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
			return super.clone();
			}
		catch (CloneNotSupportedException exc)
			{
			throw new TerminateException ("Shouldn't happen", exc);
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
		copy ((BitSet64Vbl)vbl);
		}

	/**
	 * Reduce the given shared variable into this shared variable. The two
	 * variables are combined together, and the result is stored in this shared
	 * variable. The <TT>reduce()</TT> method does not need to be multiple
	 * thread safe (thread synchronization is handled by the caller).
	 * <P>
	 * The BitSet64Vbl base class's <TT>reduce()</TT> method leaves this shared
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

// Exported classes.

	/**
	 * Class BitSet64Vbl.MinSize provides a reduction variable for a set of
	 * integers from 0 to 63, where the reduction operation is to keep the set
	 * with the smallest size. The set elements are stored in a bitmap
	 * representation.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class MinSize
		extends BitSet64Vbl
		{

	// Exported constructors.

		/**
		 * Construct a new empty set.
		 */
		public MinSize()
			{
			super();
			}

		/**
		 * Construct a new set with the elements in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 */
		public MinSize
			(long bitmap)
			{
			super (bitmap);
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (MinSize) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. 63, this set is unchanged.
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
		 * set. If any element is not in the range 0 .. 63, that element is not
		 * added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is unchanged.
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
		 * the range 0 .. 63, this set is unchanged.
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
		 * from this set. If any element is not in the range 0 .. 63, that
		 * element is not removed. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
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
		 * <TT>elem</TT> is not in the range 0 .. 63, this set is unchanged.
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
		 * 63, that element is not flipped. If <TT>lb</TT> &ge; <TT>ub</TT>,
		 * this set is unchanged.
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (MinSize) super.symmetricDifference (set);
			}

		/**
		 * Change this set's elements to be those in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 *
		 * @return  This set.
		 */
		public MinSize bitmap
			(long bitmap)
			{
			return (MinSize) super.bitmap (bitmap);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSet64Vbl.MinSize class's <TT>reduce()</TT> method changes this
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
			BitSet64Vbl set = (BitSet64Vbl)vbl;
			if (set.size() < this.size())
				this.copy (set);
			}
		}

	/**
	 * Class BitSet64Vbl.MaxSize provides a reduction variable for a set of
	 * integers from 0 to 63, where the reduction operation is to keep the set
	 * with the largest size. The set elements are stored in a bitmap
	 * representation.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class MaxSize
		extends BitSet64Vbl
		{

	// Exported constructors.

		/**
		 * Construct a new empty set.
		 */
		public MaxSize()
			{
			super();
			}

		/**
		 * Construct a new set with the elements in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 */
		public MaxSize
			(long bitmap)
			{
			super (bitmap);
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (MaxSize) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. 63, this set is unchanged.
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
		 * set. If any element is not in the range 0 .. 63, that element is not
		 * added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is unchanged.
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
		 * the range 0 .. 63, this set is unchanged.
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
		 * from this set. If any element is not in the range 0 .. 63, that
		 * element is not removed. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
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
		 * <TT>elem</TT> is not in the range 0 .. 63, this set is unchanged.
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
		 * 63, that element is not flipped. If <TT>lb</TT> &ge; <TT>ub</TT>,
		 * this set is unchanged.
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (MaxSize) super.symmetricDifference (set);
			}

		/**
		 * Change this set's elements to be those in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 *
		 * @return  This set.
		 */
		public MaxSize bitmap
			(long bitmap)
			{
			return (MaxSize) super.bitmap (bitmap);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSet64Vbl.MaxSize class's <TT>reduce()</TT> method changes this
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
			BitSet64Vbl set = (BitSet64Vbl)vbl;
			if (set.size() > this.size())
				this.copy (set);
			}
		}

	/**
	 * Class BitSet64Vbl.Union provides a reduction variable for a set of
	 * integers from 0 to 63, where the reduction operation is set union. The
	 * set elements are stored in a bitmap representation.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class Union
		extends BitSet64Vbl
		{

	// Exported constructors.

		/**
		 * Construct a new empty set.
		 */
		public Union()
			{
			super();
			}

		/**
		 * Construct a new set with the elements in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 */
		public Union
			(long bitmap)
			{
			super (bitmap);
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (Union) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. 63, this set is unchanged.
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
		 * set. If any element is not in the range 0 .. 63, that element is not
		 * added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is unchanged.
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
		 * the range 0 .. 63, this set is unchanged.
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
		 * from this set. If any element is not in the range 0 .. 63, that
		 * element is not removed. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
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
		 * <TT>elem</TT> is not in the range 0 .. 63, this set is unchanged.
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
		 * 63, that element is not flipped. If <TT>lb</TT> &ge; <TT>ub</TT>,
		 * this set is unchanged.
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (Union) super.symmetricDifference (set);
			}

		/**
		 * Change this set's elements to be those in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 *
		 * @return  This set.
		 */
		public Union bitmap
			(long bitmap)
			{
			return (Union) super.bitmap (bitmap);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSet64Vbl.Union class's <TT>reduce()</TT> method changes this
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
			union ((BitSet64Vbl)vbl);
			}
		}

	/**
	 * Class BitSet64Vbl.Intersection provides a reduction variable for a set of
	 * integers from 0 to 63, where the reduction operation is set intersection.
	 * The set elements are stored in a bitmap representation.
	 *
	 * @author  Alan Kaminsky
	 * @version 13-Jan-2015
	 */
	public static class Intersection
		extends BitSet64Vbl
		{

	// Exported constructors.

		/**
		 * Construct a new empty set.
		 */
		public Intersection()
			{
			super();
			}

		/**
		 * Construct a new set with the elements in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 */
		public Intersection
			(long bitmap)
			{
			super (bitmap);
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (Intersection) super.copy (set);
			}

		/**
		 * Add the given element to this set. If <TT>elem</TT> is not in the
		 * range 0 .. 63, this set is unchanged.
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
		 * set. If any element is not in the range 0 .. 63, that element is not
		 * added. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is unchanged.
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
		 * the range 0 .. 63, this set is unchanged.
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
		 * from this set. If any element is not in the range 0 .. 63, that
		 * element is not removed. If <TT>lb</TT> &ge; <TT>ub</TT>, this set is
		 * unchanged.
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
		 * <TT>elem</TT> is not in the range 0 .. 63, this set is unchanged.
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
		 * 63, that element is not flipped. If <TT>lb</TT> &ge; <TT>ub</TT>,
		 * this set is unchanged.
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
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
			(BitSet64 set)
			{
			return (Intersection) super.symmetricDifference (set);
			}

		/**
		 * Change this set's elements to be those in the given bitmap.
		 *
		 * @param  bitmap  Bitmap of set elements.
		 *
		 * @return  This set.
		 */
		public Intersection bitmap
			(long bitmap)
			{
			return (Intersection) super.bitmap (bitmap);
			}

		/**
		 * Reduce the given shared variable into this shared variable. The two
		 * variables are combined together, and the result is stored in this
		 * shared variable. The <TT>reduce()</TT> method does not need to be
		 * multiple thread safe (thread synchronization is handled by the
		 * caller).
		 * <P>
		 * The BitSet64Vbl.Intersection class's <TT>reduce()</TT> method changes
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
			intersection ((BitSet64Vbl)vbl);
			}
		}

	}
