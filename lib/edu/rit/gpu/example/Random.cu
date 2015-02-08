//******************************************************************************
//
// File:    Random.cu
// Package: edu.rit.gpu.example
// Unit:    Random device functions
//
// This C/CUDA source file is copyright (C) 2014 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This C/CUDA source file is part of the Parallel Java 2 Library ("PJ2"). PJ2
// is free software; you can redistribute it and/or modify it under the terms of
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

#ifndef __RANDOM_CU_INCLUDED__
#define __RANDOM_CU_INCLUDED__

//------------------------------------------------------------------------------
// This file contains CUDA functions for a pseudorandom number generator (PRNG).
// This file is intended to be #included into a program source file.

//------------------------------------------------------------------------------
// EXPORTED DATA TYPES

/**
 * Pseudorandom number generator (PRNG) data type.
 */
typedef struct
	{
	unsigned long long int counter;
	}
	prng_t;

//------------------------------------------------------------------------------
// HIDDEN CONSTANTS

#define prng_A 3935559000370003845ULL
#define prng_B 2691343689449507681ULL
#define prng_C 4768777513237032717ULL

// 2^{-64}
#define prng_TWO_SUP_MINUS_64 (1.0/18446744073709551616.0)

//------------------------------------------------------------------------------
// HIDDEN OPERATIONS

/**
 * Return the hash of the given value.
 */
__host__ __device__ unsigned long long int prngHash
	(unsigned long long int x)
	{
	x = prng_A*x + prng_B;
	x = x ^ (x >> 21);
	x = x ^ (x << 37);
	x = x ^ (x >> 4);
	x = prng_C*x;
	x = x ^ (x << 20);
	x = x ^ (x >> 41);
	x = x ^ (x << 5);
	return x;
	}

/**
 * Return the next 64-bit value in this PRNG's sequence.
 */
__host__ __device__ unsigned long long int prngNext
	(prng_t *prng)
	{
	return prngHash (++ prng->counter);
	}

//------------------------------------------------------------------------------
// EXPORTED OPERATIONS

/**
 * Set this PRNG's seed.
 *
 * @param  prng  Pointer to PRNG.
 * @param  seed  Seed.
 */
__host__ __device__ void prngSetSeed
	(prng_t *prng,
	 unsigned long long int seed)
	{
	prng->counter = prngHash (seed);
	}

/**
 * Skip this PRNG the given number of positions ahead in this PRNG's sequence.
 * If n = 0, nothing happens.
 *
 * @param  prng  Pointer to PRNG.
 * @param  n     Number of positions to skip.
 */
__host__ __device__ void prngSkip
	(prng_t *prng,
	 unsigned long long int n)
	{
	prng->counter += n;
	}

/**
 * Return the Boolean value from the next pseudorandom value in this PRNG's
 * sequence. With a probability of 0.5 true (1) is returned, with a probability
 * of 0.5 false (0) is returned.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Boolean value.
 */
__host__ __device__ int prngNextBoolean
	(prng_t *prng)
	{
	// Use the high-order bit of the 64-bit random value.
	return prngNext (prng) >> 63;
	}

/**
 * Return the byte value from the next pseudorandom value in this PRNG's
 * sequence. Each value in the range -128 through 127 is returned with a
 * probability of 1/2^8.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Byte value.
 */
__host__ __device__ int8_t prngNextByte
	(prng_t *prng)
	{
	return (int8_t) prngNext (prng);
	}

/**
 * Return the unsigned byte value from the next pseudorandom value in this
 * PRNG's sequence. Each value in the range 0 through 255 is returned with a
 * probability of 1/2^8.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Unsigned byte value.
 */
__host__ __device__ u_int8_t prngNextUnsignedByte
	(prng_t *prng)
	{
	return (u_int8_t) prngNext (prng);
	}

/**
 * Return the short value from the next pseudorandom value in this PRNG's
 * sequence. Each value in the range -32768 through 32767 is returned with a
 * probability of 1/2^16.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Short value.
 */
__host__ __device__ int16_t prngNextShort
	(prng_t *prng)
	{
	return (int16_t) prngNext (prng);
	}

/**
 * Return the unsigned short value from the next pseudorandom value in this
 * PRNG's sequence. Each value in the range 0 through 65535 is returned with a
 * probability of 1/2^16.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Unsigned short value.
 */
__host__ __device__ u_int16_t prngNextUnsignedShort
	(prng_t *prng)
	{
	return (u_int16_t) prngNext (prng);
	}

/**
 * Return the integer value from the next pseudorandom value in this PRNG's
 * sequence. Each value in the range -2147483648 through 2147483647 is returned
 * with a probability of 1/2^32.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Integer value.
 */
__host__ __device__ int prngNextInteger
	(prng_t *prng)
	{
	return (int) prngNext (prng);
	}

/**
 * Return the unsigned integer value from the next pseudorandom value in this
 * PRNG's sequence. Each value in the range 0 through 4294967296 is returned
 * with a probability of 1/2^32.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Unsigned integer value.
 */
__host__ __device__ unsigned int prngNextUnsignedInteger
	(prng_t *prng)
	{
	return (unsigned int) prngNext (prng);
	}

/**
 * Return the long value from the next pseudorandom value in this PRNG's
 * sequence. Each value in the range -9223372036854775808 through
 * 9223372036854775807 is returned with a probability of 1/2^64.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Long value.
 */
__host__ __device__ long long int prngNextLong
	(prng_t *prng)
	{
	return (long long int) prngNext (prng);
	}

/**
 * Return the unsigned long value from the next pseudorandom value in this
 * PRNG's sequence. Each value in the range 0 through 18446744073709551615 is
 * returned with a probability of 1/2^64.
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Unsigned long value.
 */
__host__ __device__ unsigned long long int prngNextUnsignedLong
	(prng_t *prng)
	{
	return prngNext (prng);
	}

/**
 * Return the double precision floating point value from the next pseudorandom
 * value in this PRNG's sequence. The returned numbers have a uniform
 * distribution in the range 0.0 (inclusive) to 1.0 (exclusive).
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Double value.
 */
__host__ __device__ double prngNextDouble
	(prng_t *prng)
	{
	return ((double) prngNext (prng))*prng_TWO_SUP_MINUS_64;
	}

/**
 * Return the single precision floating point value from the next pseudorandom
 * value in this PRNG's sequence. The returned numbers have a uniform
 * distribution in the range 0.0 (inclusive) to 1.0 (exclusive).
 *
 * @param  prng  Pointer to PRNG.
 *
 * @return  Float value.
 */
__host__ __device__ float prngNextFloat
	(prng_t *prng)
	{
	return (float) prngNextDouble (prng);
	}

/**
 * Return the integer value in the given range from the next pseudorandom value
 * in this PRNG's sequence. Each value in the range 0 through n-1 is returned
 * with a probability of 1/n.
 *
 * @param  prng  Pointer to PRNG.
 * @param  n     Range of values to return.
 *
 * @return  Integer value in the range 0 through n-1 inclusive.
 */
__host__ __device__ int prngNextInt
	(prng_t *prng,
	 int n)
	{
	return (int) (prngNextDouble (prng) * n);
	}

#endif
