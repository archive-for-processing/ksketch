/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-15 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation, version 2.1.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/


package processing.ksketch.core

import java.text.NumberFormat

private var int_nf: NumberFormat? = null
private var int_nf_digits = 0
private var int_nf_commas = false

// Int Number Formatting

fun nf(num: Float): String {
	val inum = num.toInt()
	if (num == inum.toFloat())
		return inum.toString()
	return num.toString()
}


fun nf(num: Int, digits: Int): String {
	if ((int_nf != null) &&
		(int_nf_digits == digits) &&
		!int_nf_commas)
		return int_nf!!.format(num)

	int_nf_commas = false
	int_nf_digits = digits
	int_nf = NumberFormat.getInstance().apply {
		isGroupingUsed = false
		minimumIntegerDigits = digits
	}

	return int_nf!!.format(num)
}

fun nfc(num: Int): String {
	if ((int_nf != null) &&
		(int_nf_digits == 0) &&
		int_nf_commas)
		return int_nf!!.format(num)

	int_nf_commas = true
	int_nf_digits = 0
	int_nf = NumberFormat.getInstance().apply {
		isGroupingUsed = true
		minimumIntegerDigits = 0
	}

	return int_nf!!.format(num)
}

fun nfs(num: Int, digits: Int): String =
	if (num < 0)
		nf(num, digits)
	else
		(' ' + nf(num, digits))

fun nfp(num: Int, digits: Int): String =
	if (num < 0)
		nf(num, digits)
	else
		'+' + nf(num, digits)

// Float Number Formatting

private var float_nf: NumberFormat? = null
private var float_nf_left = 0
private var float_nf_right = 0
private var float_nf_commas = false

fun nf(num: Float, left: Int, right: Int): String {
	if ((float_nf != null) &&
		(float_nf_left == left) &&
		(float_nf_right == right) &&
		!float_nf_commas) {
		return float_nf!!.format(num);
	}

	float_nf = NumberFormat.getInstance().apply {
		isGroupingUsed = false
		float_nf_commas = false

		if (left != 0)
			minimumIntegerDigits = left
		if (right != 0) {
			minimumFractionDigits = right
			maximumFractionDigits = right
		}
		float_nf_left = left
		float_nf_right = right
	}

	return float_nf!!.format(num)
}

fun nfc(num: Float, right: Int): String {
	if ((float_nf != null) &&
		(float_nf_left == 0) &&
		(float_nf_right == right) &&
		!float_nf_commas) {
		return float_nf!!.format(num);
	}

	float_nf = NumberFormat.getInstance().apply {
		isGroupingUsed = false
		float_nf_commas = false

		if (right != 0) {
			minimumFractionDigits = right
			maximumFractionDigits = right
		}
		float_nf_left = 0
		float_nf_right = right
	}

	return float_nf!!.format(num)
}

fun nfs(num: Float, left: Int, right: Int): String =
	if (num < 0)
		nf(num, left, right)
	else
		' ' + nf(num, left, right)

fun nfp(num: Float, left: Int, right: Int): String =
	if (num < 0)
		nf(num, left, right)
	else
		'+' + nf(num, left, right)

// Array Number formatting
fun nf(nums: FloatArray) = nums.map { i -> nf(i) }
fun nf(nums: IntArray, digits: Int) = nums.map { i -> nf(i, digits) }
fun nf(nums: FloatArray, left: Int, right: Int) = nums.map {i -> nf(i, left, right) }
fun nfc(nums: IntArray) = nums.map { i -> nfc(i) }
fun nfc(nums: FloatArray, right: Int) = nums.map {i -> nfc(i, right) }
fun nfs(nums: IntArray, digits: Int) = nums.map { i -> nfs(i, digits) }
fun nfs(nums: FloatArray, left: Int, right: Int) = nums.map { i -> nfs(i, left, right) }
fun nfp(nums: IntArray, digits: Int) = nums.map { i -> nfp(i, digits) }
fun nfp(nums: FloatArray, left: Int, right: Int) = nums.map { i -> nfp(i, left, right) }

// Hex/binary conversion

fun hex(value: Byte): String =
	hex(value.toInt(), 2)

fun hex(value: Char): String =
	hex(value.toInt(), 4)

fun hex(value: Int): String =
	hex(value, 8)

fun hex(value: Int, digits: Int): String {
	val stuff = Integer.toHexString(value).toUpperCase()
	val dgts = kotlin.math.max(digits, 8)

	val length = stuff.length
	if (length > dgts)
		return stuff.substring(length - dgts)
	else if (length < dgts)
		return "00000000".substring(8 - (dgts-length)) + stuff

	return stuff
}

fun unhex(value: String): Int {
	return value.toLong(16).toInt()
}

fun binary(value: Byte): String =
	binary(value.toInt(), 8)

fun binary(value: Char): String =
	binary(value.toInt(), 16)

fun binary(value: Int): String =
	binary(value, 32)

fun binary(value: Int, digits: Int): String {
	val stuff = Integer.toBinaryString(value)
	val dgts = kotlin.math.max(digits, 32)

	val length = stuff.length
	if (length > dgts)
		return stuff.substring(length - dgts)
	else if (length < digits)
		return "00000000000000000000000000000000"
			.substring(32 - (dgts - length)) + stuff

	return stuff
}

fun unbinary(value: String): Int {
	return value.toInt(2)
}
