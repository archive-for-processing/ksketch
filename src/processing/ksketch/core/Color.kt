/*
  Part of KSketch

  Copyright (c) 2018 Richard Patel

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

import kotlin.math.roundToInt

inline val Long.argb: PColor
	get() = this.toInt()

// rgba to argb
inline val Long.rgba: PColor
	get() {
		val v = this.toInt()
		return (
			((v and 0xFF) shl 24) or   // a
			(((v ushr 8) and 0xFFFFFF)) // rgb
		)
	}

// rgb to argb
inline val Long.rgb: PColor
	get() {
		val v = this.toInt()
		return (
			(0xFF shl 24) or // a
			(v and 0xFFFFFF) // rgb
		)
	}

fun color(s: Float): PColor {
	val alpha = 0xFF shl 24
	val comp  = (255 * s).clamp(0f, 255f).roundToInt()
	return alpha or (comp shl 16) or (comp shl 8) or comp
}

fun color(x: Float, y: Float, z: Float): PColor {
	val alpha = 0xFF shl 24
	val red   = (255 * x).clamp(0f, 255f).roundToInt() shl 16
	val green = (255 * y).clamp(0f, 255f).roundToInt() shl 8
	val blue  = (255 * z).clamp(0f, 255f).roundToInt()
	return alpha or red or green or blue
}

fun color(x: Float, y: Float, z: Float, a: Float): PColor {
	val alpha = (255 * a).clamp(0f, 255f).roundToInt() shl 24
	val red   = (255 * x).clamp(0f, 255f).roundToInt() shl 16
	val green = (255 * y).clamp(0f, 255f).roundToInt() shl 8
	val blue  = (255 * z).clamp(0f, 255f).roundToInt()
	return alpha or red or green or blue
}
