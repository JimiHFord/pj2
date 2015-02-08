//******************************************************************************
//
// File:    Instance.java
// Package: edu.rit.util
// Unit:    Class edu.rit.util.Instance
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

package edu.rit.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Class Instance provides static methods for creating instances of classes.
 *
 * @author  Alan Kaminsky
 * @version 10-Jan-2015
 */
public class Instance
	{

// Prevent construction.

	private Instance()
		{
		}

// Exported operations.

	/**
	 * Create a new instance of a class as specified by the given string.
	 * Calling this method is equivalent to calling
	 * <TT>newInstance(s,false)</TT>. See the {@link
	 * #newInstance(String,boolean) newInstance(String,boolean)} method for
	 * further information.
	 *
	 * @param  s  Constructor expression string.
	 *
	 * @return  New instance.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>s</TT> does not obey the required
	 *     syntax.
	 * @exception  ClassNotFoundException
	 *     Thrown if the given class cannot be found.
	 * @exception  NoSuchMethodException
	 *     Thrown if a suitable constructor cannot be found in the given class.
	 * @exception  InstantiationException
	 *     Thrown if an instance cannot be created because the given class is an
	 *     interface or an abstract class.
	 * @exception  IllegalAccessException
	 *     Thrown if an instance cannot be created because the calling method
	 *     does not have access to the given constructor.
	 * @exception  InvocationTargetException
	 *     Thrown if the given constructor throws an exception.
	 */
	public static Object newInstance
		(String s)
		throws
			ClassNotFoundException,
			NoSuchMethodException,
			InstantiationException,
			IllegalAccessException,
			InvocationTargetException
		{
		return newInstance (s, false);
		}

	/**
	 * Create a new instance of a class as specified by the given string. The
	 * string must consist of a fully-qualified class name, a left parenthesis,
	 * zero or more comma-separated arguments, and a right parenthesis. No
	 * whitespace is allowed. This method attempts to find a constructor for the
	 * given class as follows, where <I>N</I> is the number of arguments:
	 * <UL>
	 * <P><LI>
	 * If <I>N</I> = 0, use a no-argument constructor.
	 * <P><LI>
	 * Else if all arguments are integers, use a constructor with <I>N</I>
	 * arguments of type <TT>int</TT>.
	 * <P><LI>
	 * Else if all arguments are integers and there is no such constructor, use
	 * a constructor with one argument of type <TT>int[]</TT>.
	 * <P><LI>
	 * Else if not all arguments are integers, use a constructor with <I>N</I>
	 * arguments of type <TT>String</TT>.
	 * <P><LI>
	 * Else if not all arguments are integers and there is no such constructor,
	 * use a constructor with one argument of type <TT>String[]</TT>.
	 * <P><LI>
	 * Else throw a NoSuchMethodException.
	 * </UL>
	 * <P>
	 * This method invokes the chosen constructor, passing in the given argument
	 * values, and returns a reference to the newly-created instance.
	 * <P>
	 * If the <TT>disableAccessChecks</TT> argument is true, access checks are
	 * suppressed when constructing the instance. This means the object's class
	 * and/or the class's pertinent constructor need not be public, and a new
	 * instance will still be constructed. However, this also requires that
	 * either (a) a security manager is not installed, or (b) the security
	 * manager allows ReflectPermission("suppressAccessChecks"). See the
	 * <TT>java.lang.reflect.Constructor.setAccessible()</TT> method for further
	 * information.
	 * <P>
	 * <I>Note:</I> To find the given class, the calling thread's context class
	 * loader is used.
	 *
	 * @param  s  Constructor expression string.
	 * @param  disableAccessChecks  True to disable access checks, false to
	 *                              perform access checks.
	 *
	 * @return  New instance.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>s</TT> does not obey the required
	 *     syntax.
	 * @exception  ClassNotFoundException
	 *     Thrown if the given class cannot be found.
	 * @exception  NoSuchMethodException
	 *     Thrown if a suitable constructor cannot be found in the given class.
	 * @exception  InstantiationException
	 *     Thrown if an instance cannot be created because the given class is an
	 *     interface or an abstract class.
	 * @exception  IllegalAccessException
	 *     Thrown if an instance cannot be created because the calling method
	 *     does not have access to the class or constructor.
	 * @exception  InvocationTargetException
	 *     Thrown if the constructor throws an exception.
	 */
	public static Object newInstance
		(String s,
		 boolean disableAccessChecks)
		throws
			ClassNotFoundException,
			NoSuchMethodException,
			InstantiationException,
			IllegalAccessException,
			InvocationTargetException
		{
		int state = 0;
		int nest = 0;
		int i = 0;
		int len = s.length();
		char c;
		StringBuilder token = new StringBuilder();
		String classname = null;
		AList<String> arglist = new AList<String>();

		while (i < len)
			{
			c = s.charAt (i);
			switch (state)
				{
				case 0: // Class name
					if (c == '(')
						{
						if (token.length() == 0)
							syntaxError (s, i);
						classname = token.toString();
						token = new StringBuilder();
						state = 1;
						}
					else if (c == ')')
						syntaxError (s, i);
					else if (c == ',')
						syntaxError (s, i);
					else
						token.append (c);
					break;
				case 1: // Constructor argument
					if (c == '(')
						{
						token.append (c);
						++ nest;
						}
					else if (c == ')')
						{
						if (nest == 0)
							{
							if (token.length() == 0 && arglist.size() > 0)
								syntaxError (s, i);
							else if (token.length() > 0)
								arglist.addLast (token.toString());
							state = 2;
							}
						else
							{
							token.append (c);
							-- nest;
							}
						}
					else if (c == ',')
						{
						if (nest == 0)
							{
							if (token.length() == 0)
								syntaxError (s, i);
							arglist.addLast (token.toString());
							token = new StringBuilder();
							}
						else
							token.append (c);
						}
					else
						token.append (c);
					break;
				case 2: // After closing right parenthesis
					syntaxError (s, i);
					break;
				}
			++ i;
			}
		if (state == 0)
			syntaxError (s, i - 1);

		// Get arguments as strings and integers.
		String[] args = arglist.toArray (new String [arglist.size()]);
		Integer[] intargs = new Integer [args.length];
		boolean allAreInts = true;
		for (i = 0; i < args.length; ++ i)
			{
			try
				{
				intargs[i] = new Integer (args[i]);
				}
			catch (NumberFormatException exc)
				{
				allAreInts = false;
				}
			}

		// Get class.
		Class<?> theClass = Class.forName
			(classname,
			 true,
			 Thread.currentThread().getContextClassLoader());

		// Get constructor and create instance.
		Constructor<?> ctor = null;
		Class<?>[] argtypes = null;

		// No-argument constructor.
		if (args.length == 0)
			{
			try
				{
				ctor = theClass.getConstructor();
				ctor.setAccessible (disableAccessChecks);
				return ctor.newInstance();
				}
			catch (NoSuchMethodException exc)
				{
				}
			}

		// Constructor(int,int,...,int).
		if (allAreInts)
			{
			try
				{
				argtypes = new Class<?> [args.length];
				for (i = 0; i < args.length; ++ i)
					{
					argtypes[i] = Integer.TYPE;
					}
				ctor = theClass.getConstructor (argtypes);
				ctor.setAccessible (disableAccessChecks);
				return ctor.newInstance ((Object[]) intargs);
				}
			catch (NoSuchMethodException exc)
				{
				}
			}

		// Constructor(int[]).
		if (allAreInts)
			{
			try
				{
				ctor = theClass.getConstructor (int[].class);
				ctor.setAccessible (disableAccessChecks);
				return ctor.newInstance ((Object) intargs);
				}
			catch (NoSuchMethodException exc)
				{
				}
			}

		// Constructor(String,String,...,String).
		try
			{
			argtypes = new Class<?> [args.length];
			for (i = 0; i < args.length; ++ i)
				{
				argtypes[i] = String.class;
				}
			ctor = theClass.getConstructor (argtypes);
			ctor.setAccessible (disableAccessChecks);
			return ctor.newInstance ((Object[]) args);
			}
		catch (NoSuchMethodException exc)
			{
			}

		// Constructor(String[]).
		try
			{
			ctor = theClass.getConstructor (String[].class);
			ctor.setAccessible (disableAccessChecks);
			return ctor.newInstance ((Object) args);
			}
		catch (NoSuchMethodException exc)
			{
			}

		// Could not find suitable constructor.
		throw new NoSuchMethodException (String.format
			("Instance.newInstance(\"%s\"): Cannot find suitable constructor",
			 s));
		}

	/**
	 * Create a new instance of the class with the given name using the class's
	 * default constructor. Calling this method is equivalent to calling
	 * <TT>newDefaultInstance(className,false)</TT>. See the {@link
	 * #newDefaultInstance(String,boolean) newDefaultInstance(String,boolean)}
	 * method for further information.
	 * <P>
	 * <I>Note:</I> To find the class with the given name, the calling thread's
	 * context class loader is used.
	 *
	 * @param  className  Class name.
	 *
	 * @return  New instance.
	 *
	 * @exception  ClassNotFoundException
	 *     Thrown if the class with the given name cannot be found.
	 * @exception  NoSuchMethodException
	 *     Thrown if the given class does not have a default constructor.
	 * @exception  InstantiationException
	 *     Thrown if an instance cannot be created because the given class is an
	 *     interface or an abstract class.
	 * @exception  IllegalAccessException
	 *     Thrown if an instance cannot be created because the calling method
	 *     does not have access to the class or constructor.
	 * @exception  InvocationTargetException
	 *     Thrown if the constructor throws an exception.
	 */
	public static Object newDefaultInstance
		(String className)
		throws
			ClassNotFoundException,
			NoSuchMethodException,
			InstantiationException,
			IllegalAccessException,
			InvocationTargetException
		{
		return newDefaultInstance (className, false);
		}

	/**
	 * Create a new instance of the class with the given name using the class's
	 * default constructor.
	 * <P>
	 * If the <TT>disableAccessChecks</TT> argument is true, access checks are
	 * suppressed when constructing the new instance. This means the object's
	 * class and/or the class's default constructor need not be public, and a
	 * new instance will still be constructed. However, this also requires that
	 * either (a) a security manager is not installed, or (b) the security
	 * manager allows ReflectPermission("suppressAccessChecks"). See the
	 * <TT>java.lang.reflect.Constructor.setAccessible()</TT> method for further
	 * information.
	 * <P>
	 * <I>Note:</I> To find the class with the given name, the calling thread's
	 * context class loader is used.
	 *
	 * @param  className  Class name.
	 * @param  disableAccessChecks
	 *     True to disable access checks, false to perform access checks.
	 *
	 * @return  New instance.
	 *
	 * @exception  ClassNotFoundException
	 *     Thrown if the class with the given name cannot be found.
	 * @exception  NoSuchMethodException
	 *     Thrown if the given class does not have a default constructor.
	 * @exception  InstantiationException
	 *     Thrown if an instance cannot be created because the given class is an
	 *     interface or an abstract class.
	 * @exception  IllegalAccessException
	 *     Thrown if an instance cannot be created because the calling method
	 *     does not have access to the class or constructor.
	 * @exception  InvocationTargetException
	 *     Thrown if the constructor throws an exception.
	 */
	public static Object newDefaultInstance
		(String className,
		 boolean disableAccessChecks)
		throws
			ClassNotFoundException,
			NoSuchMethodException,
			InstantiationException,
			IllegalAccessException,
			InvocationTargetException
		{
		Class<?> theClass = Class.forName
			(className,
			 true,
			 Thread.currentThread().getContextClassLoader());
		return newDefaultInstance (theClass, disableAccessChecks);
		}

	/**
	 * Create a new instance of the given class using the class's default
	 * constructor. Calling this method is equivalent to calling
	 * <TT>newDefaultInstance(c,false)</TT>. See the {@link
	 * #newDefaultInstance(Class,boolean) newDefaultInstance(Class,boolean)}
	 * method for further information.
	 *
	 * @param  <T>  Class's data type.
	 * @param  c    Class.
	 *
	 * @return  New instance.
	 *
	 * @exception  NoSuchMethodException
	 *     Thrown if the given class does not have a default constructor.
	 * @exception  InstantiationException
	 *     Thrown if an instance cannot be created because the given class is an
	 *     interface or an abstract class.
	 * @exception  IllegalAccessException
	 *     Thrown if an instance cannot be created because the calling method
	 *     does not have access to the class or constructor.
	 * @exception  InvocationTargetException
	 *     Thrown if the constructor throws an exception.
	 */
	public static <T> T newDefaultInstance
		(Class<T> c)
		throws
			NoSuchMethodException,
			InstantiationException,
			IllegalAccessException,
			InvocationTargetException
		{
		return newDefaultInstance (c, false);
		}

	/**
	 * Create a new instance of the given class using the class's default
	 * constructor.
	 * <P>
	 * If the <TT>disableAccessChecks</TT> argument is true, access checks are
	 * suppressed when constructing the new instance. This means the object's
	 * class and/or the class's default constructor need not be public, and a
	 * new instance will still be constructed. However, this also requires that
	 * either (a) a security manager is not installed, or (b) the security
	 * manager allows ReflectPermission("suppressAccessChecks"). See the
	 * <TT>java.lang.reflect.Constructor.setAccessible()</TT> method for further
	 * information.
	 *
	 * @param  <T>  Class's data type.
	 * @param  c    Class.
	 * @param  disableAccessChecks
	 *     True to disable access checks, false to perform access checks.
	 *
	 * @return  New instance.
	 *
	 * @exception  NoSuchMethodException
	 *     Thrown if the given class does not have a default constructor.
	 * @exception  InstantiationException
	 *     Thrown if an instance cannot be created because the given class is an
	 *     interface or an abstract class.
	 * @exception  IllegalAccessException
	 *     Thrown if an instance cannot be created because the calling method
	 *     does not have access to the class or constructor.
	 * @exception  InvocationTargetException
	 *     Thrown if the constructor throws an exception.
	 */
	public static <T> T newDefaultInstance
		(Class<T> c,
		 boolean disableAccessChecks)
		throws
			NoSuchMethodException,
			InstantiationException,
			IllegalAccessException,
			InvocationTargetException
		{
		return
			((Constructor<T>)(getDefaultConstructor (c, disableAccessChecks)))
				.newInstance();
		}

	/**
	 * Get the given class's default constructor. Calling this method is
	 * equivalent to calling <TT>getDefaultConstructor(c,false)</TT>. See the
	 * {@link #getDefaultConstructor(Class,boolean)
	 * getDefaultConstructor(Class,boolean)} method for further information.
	 *
	 * @param  c  Class.
	 *
	 * @return  Default (no-argument) constructor for the class.
	 *
	 * @exception  NoSuchMethodException
	 *     Thrown if the class does not have a default constructor.
	 */
	public static Constructor<?> getDefaultConstructor
		(Class<?> c)
		throws NoSuchMethodException
		{
		return getDefaultConstructor (c, false);
		}

	/**
	 * Get the given class's default constructor.
	 * <P>
	 * If the <TT>disableAccessChecks</TT> argument is true, access checks are
	 * suppressed when constructing an instance using the returned constructor.
	 * This means the object's class and/or the class's default constructor need
	 * not be public, and a new instance will still be constructed. However,
	 * this also requires that either (a) a security manager is not installed,
	 * or (b) the security manager allows
	 * ReflectPermission("suppressAccessChecks"). See the
	 * <TT>java.lang.reflect.Constructor.setAccessible()</TT> method for further
	 * information.
	 *
	 * @param  c  Class.
	 * @param  disableAccessChecks  True to disable access checks, false to
	 *                              perform access checks.
	 *
	 * @return  Default (no-argument) constructor for the class.
	 *
	 * @exception  NoSuchMethodException
	 *     Thrown if the class does not have a default constructor.
	 */
	public static Constructor<?> getDefaultConstructor
		(Class<?> c,
		 boolean disableAccessChecks)
		throws NoSuchMethodException
		{
		for (Constructor<?> ctor : c.getDeclaredConstructors())
			if (ctor.getParameterTypes().length == 0)
				{
				ctor.setAccessible (disableAccessChecks);
				return ctor;
				}
		throw new NoSuchMethodException (String.format
			("No such method: %s.<init>()", c.getName()));
		}

// Hidden operations.

	/**
	 * Throw an exception indicating a syntax error.
	 */
	private static void syntaxError
		(String s,
		 int i)
		{
		throw new IllegalArgumentException (String.format
			("Instance.newInstance(): Syntax error in \"%s<<<%s\" at <<<",
			 s.substring (0, i + 1), s.substring (i + 1)));
		}

// Unit test main program.

//	/**
//	 * Unit test main program.
//	 */
//	public static void main
//		(String[] args)
//		throws Exception
//		{
//		System.out.println (Instance.newInstance (args[0]));
//		}

	}
